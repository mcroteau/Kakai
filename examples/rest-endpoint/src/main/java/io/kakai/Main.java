package io.kakai;

import io.kakai.annotate.Application;
import io.kakai.resources.Environments;

@Application(Environments.DEVELOPMENT)
public class Main {
    public static void main(String[] args){
        new Kakai(8080).start();
    }
}
