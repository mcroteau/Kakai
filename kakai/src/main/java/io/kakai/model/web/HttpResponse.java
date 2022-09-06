package io.kakai.model.web;

import java.util.HashMap;
import java.util.Map;

public class HttpResponse {

    String title;
    String keywords;
    String description;
    Map<String, Object> data;

    public HttpResponse(){
        this.data = new HashMap<>();
    }

    public void set(String key, Object value){
        this.data.put(key, value);
    }

    public Object get(String key){
        if(this.data.containsKey(key)){
            return this.data.get(key);
        }
        return null;
    }

    public Boolean contains(String key){
        return this.data.containsKey(key);
    }

    public void setTitle(String title){
        this.title = title;
    }

    public String getTitle(){
        if(this.title != null){
            return this.title;
        }
        return "";
    }

    public String getKeywords() {
        if(this.keywords != null){
            return this.keywords;
        }
        return "";
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getDescription() {
        if(this.description != null){
            return this.description;
        }
        return "";
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, Object> data(){
        return this.data;
    }

}
