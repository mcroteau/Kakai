package io.shape.web;

import io.kakai.annotate.Bind;
import io.kakai.annotate.Router;
import io.kakai.annotate.http.Get;
import io.shape.service.AuthService;

@Router
public class GenericRouter {

    @Bind
    AuthService authService;

    @Get("/")
    public String index(){
        if(authService.isAuthenticated()){
            return "[redirect]/snapshot";
        }
        return "[redirect]/signin";
    }

}
