package io.kakai.resources;

import com.sun.net.httpserver.HttpExchange;

public class UriTranslator {

    Resources resources;
    HttpExchange httpExchange;

    public UriTranslator(Resources resources, HttpExchange httpExchange){
        this.resources = resources;
        this.httpExchange = httpExchange;
    }

    public String translate(){
        String uriPre = httpExchange.getRequestURI().toString();
        String[] parts = uriPre.split("\\?");
        String uri = parts[0];

        if(uri.equals("")) {
            uri = "/";
        }
        if(uri.endsWith("/") &&
                !uri.equals("/")){
            uri = resources.removeLast(uri);
        }
        return uri;
    }

    public String getParameters() {
        String uriPre = httpExchange.getRequestURI().toString();
        String[] parts = uriPre.split("\\?");
        if(parts.length > 1){
            return parts[1];
        }
        return "";
    }
}
