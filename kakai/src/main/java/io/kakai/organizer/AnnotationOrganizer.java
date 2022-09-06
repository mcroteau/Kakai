package io.kakai.organizer;

import io.kakai.Kakai;
import io.kakai.annotate.*;
import io.kakai.model.DependencyElement;
import io.kakai.resources.Resources;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnnotationOrganizer {

    Kakai kakai;
    Resources resources;
    Map<String, DependencyElement> processed;
    List<DependencyElement> annotations;

    public AnnotationOrganizer(Kakai kakai){
        this.kakai = kakai;
        this.resources = new Resources();
        this.processed = new HashMap<>();
        this.annotations = new ArrayList<>();
        this.mapEm();
    }

    public void run() throws Exception{
        while(!allAnnotationsProcessed()){
            processAnnotations(0);
            break;
        }
    }

    private void processAnnotations(int idx) throws Exception {

        if(idx > annotations.size())idx = 0;

        for(Integer z = idx; z < annotations.size(); z++){
            DependencyElement dependencyElement = annotations.get(z);
            Integer fieldsCount = getAnnotatedFieldsCount(dependencyElement.getKlass());
            Integer processedFieldsCount = 0;

            Object object = dependencyElement.getObject();
            Field[] fields = dependencyElement.getKlass().getDeclaredFields();

            for(Field field: fields) {
                if(field.isAnnotationPresent(Bind.class) ||
                        field.isAnnotationPresent(Set.class)) {

                    String fieldKey = field.getName().toLowerCase();
                    if(kakai.getElementStorage().getElements().containsKey(fieldKey)){
                        Object element = kakai.getElementStorage().getElements().get(fieldKey).getElement();
                        field.setAccessible(true);
                        field.set(object, element);
                        processedFieldsCount++;
                    }else{
                        processAnnotations(z + 1);
                    }
                }
                if(field.isAnnotationPresent(Property.class)){
                    Property annotation = field.getAnnotation(Property.class);
                    String key = annotation.value();

                    if(kakai.getPropertyStorage().getProperties().containsKey(key)){
                        field.setAccessible(true);
                        String value = kakai.getPropertyStorage().getProperties().get(key);
                        attachValue(field, object, value);
                        processedFieldsCount++;
                    }else{
                        processAnnotations(z + 1);
                        throw new Exception(field.getName() + " is missing on " + object.getClass().getName());
                    }
                }
            }

            if(fieldsCount !=
                    processedFieldsCount){
                processAnnotations( z + 1);
            }else{
                String key = resources.getName(dependencyElement.getName());
                processed.put(key, dependencyElement);
            }
        }
    }

    protected void attachValue(Field field, Object object, String stringValue) throws Exception{
        Type type = field.getType();
        if(type.getTypeName().equals("java.lang.String")){
            field.set(object, stringValue);
        }
        if(type.getTypeName().equals("boolean") || type.getTypeName().equals("java.lang.Boolean")){
            Boolean value = Boolean.valueOf(stringValue);
            field.set(object, value);
        }
        if(type.getTypeName().equals("int") || type.getTypeName().equals("java.lang.Integer")){
            Integer value = Integer.valueOf(stringValue);
            field.set(object, value);
        }
        if(type.getTypeName().equals("float") || type.getTypeName().equals("java.lang.Float")){
            Float value = Float.valueOf(stringValue);
            field.set(object, value);
        }
        if(type.getTypeName().equals("double") || type.getTypeName().equals("java.lang.Double")){
            Double value = Double.valueOf(stringValue);
            field.set(object, value);
        }
        if(type.getTypeName().equals("java.math.BigDecimal")){
            BigDecimal value = new BigDecimal(stringValue);
            field.set(object, value);
        }
    }

    protected Integer getAnnotatedFieldsCount(Class<?> klass) throws Exception{
        Integer count = 0;
        Field[] fields = klass.getDeclaredFields();
        for(Field field: fields){
            if(field.isAnnotationPresent(Bind.class)){
                count++;
            }
            if(field.isAnnotationPresent(Set.class)){
                count++;
            }
            if(field.isAnnotationPresent(Property.class)){
                count++;
            }
        }
        return count;
    }

    private void mapEm(){
        for(Map.Entry<String, DependencyElement> entry: kakai.getElementProcessor().getAnnotatedClasses().entrySet()){
            DependencyElement dependencyElement = entry.getValue();
            if(!annotations.contains(dependencyElement))annotations.add(dependencyElement);
        }
    }

    protected Boolean allAnnotationsProcessed(){
        return this.processed.size() == kakai.getElementProcessor().getAnnotatedClasses().size();
    }
}
