package io.kakai.repository;

import io.kakai.model.DependencyElement;

import java.util.HashMap;
import java.util.Map;

public class ObjectRepository {

    Map<String, DependencyElement> objects;

    public ObjectRepository(){
        this.objects = new HashMap<>();
    }

    public Map<String, DependencyElement> getObjects() {
        return objects;
    }

    public void setObjects(Map<String, DependencyElement> objects) {
        this.objects = objects;
    }
}
