package io.kakai.security.renderer;

import com.sun.net.httpserver.HttpExchange;
import io.kakai.implement.ViewRenderer;
import io.kakai.model.web.HttpRequest;
import io.kakai.security.SecurityManager;

public class UserRenderer implements ViewRenderer {

    public boolean truthy(HttpRequest httpRequest, HttpExchange exchange){ return true; }

    public String render(HttpRequest httpRequest, HttpExchange exchange){
        return SecurityManager.getUser();
    }

    public String getKey() {
        return "kakai:user";
    }

    public Boolean isEval() {
        return false;
    }
}