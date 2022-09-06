package io.kakai;

import io.kakai.annotate.Application;
import io.kakai.security.negotiator.AuthNegotiator;
import io.kakai.security.renderer.AuthenticatedRenderer;
import io.kakai.security.renderer.GuestRenderer;
import io.kakai.security.renderer.UserRenderer;
import io.kakai.resources.Environments;

@Application(Environments.DEVELOPMENT)
public class Main {
    public static void main(String[] args) {
        new Kakai(8080)
                .addNegotiator(new AuthNegotiator())
                .addViewRenderer(new AuthenticatedRenderer())
                .addViewRenderer(new GuestRenderer())
                .addViewRenderer(new UserRenderer())
                .start();
    }
}