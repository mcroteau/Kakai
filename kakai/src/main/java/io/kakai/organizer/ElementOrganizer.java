package io.kakai.organizer;

import io.kakai.Kakai;
import io.kakai.annotate.*;
import io.kakai.model.DependencyElement;
import io.kakai.model.Element;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ElementOrganizer {

    Kakai kakai;
    Integer jdbcCount;
    Integer serviceCount;
    Integer elementCount;
    List<Class<?>> configs;
    Map<String, DependencyElement> httpClasses;
    Map<String, DependencyElement> annotatedClasses;

    public ElementOrganizer(Kakai kakai){
        this.kakai = kakai;
        jdbcCount = 0;
        serviceCount = 0;
        elementCount = 0;
        configs = new ArrayList<>();
        httpClasses = new HashMap<>();
        annotatedClasses = new HashMap<>();
    }

    public void run() {
        for (Map.Entry<String, DependencyElement> entry : kakai.getObjects().entrySet()) {
            Class<?> klass = entry.getValue().getKlass();
            if (klass.isAnnotationPresent(Configuration.class)) {
                configs.add(klass);
            }
        }
        for (Map.Entry<String, DependencyElement> entry : kakai.getObjects().entrySet()) {
            Class<?> klass = entry.getValue().getKlass();

            if (klass.isAnnotationPresent(io.kakai.annotate.Element.class)) {
                buildAddElement(entry);
                elementCount++;
            }
            if (klass.isAnnotationPresent(io.kakai.annotate.Repo.class) ||
                    klass.isAnnotationPresent(io.kakai.annotate.Persistence.class)) {
                buildAddElement(entry);
                jdbcCount++;
            }
            if (klass.isAnnotationPresent(Service.class)) {
                buildAddElement(entry);
                serviceCount++;
            }
            if (klass.isAnnotationPresent(Router.class)) {
                httpClasses.put(entry.getKey(), entry.getValue());
            }

            Field[] fields = klass.getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(Bind.class)) {
                    annotatedClasses.put(entry.getKey(), entry.getValue());
                }
                if (field.isAnnotationPresent(Property.class)) {
                    annotatedClasses.put(entry.getKey(), entry.getValue());
                }
            }
        }
    }

    public Map<String, DependencyElement> getHttpClasses(){
        return this.httpClasses;
    }

    public Map<String, DependencyElement> getAnnotatedClasses(){
        return this.annotatedClasses;
    }

    public List<Class<?>> getConfigs(){
        return this.configs;
    }

    protected void buildAddElement(Map.Entry<String, DependencyElement> entry){
        Element element = new Element();
        String key = entry.getKey();
        Object object = entry.getValue().getObject();
        element.setElement(object);
        kakai.getElementStorage().getElements().put(key, element);
    }
}
