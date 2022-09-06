package io.kakai;

import io.kakai.annotate.Router;
import io.kakai.annotate.http.Get;
import io.kakai.annotate.http.Post;
import io.kakai.model.web.HttpRequest;
import io.kakai.model.web.HttpResponse;
import io.kakai.security.SecurityManager;

@Router
public class IdentityRouter {

    @Get("/")
    public String signin(HttpResponse resp) {
        resp.set("instructions", "effort.");
        return "/pages/signin.html";
    }

    @Post("/authenticate")
    public String authenticate(HttpRequest req) {
        String user = req.value("user");
        String pass = req.value("pass");

        if(SecurityManager.signin(user, pass)){
            return "[redirect]/secret";
        }

        return "[redirect]/";
    }

    @Get("/secret")
    public String secret(HttpResponse resp) {
        if(SecurityManager.isAuthenticated()){
            return "/pages/secret.html";
        }
        resp.set("message", "authenticate please.");
        return "[redirect]/";
    }

    @Get("/signout")
    public String signout() {
        SecurityManager.signout();
        return "[redirect]/";
    }

}