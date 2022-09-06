package io.kakai.model.web;

import com.sun.net.httpserver.HttpExchange;
import io.kakai.resources.Resources;

import java.util.HashMap;
import java.util.Map;

import static io.kakai.resources.Project.SESSION;

public class HttpSession {

    String id;
    HttpExchange httpExchange;
    Map<String, HttpSession> sessions;
    Map<String, Object> attributes;
    Resources resources;

    public HttpSession(Map<String, HttpSession> sessions, HttpExchange httpExchange){
        this.resources = new Resources();
        this.id = resources.SESSION_GUID(27);
        this.sessions = sessions;
        this.httpExchange = httpExchange;
        this.attributes = new HashMap<>();
    }

    public String getId() {
        return id;
    }

    public boolean set(String key, Object value){
        this.attributes.put(key, value);
        return true;
    }

    public Object get(String key){
        if(this.attributes.containsKey(key)){
            return this.attributes.get(key);
        }
        return "";
    }

    public Map<String, Object> data(){
        return this.attributes;
    }

    public boolean remove(String key){
        this.attributes.remove(key);
        return true;
    }

    public boolean dispose(){
        this.httpExchange.getResponseHeaders().set("Set-Cookie", SESSION + "=" + this.id + "; max-age=0" );
        if(this.sessions.containsKey(this.id)){
            this.sessions.remove(this.id);
            return true;
        }
        return false;
    }

}
