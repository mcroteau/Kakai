package io.kakai;

import io.kakai.annotate.*;
import io.kakai.annotate.http.Get;

@Router
public class HelloRouter {

    @Text
    @Get("/")
    public String hi(){
        return "Kanami!";
    }

}
