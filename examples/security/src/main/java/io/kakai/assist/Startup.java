package io.kakai.assist;

import io.kakai.Kakai;
import io.kakai.security.SecurityManager;
import io.kakai.annotate.StartupEvent;
import io.kakai.events.KakaiStartup;

@StartupEvent
public class Startup implements KakaiStartup {
    public void setupComplete(Kakai kakai) {
        AuthAccess authAccess = (AuthAccess) kakai.getElement("authAccess");
        System.out.println("auth access" + authAccess);
        SecurityManager.configure(authAccess);
    }
}