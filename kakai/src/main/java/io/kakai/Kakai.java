package io.kakai;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import io.kakai.jdbc.DatabaseMediator;
import io.kakai.model.DependencyElement;
import io.kakai.model.Element;
import io.kakai.model.Event;
import io.kakai.model.web.EndpointMappings;
import io.kakai.model.web.HttpRequest;
import io.kakai.organizer.*;
import io.kakai.repository.ElementRepository;
import io.kakai.repository.ObjectRepository;
import io.kakai.repository.PropertyRepository;
import io.kakai.resources.Environments;
import io.kakai.resources.MimeGetter;
import io.kakai.resources.Settings;
import io.kakai.resources.Resources;
import io.kakai.web.ExperienceProcessor;
import io.kakai.web.RequestHandler;
import io.kakai.implement.RequestNegotiator;
import io.kakai.implement.ViewRenderer;

import javax.sql.DataSource;
import java.io.*;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.file.Files;
import java.sql.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import static io.kakai.resources.Project.*;

public class Kakai {

    Logger Log = Logger.getLogger(Kakai.class.getName());

    int port;
    String flag;
    String propertyFile;
    Resources resources;
    DataSource dataSource;
    Settings settings;
    HttpServer httpServer;
    Map<String, ViewRenderer> viewRenderers;
    Map<String, RequestNegotiator> requestNegotiators;

    Event startupEvent;
    ExecutorService executorService;

    ObjectRepository objectRepository;
    PropertyRepository propertyRepository;
    ElementRepository elementRepository;

    ExperienceProcessor experienceProcessor;
    EndpointOrganizer endpointOrganizer;
    ElementOrganizer elementOrganizer;
    EndpointMappings endpointMappings;

    public Kakai(int port) {
        try {
            this.port = port;
            this.resources = new Resources();
            this.viewRenderers = new HashMap<>();
            this.requestNegotiators = new HashMap<>();
            this.objectRepository = new ObjectRepository();
            this.propertyRepository = new PropertyRepository();
            this.elementRepository = new ElementRepository();
            this.executorService = Executors.newFixedThreadPool(NUMBER_THREADS);
            httpServer = HttpServer.create(new InetSocketAddress(port), 0);
            httpServer.setExecutor(executorService);
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }

    public Kakai start(){
        try {
            ExperienceProcessor experienceProcessor = new ExperienceProcessor();
            setExperienceProcessor(experienceProcessor);

            RequestHandler requestHandler = new RequestHandler(this);

            List<String> resources = new ArrayList<>();
            resources.add(WEB_ASSETS_DIRECTORY);

            List<String> propertiesFiles = new ArrayList<>();
            propertiesFiles.add(PROPERTY_FILE);

            this.settings = new Settings();
            this.settings.setResources(resources);
            this.settings.setPropertiesFiles(propertiesFiles);

            new PropertyOrganizer(this).run();

            InstanceOrganizer instanceOrganizer = new InstanceOrganizer(this);
            instanceOrganizer.run();

            ElementOrganizer elementsProcessor = new ElementOrganizer(this);
            elementsProcessor.run();
            setElementProcessor(elementsProcessor);

            if (getElementProcessor().getConfigs() != null &&
                    getElementProcessor().getConfigs().size() > 0) {
                ConfigOrganizer configOrganizer = new ConfigOrganizer(this);
                configOrganizer.run();
            }

            Element kakaiElement = new Element();
            kakaiElement.setElement(this);
            getElementStorage().getElements().put(KAKAI_DEPENDENCY, kakaiElement);

            if(this.getResources() == null) this.setResources(new ArrayList<>());

            AnnotationOrganizer annotationOrganizer = new AnnotationOrganizer(this);
            annotationOrganizer.run();

            EndpointOrganizer endpointOrganizer = new EndpointOrganizer(this);
            endpointOrganizer.run();

            EndpointMappings endpointMappings = endpointOrganizer.getMappings();
            setEndpointMappings(endpointMappings);

            fireStartupEvent();

            httpServer.createContext("/", requestHandler);
//            httpServer.createContext("/resources", new StaticFileHandler());
            httpServer.start();

            Log.info("Ready.");

        } catch(Exception ioe) {
            ioe.printStackTrace();
        }
        return this;
    }


    public class StaticFileHandler implements HttpHandler {

        String baseDir = "web-ux/";

        public void handle(HttpExchange ex) throws IOException {
            URI uri = ex.getRequestURI();
            String path = uri.getPath();
            File completeFilePath = new File(baseDir, path);

            Headers h = ex.getResponseHeaders();
            MimeGetter getter = new MimeGetter(uri.getPath());

            h.add("Content-Type", getter.resolve());

            OutputStream out = ex.getResponseBody();

            if (completeFilePath.exists()) {
                ex.sendResponseHeaders(200, completeFilePath.length());
                out.write(Files.readAllBytes(completeFilePath.toPath()));
            } else {
                System.err.println("File not found: " + completeFilePath.getAbsolutePath());
                ex.sendResponseHeaders(404, 0);
                out.write("404 File not found.".getBytes());
            }

            out.close();
        }
    }




    public Kakai stop(){
        httpServer.stop(0);
        return this;
    }

    void fireStartupEvent() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        if(startupEvent != null) {
            Method completeMethod = startupEvent.getEvent().getClass().getMethod(SETUP_COMPLETE, Kakai.class);
            completeMethod.setAccessible(true);
            completeMethod.invoke(startupEvent.getEvent(), this);
        }
    }

    public void setFlag(String flag){
        this.flag = flag;
    }

    public String getFlag(){
        return this.flag;
    }

    public ExperienceProcessor getExperienceProcessor(){
        return experienceProcessor;
    }

    void setExperienceProcessor(ExperienceProcessor experienceProcessor){
        this.experienceProcessor = experienceProcessor;
    }

    public Kakai addViewRenderer(ViewRenderer viewRenderer){
        String key = resources.getName(viewRenderer.getClass().getName());
        viewRenderers.put(key, viewRenderer);
        return this;
    }

    public Kakai addNegotiator(RequestNegotiator requestNegotiator){
        String key = resources.getName(requestNegotiator.getClass().getName());
        requestNegotiators.put(key, requestNegotiator);
        return this;
    }

    public Object getElement(String name){
        String key = name.toLowerCase();
        if(elementRepository.getElements().containsKey(key)){
            return elementRepository.getElements().get(key).getElement();
        }
        return null;
    }
    public Map<String, Element> getElements(){
        return this.elementRepository.getElements();
    }
    public ElementRepository getElementStorage() {
        return this.elementRepository;
    }
    public List<String> getResources() {
        return this.settings.getResources();
    }
    public void setResources(List<String> resources) {
        this.settings.setResources(resources);
    }
    public Map<String, DependencyElement> getObjects() {
        return this.objectRepository.getObjects();
    }
    public Map<String, RequestNegotiator> getInterceptors() {
        return requestNegotiators;
    }
    public Map<String, ViewRenderer> getViewRenderers() {
        return viewRenderers;
    }

    //todo:dine! then hopefully dance. thank you! i hope!
    public Event getStartupEvent() {
        return startupEvent;
    }

    public void setStartupEvent(Event startupEvent) {
        this.startupEvent = startupEvent;
    }

    public PropertyRepository getPropertyStorage() {
        return propertyRepository;
    }

    public void setPropertyStorage(PropertyRepository propertyRepository) {
        this.propertyRepository = propertyRepository;
    }

    public ElementOrganizer getElementProcessor() {
        return elementOrganizer;
    }

    public void setElementProcessor(ElementOrganizer elementOrganizer) {
        this.elementOrganizer = elementOrganizer;
    }

    public EndpointMappings getEndpointMappings() {
        return endpointMappings;
    }

    public void setEndpointMappings(EndpointMappings endpointMappings) {
        this.endpointMappings = endpointMappings;
    }




    public Object get(HttpRequest request, Class<?> cls){
        Object object =  null;
        try {
            object = cls.getConstructor().newInstance();
            Field[] fields = cls.getDeclaredFields();
            for(Field field : fields){
                String name = field.getName();
                String value = request.value(name);
                if(value != null &&
                        !value.equals("")){

                    field.setAccessible(true);

                    Type type = field.getType();

                    if (type.getTypeName().equals("int") ||
                            type.getTypeName().equals("java.lang.Integer")) {
                        field.set(object, Integer.valueOf(value));
                    }
                    if (type.getTypeName().equals("double") ||
                            type.getTypeName().equals("java.lang.Double")) {
                        field.set(object, Double.valueOf(value));
                    }
                    if (type.getTypeName().equals("float") ||
                            type.getTypeName().equals("java.lang.Float")) {
                        field.set(object, Float.valueOf(value));
                    }
                    if (type.getTypeName().equals("long") ||
                            type.getTypeName().equals("java.lang.Long")) {
                        field.set(object, Long.valueOf(value));
                    }
                    if (type.getTypeName().equals("boolean") ||
                            type.getTypeName().equals("java.lang.Boolean")) {
                        field.set(object, Boolean.valueOf(value));
                    }
                    if (type.getTypeName().equals("java.math.BigDecimal")) {
                        field.set(object, new BigDecimal(value));
                    }
                    if (type.getTypeName().equals("java.lang.String")) {
                        field.set(object, value);
                    }
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }

        return object;
    }

    public Kakai setDatasource(DataSource dataSource) {
        this.dataSource = dataSource;
        return this;
    }

    public Object get(String preSql, Object[] params, Class<?> cls){
        Object result = null;
        String sql = "";
        try {
            sql = hydrateSql(preSql, params);
            Connection connection = dataSource.getConnection();
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            if(rs.next()){
                result = extractData(rs, cls);
            }
            if(result == null){
                throw new Exception(cls + " not found using '" + sql + "'");
            }

            connection.commit();
            connection.close();

        } catch (SQLException ex) {
            System.out.println("bad sql grammar : " + sql);
            System.out.println("\n\n\n");
            ex.printStackTrace();
        } catch (Exception ex) {}

        return result;
    }

    public Integer getInt(String preSql, Object[] params){
        Integer result = null;
        String sql = "";
        try {
            sql = hydrateSql(preSql, params);
            Connection connection = dataSource.getConnection();
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            if(rs.next()){
                result = Integer.parseInt(rs.getObject(1).toString());
            }

            if(result == null){
                throw new Exception("no results using '" + sql + "'");
            }

            connection.commit();
            connection.close();

        } catch (SQLException ex) {
            System.out.println("bad sql grammar : " + sql);
            System.out.println("\n\n\n");
            ex.printStackTrace();
        } catch (Exception ex) {}

        return result;
    }

    public Long getLong(String preSql, Object[] params){
        Long result = null;
        String sql = "";
        try {
            sql = hydrateSql(preSql, params);
            Connection connection = dataSource.getConnection();
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            if(rs.next()){
                result = Long.parseLong(rs.getObject(1).toString());
            }

            if(result == null){
                throw new Exception("no results using '" + sql + "'");
            }

            connection.commit();
            connection.close();
        } catch (SQLException ex) {
            System.out.println("bad sql grammar : " + sql);
            System.out.println("\n\n\n");
            ex.printStackTrace();
        } catch (Exception ex) {}

        return result;
    }

    public boolean save(String preSql, Object[] params){
        try {
            String sql = hydrateSql(preSql, params);
            Connection connection = dataSource.getConnection();
            Statement stmt = connection.createStatement();
            stmt.execute(sql);
            connection.commit();
            connection.close();
        }catch(Exception ex){
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    public List<Object> getList(String preSql, Object[] params, Class<?> cls){
        List<Object> results = new ArrayList<>();
        try {
            String sql = hydrateSql(preSql, params);
            Connection connection = dataSource.getConnection();
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            results = new ArrayList<>();
            while(rs.next()){
                Object obj = extractData(rs, cls);
                results.add(obj);
            }
            connection.commit();
            connection.close();
        }catch(ClassCastException ccex){
            System.out.println("");
            System.out.println("Wrong Class type, attempted to cast the return data as a " + cls);
            System.out.println("");
            ccex.printStackTrace();
        }catch (Exception ex){ ex.printStackTrace(); }
        return results;
    }

    public boolean update(String preSql, Object[] params){
        try {
            String sql = hydrateSql(preSql, params);
            Connection connection = dataSource.getConnection();
            Statement stmt = connection.createStatement();
            Boolean rs = stmt.execute(sql);
            connection.commit();
            connection.close();
        }catch(Exception ex){
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean delete(String preSql, Object[] params){
        try {
            String sql = hydrateSql(preSql, params);

            Connection connection = dataSource.getConnection();
            Statement stmt = connection.createStatement();
            stmt.execute(sql);
            connection.commit();
            connection.close();
        }catch(Exception ex){
            return false;
        }
        return true;
    }


    protected String hydrateSql(String sql, Object[] params){
        for(Object object : params){
            if(object != null) {
                String parameter = object.toString();
                if (object.getClass().getTypeName().equals("java.lang.String")) {
                    parameter = parameter.replace("'", "''")
                            .replace("$", "\\$")
                            .replace("#", "\\#")
                            .replace("@", "\\@");
                }
                sql = sql.replaceFirst("\\[\\+\\]", parameter);
            }else{
                sql = sql.replaceFirst("\\[\\+\\]", "null");
            }
        }
        return sql;
    }

    protected Object extractData(ResultSet rs, Class<?> cls) throws Exception{
        Object object = new Object();
        Constructor[] constructors = cls.getConstructors();
        for(Constructor constructor: constructors){
            if(constructor.getParameterCount() == 0){
                object = constructor.newInstance();
            }
        }

        Field[] fields = object.getClass().getDeclaredFields();
        for(Field field: fields){
            field.setAccessible(true);
            String originalName = field.getName();
            String regex = "([a-z])([A-Z]+)";
            String replacement = "$1_$2";
            String name = originalName.replaceAll(regex, replacement).toLowerCase();
            Type type = field.getType();
            if (hasColumn(rs, name)) {
                if (type.getTypeName().equals("int") || type.getTypeName().equals("java.lang.Integer")) {
                    field.set(object, rs.getInt(name));
                } else if (type.getTypeName().equals("double") || type.getTypeName().equals("java.lang.Double")) {
                    field.set(object, rs.getDouble(name));
                } else if (type.getTypeName().equals("float") || type.getTypeName().equals("java.lang.Float")) {
                    field.set(object, rs.getFloat(name));
                } else if (type.getTypeName().equals("long") || type.getTypeName().equals("java.lang.Long")) {
                    field.set(object, rs.getLong(name));
                } else if (type.getTypeName().equals("boolean") || type.getTypeName().equals("java.lang.Boolean")) {
                    field.set(object, rs.getBoolean(name));
                } else if (type.getTypeName().equals("java.math.BigDecimal")) {
                    field.set(object, rs.getBigDecimal(name));
                } else if (type.getTypeName().equals("java.lang.String")) {
                    field.set(object, rs.getString(name));
                }
            }
        }
        return object;
    }

    public static boolean hasColumn(ResultSet rs, String columnName) throws SQLException {
        ResultSetMetaData rsmd = rs.getMetaData();
        for (int x = 1; x <= rsmd.getColumnCount(); x++) {
            if (columnName.equals(rsmd.getColumnName(x).toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    public void setApplication(io.kakai.annotate.Application application) throws Exception {
        String environment = application.value();
        if(environment.equals(Environments.DEVELOPMENT) ||
                environment.equals(Environments.TEST)){
            createDevelopmentDatabase();
        }
    }

    public Kakai createDevelopmentDatabase() throws Exception {
        DatabaseMediator databaseMediator = new DatabaseMediator(new Resources());
        databaseMediator.createDatabase();
        DataSource datasource = databaseMediator.getDevelopmentDatasource();
        setDatasource(datasource);
        return this;
    }
}
