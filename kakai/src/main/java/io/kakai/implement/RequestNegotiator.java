package io.kakai.implement;

import com.sun.net.httpserver.HttpExchange;
import io.kakai.model.web.HttpRequest;
import io.kakai.model.web.HttpResponse;

public interface RequestNegotiator {
    public void intercept(HttpRequest request, HttpExchange httpExchange);
}
