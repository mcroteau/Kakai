package io.kakai.organizer;

import io.kakai.Kakai;
import io.kakai.model.DependencyElement;
import io.kakai.model.web.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EndpointOrganizer {

    public static final String GET    = "Get";
    public static final String POST   = "Post";
    public static final String DELETE = "Delete";

    Kakai kakai;

    Map<String, DependencyElement> processed;
    EndpointMappings endpointMappings;

    public EndpointOrganizer(Kakai kakai){
        this.kakai = kakai;
        this.processed = new HashMap<>();
        this.endpointMappings = new EndpointMappings();
    }

    public EndpointOrganizer run() throws Exception{
        while(!allAnnotationsProcessed()){
            processWebAnnotations();
        }
        return this;
    }

    private boolean allAnnotationsProcessed(){
        return this.processed.size() == kakai.getElementProcessor().getHttpClasses().size();
    }

    private void processWebAnnotations() throws Exception{
        for(Map.Entry<String, DependencyElement> entry : kakai.getElementProcessor().getHttpClasses().entrySet()){
            Class<?> klass = entry.getValue().getKlass();
            Method[] methods = klass.getDeclaredMethods();
            for(Method method: methods){

                if(method.isAnnotationPresent(io.kakai.annotate.http.Get.class)){
                    setGetMapping(method, entry.getValue());
                    processed.put(entry.getKey(), entry.getValue());
                }
                if(method.isAnnotationPresent(io.kakai.annotate.http.Post.class)){
                    setPostMapping(method, entry.getValue());
                    processed.put(entry.getKey(), entry.getValue());
                }
                if(method.isAnnotationPresent(io.kakai.annotate.http.Delete.class)){
                    setDeleteMapping(method, entry.getValue());
                    processed.put(entry.getKey(), entry.getValue());
                }
            }
        }
    }

    protected void setGetMapping(Method method, DependencyElement dependencyElement) throws Exception{
        io.kakai.annotate.http.Get get = method.getAnnotation(io.kakai.annotate.http.Get.class);
        String path = get.value();
        EndpointMapping mapping = new EndpointMapping();
        mapping.setVerb(GET);
        setBaseDetailsAdd(path, mapping, method, dependencyElement);
    }

    protected void setPostMapping(Method method, DependencyElement dependencyElement) throws Exception{
        io.kakai.annotate.http.Post post = method.getAnnotation(io.kakai.annotate.http.Post.class);
        String path = post.value();
        EndpointMapping mapping = new EndpointMapping();
        mapping.setVerb(POST);
        setBaseDetailsAdd(path, mapping, method, dependencyElement);
    }

    protected void setDeleteMapping(Method method, DependencyElement dependencyElement) throws Exception{
        io.kakai.annotate.http.Delete delete = method.getAnnotation(io.kakai.annotate.http.Delete.class);
        String path = delete.value();
        EndpointMapping mapping = new EndpointMapping();
        mapping.setVerb(DELETE);
        setBaseDetailsAdd(path, mapping, method, dependencyElement);
    }

    protected void setBaseDetailsAdd(String path, EndpointMapping mapping, Method method, DependencyElement dependencyElement) throws Exception{

        mapping.setTypeNames(new ArrayList<>());
        Type[] types = method.getGenericParameterTypes();
        for(Type type : types){
            mapping.getTypeNames().add(type.getTypeName());
        }

        List<TypeDetails> typeDetails = new ArrayList<>();
        Annotation[][] paramAnnotations = method.getParameterAnnotations();
        Class<?>[] paramTypes = method.getParameterTypes();
        for (int n = 0; n < paramAnnotations.length; n++) {
            for (Annotation a: paramAnnotations[n]) {
                if (a instanceof io.kakai.annotate.Variable) {
                    TypeDetails details = new TypeDetails();
                    details.setName(paramTypes[n].getTypeName());
                    details.setType(paramTypes[n].getTypeName());
                    typeDetails.add(details);
                }
            }
        }

//https://regex101.com/r/sYeDyN/1
//\/(get){1}\/[A-Za-z0-9]\/[A-Za-z0-9]\/[A-Za-z0-9]\/$

        StringBuilder regexPath = new StringBuilder();
        regexPath.append("\\/(");
        int count = 0;
        String[] parts = path.split("/");
        for(String part: parts){
            count++;
            if(!part.equals("")) {
                if (part.matches("(\\{[a-zA-Z]*\\})")) {
                    regexPath.append("(.*[A-Za-z0-9])");
                    mapping.getVariablePositions().add(count - 1);
                } else {
                    regexPath.append("(" + part.toLowerCase() + "){1}");
                }
                if (count < parts.length) {
                    regexPath.append("\\/");
                }
            }
        }
        regexPath.append(")$");

        mapping.setRegexedPath(regexPath.toString());
        mapping.setTypeDetails(typeDetails);
        mapping.setPath(path);
        mapping.setMethod(method);
        mapping.setClassDetails(dependencyElement);

        String key = mapping.getVerb().concat("-").concat(path);
        if(endpointMappings.contains(key)){
            throw new Exception("Request path + " + path + " exists multiple times.");
        }

        String[] bits = path.split("/");
        UrlBitFeatures urlBitFeatures = new UrlBitFeatures();
        List<UrlBit> urlBits = new ArrayList<>();
        for(String bit : bits){
            UrlBit urlBit = new UrlBit();
            if(bit.contains("{{") && bit.contains("}}")){
                urlBit.setVariable(true);
            }else{
                urlBit.setVariable(false);
            }
            urlBits.add(urlBit);
        }
        urlBitFeatures.setUrlBits(urlBits);
        mapping.setUrlBitFeatures(urlBitFeatures);

        endpointMappings.add(key, mapping);

    }

    public EndpointMappings getMappings() {
        return endpointMappings;
    }
}
