package io.kakai.model.web;

import io.kakai.model.DependencyElement;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class EndpointMapping {

    String path;
    String regexedPath;
    String verb;

    Method method;

    List<TypeDetails> typeDetails;
    List<String> typeNames;
    List<Integer> variablePositions;

    DependencyElement dependencyElement;

    UrlBitFeatures urlBitFeatures;

    public EndpointMapping(){
        this.variablePositions = new ArrayList<>();
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getRegexedPath() {
        return regexedPath;
    }

    public void setRegexedPath(String regexedPath) {
        this.regexedPath = regexedPath;
    }

    public String getVerb() {
        return verb;
    }

    public void setVerb(String verb) {
        this.verb = verb;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public List<TypeDetails> getTypeDetails() {
        return typeDetails;
    }

    public void setTypeDetails(List<TypeDetails> typeDetails) {
        this.typeDetails = typeDetails;
    }

    public List<String> getTypeNames() {
        return typeNames;
    }

    public void setTypeNames(List<String> typeNames) {
        this.typeNames = typeNames;
    }

    public List<Integer> getVariablePositions() {
        return variablePositions;
    }

    public void setVariablePositions(List<Integer> variablePositions) {
        this.variablePositions = variablePositions;
    }

    public DependencyElement getClassDetails() {
        return dependencyElement;
    }

    public void setClassDetails(DependencyElement dependencyElement) {
        this.dependencyElement = dependencyElement;
    }

    public UrlBitFeatures getUrlBitFeatures() {
        return urlBitFeatures;
    }

    public void setUrlBitFeatures(UrlBitFeatures urlBitFeatures) {
        this.urlBitFeatures = urlBitFeatures;
    }
}
