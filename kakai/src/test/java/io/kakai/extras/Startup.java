package io.kakai.extras;

import io.kakai.Kakai;
import io.kakai.annotate.StartupEvent;
import io.kakai.events.KakaiStartup;

@StartupEvent
public class Startup implements KakaiStartup{
    @Override
    public void setupComplete(Kakai kakai) {
        kakai.setFlag("Effort.");
    }
}
