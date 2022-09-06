package io.shape.assist;

import io.kakai.Kakai;
import io.kakai.annotate.StartupEvent;
import io.kakai.events.KakaiStartup;
import io.shape.service.StartupService;

@StartupEvent
public class Startup implements KakaiStartup {
    public void setupComplete(Kakai kakai) {
        StartupService startupService = (StartupService) kakai.getElement("startupservice");
        startupService.init();
    }
}
