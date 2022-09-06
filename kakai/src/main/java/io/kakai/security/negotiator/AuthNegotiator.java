package io.kakai.security.negotiator;

import com.sun.net.httpserver.HttpExchange;
import io.kakai.implement.RequestNegotiator;
import io.kakai.model.web.HttpRequest;
import io.kakai.security.SecurityManager;

public class AuthNegotiator implements RequestNegotiator {
    @Override
    public void intercept(HttpRequest request, HttpExchange httpExchange) {
        SecurityManager.SAVE(request);
        SecurityManager.SAVE(httpExchange);
    }
}
