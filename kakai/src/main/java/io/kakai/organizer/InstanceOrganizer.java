package io.kakai.organizer;

import io.kakai.Kakai;
import io.kakai.annotate.*;
import io.kakai.model.DependencyElement;
import io.kakai.resources.Resources;
import io.kakai.model.Event;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class InstanceOrganizer {

    Kakai kakai;
    Resources resources;
    ClassLoader classLoader;
    List<String> jarDeps;
    Map<String, DependencyElement> objects;

    public InstanceOrganizer(Kakai kakai){
        this.kakai = kakai;
        this.resources = new Resources();
        this.objects = new HashMap<>();
        this.classLoader = Thread.currentThread().getContextClassLoader();
    }

    public InstanceOrganizer run() {
        if (resources.isJar()) {
            setJarDeps();
            getClassesJar();
        }else{
            String uri = null;
            try {
                uri = resources.getClassesUri();
            } catch (Exception e) {
                e.printStackTrace();
            }
            getClasses(uri);
        }
        return this;
    }


    private List<String> setJarDeps(){
        jarDeps = new ArrayList<>();

        Enumeration<JarEntry> entries = resources.getJarEntries();

        do{

            JarEntry jarEntry = entries.nextElement();
            String path = getPath(jarEntry.toString());

            if(!path.contains("META-INF.maven."))continue;

            String dep = path.substring(14);
            jarDeps.add(dep);

        }while(entries.hasMoreElements());


        return jarDeps;
    }

    protected boolean isDep(String jarEntry){
        String jarPath = getPath(jarEntry);
        for(String dep : jarDeps){
            if(jarPath.contains(dep))return true;
        }
        return false;
    }

    //Thank you walen
    private int getLastIndxOf(int nth, String ch, String string) {
        if (nth <= 0) return string.length();
        return getLastIndxOf(--nth, ch, string.substring(0, string.lastIndexOf(ch)));
    }

    protected Boolean isWithinRunningProgram(String jarEntry){
        String main = resources.getMain();
        String path = main.substring(0, getLastIndxOf(1, ".", main) + 1);
        String jarPath = getPath(jarEntry);
        return jarPath.contains(path) ? true : false;
    }

    protected Boolean isDirt(String jarEntry){
        if(resources.isJar() &&
                !isWithinRunningProgram(jarEntry) &&
                    isDep(jarEntry))return true;

        if(resources.isJar() && !jarEntry.endsWith(".class"))return true;
        if(jarEntry.contains("org/h2"))return true;
        if(jarEntry.contains("javax/servlet/http"))return true;
        if(jarEntry.contains("package-info"))return true;
        if(jarEntry.startsWith("module-info"))return true;
        if(jarEntry.contains("META-INF/"))return true;
        if(jarEntry.contains("$"))return true;
        if(jarEntry.endsWith("Exception"))return true;

        return false;
    }

    protected void getClassesJar(){
        try {

            URL jarUriTres = this.classLoader.getResource("io/kakai/");//was 5
            String jarPath = jarUriTres.getPath().substring(5, jarUriTres.getPath().indexOf("!"));

            JarFile file = new JarFile(jarPath);
            Enumeration jarFile = file.entries();

            while (jarFile.hasMoreElements()) {
                JarEntry jarEntry = (JarEntry) jarFile.nextElement();

                if(jarEntry.isDirectory()){
                    continue;
                }

                if(isDirt(jarEntry.toString()))continue;

                String path = getPath(jarEntry.toString());
                Class<?> klass = classLoader.loadClass(path);

                if (klass.isAnnotation() ||
                        klass.isInterface()) {
                    continue;
                }

                if(klass.isAnnotationPresent(io.kakai.annotate.Application.class)){
                    io.kakai.annotate.Application application = klass.getAnnotation(io.kakai.annotate.Application.class);
                    kakai.setApplication(application);
                }

                if(klass.isAnnotationPresent(io.kakai.annotate.StartupEvent.class)){
                    Object object = getObject(klass);
                    Event event = new Event(object);
                    kakai.setStartupEvent(event);
                }

                DependencyElement dependencyElement = getObjectDetails(klass);
                kakai.getObjects().put(dependencyElement.getName(), dependencyElement);

            }

        }catch (Exception ex){ex.printStackTrace();}
    }


    protected void getClasses(String uri){
        File pathFile = new File(uri);

        File[] files = pathFile.listFiles();
        for (File file : files) {

            if (file.isDirectory()) {
                getClasses(file.getPath());
                continue;
            }

            try {

                if(isDirt(file.getPath()))continue;
                if(!file.getPath().endsWith(".class"))continue;

                String path = getPath("class", "classes", file.getPath());
                Class<?> klass = classLoader.loadClass(path);

                if (klass.isAnnotation() ||
                        klass.isInterface() ||
                        (klass.getName() == this.getClass().getName())) {
                    continue;
                }

                if(klass.isAnnotationPresent(StartupEvent.class)){
                    Object object = getObject(klass);
                    Event event = new Event(object);
                    kakai.setStartupEvent(event);
                }

                if(klass.isAnnotationPresent(io.kakai.annotate.Application.class)){
                    io.kakai.annotate.Application application = klass.getAnnotation(io.kakai.annotate.Application.class);
                    kakai.setApplication(application);
                }

                if(klass.isAnnotationPresent(io.kakai.annotate.Element.class) ||
                        klass.isAnnotationPresent(io.kakai.annotate.Configuration.class) ||
                        klass.isAnnotationPresent(io.kakai.annotate.Config.class) ||
                        klass.isAnnotationPresent(io.kakai.annotate.Repo.class) ||
                        klass.isAnnotationPresent(io.kakai.annotate.Router.class) ||
                        klass.isAnnotationPresent(io.kakai.annotate.Persistence.class) ||
                        klass.isAnnotationPresent(io.kakai.annotate.Service.class)) {
                    DependencyElement dependencyElement = getObjectDetails(klass);
                    kakai.getObjects().put(dependencyElement.getName(), dependencyElement);
                }

            }catch (Exception ex){
                ex.printStackTrace();
            }
        }

    }

    protected String getPath(String path){
        if(path.startsWith("/"))path = path.replaceFirst("/", "");
        return path
                .replace("\\", ".")
                .replace("/",".")
                .replace(".class", "");
    }

    protected String getPath(String name, String key, String beginPath){
        String separator = System.getProperty("file.separator");
        String regex = key + "\\" + separator;
        String[] pathParts = beginPath.split(regex);
        String pathPre =  pathParts[1]
                .replace("\\", ".")
                .replace("/",".")
                .replace("."+ name, "");
        String path = pathPre.replaceFirst("java.", "").replaceFirst("main.", "");
        if(path.startsWith("test"))path = path.replaceFirst("test.", "");
        return path;
    }

    protected DependencyElement getObjectDetails(Class<?> klass) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        DependencyElement dependencyElement = new DependencyElement();
        dependencyElement.setKlass(klass);
        dependencyElement.setName(resources.getName(klass.getName()));
        Object object = getObject(klass);
        dependencyElement.setObject(object);
        return dependencyElement;
    }

    protected Object getObject(Class<?> klass) {

        try {
            Object object = klass.getConstructor().newInstance();
            return object;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        return null;
    }

}
