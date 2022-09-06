package io.kakai.repository;

import java.util.HashMap;
import java.util.Map;

public class PropertyRepository {
    Map<String, String> properties;

    public PropertyRepository(){
        this.properties = new HashMap<>();
    }

    public Map<String, String> getProperties(){
        return this.properties;
    }
}
