package io.kakai.model.web;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import io.kakai.resources.Resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.kakai.resources.Project.SESSION;

public class HttpRequest {

    String requestBody;
    Resources resources;
    HttpSession httpSession;
    HttpExchange httpExchange;
    Map<String, HttpSession> sessions;
    Map<String, RequestComponent> elements;

    public HttpRequest(Map<String, HttpSession> sessions, HttpExchange httpExchange){
        this.sessions = sessions;
        this.resources = new Resources();
        this.elements = new HashMap<>();
        this.httpExchange = httpExchange;
        this.setSession();
    }

    public Map<String, RequestComponent> data(){
        return this.elements;
    }

    public void put(String elementName, RequestComponent requestComponent){
        this.elements.put(elementName, requestComponent);
    }

    public Headers getHeaders() {
        return httpExchange.getRequestHeaders();
    }

    public HttpSession getSession() {
        return httpSession;
    }

    public void setSession(){
        String id = resources.getCookie(SESSION, httpExchange.getRequestHeaders());
        if(this.sessions.containsKey(id)) {
            setSession(this.sessions.get(id));
        }
    }

    public void setSession(HttpSession httpSession) {
        this.httpSession = httpSession;
    }

    public HttpSession getSession(boolean newitup){
        String id = resources.getCookie(SESSION, httpExchange.getRequestHeaders());
        if(!newitup){
            if(this.sessions.containsKey(id)){
                setSession(this.sessions.get(id));
                return this.sessions.get(id);
            }
        }else if(newitup){
            return getHttpSession();
        }
        return null;
    }

    private HttpSession getHttpSession(){
        HttpSession httpSession = new HttpSession(this.sessions, httpExchange);
        this.sessions.put(httpSession.getId(), httpSession);
        String compound = SESSION + "=" + httpSession.getId();
        this.httpExchange.getResponseHeaders().set("Set-Cookie", compound);
        setSession(httpSession);
        return httpSession;
    }


    public void set(String key, RequestComponent requestComponent){
        this.elements.put(key, requestComponent);
    }

    /**
     * value(String key) is a lookup
     * for a given form field and returns the
     * value for the given RequestComponent
     *
     * @see RequestComponent
     *
     * @param key
     * @return returns the value for the given form field
     */
    public String value(String key){
        if(elements.containsKey(key)){
            return elements.get(key).value();
        }
        return null;
    }

    public RequestComponent getRequestComponent(String key){
        for(Map.Entry<String,RequestComponent> component : elements.entrySet()){
            System.out.println(component.getKey());
        }
        if(elements.containsKey(key)){
            return elements.get(key);
        }
        return null;
    }

    public List<String> getMultipleValues(String key){
        List<String> values = new ArrayList<>();
        for(Map.Entry<String, RequestComponent> entry : elements.entrySet()){
            if(key.equals(entry.getKey()) &&
                    entry.getValue().value() != null){
                values.add(entry.getValue().value());
            }
        }
        return values;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    public void setValues(String parameters) {
        String[] keyValues = parameters.split("&");
        for(String keyValue : keyValues){
            String[] parts = keyValue.split("=");
            if(parts.length > 1){
                String key = parts[0];
                String value = parts[1];
                RequestComponent requestComponent = new RequestComponent();
                requestComponent.setName(key);
                requestComponent.setValue(value);
                elements.put(key, requestComponent);
            }
        }
    }

    public void setSessionValue(String name, String value){
        this.httpSession.set(name, value);
    }

}
