package io.kakai.implement;

import com.sun.net.httpserver.HttpExchange;
import io.kakai.model.web.HttpRequest;

public interface ViewRenderer {

    /**
     * example would be sec:auth
     * within the view &lt;sec:auth&gt;Hi!&lt;/sec:auth&gt;
     *
     * @return key:value pair
     */
    public String getKey();

    /**
     * @return true if conditional snipit, false if content is rendered.
     */
    public Boolean isEval();

    public boolean truthy(HttpRequest httpRequest, HttpExchange exchange);

    public String render(HttpRequest httpRequest, HttpExchange exchange);

}
