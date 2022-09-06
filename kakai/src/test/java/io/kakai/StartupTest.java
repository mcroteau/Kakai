package io.kakai;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class StartupTest extends BaseTest{

    @Test
    public void a(){
        assertEquals("Effort.", kakai.getFlag());
    }
}
