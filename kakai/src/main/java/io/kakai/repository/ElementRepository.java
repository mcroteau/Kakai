package io.kakai.repository;

import io.kakai.model.Element;

import java.util.HashMap;
import java.util.Map;

public class ElementRepository {

    Map<String, Element> elements;

    public ElementRepository(){
        this.elements = new HashMap<>();
    }

    public Map<String, Element> getElements(){
        return this.elements;
    }

}
