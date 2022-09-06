package io.kakai.model;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Event {

    Object event;

    public Object getEvent() {
        return this.event;
    }

    public void setEvent(Object event) {
        this.event = event;
    }

    public Event(Object event){
        this.event = event;
    }
}
