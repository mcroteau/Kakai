package io.kakai.model.web;

import java.util.ArrayList;
import java.util.List;

public class RequestComponent {
    String name;
    String value;
    boolean hasFiles;
    List<FileComponent> files;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String value() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public boolean isHasFiles() {
        return hasFiles;
    }

    public void setHasFiles(boolean hasFiles) {
        this.hasFiles = hasFiles;
    }

    public List<FileComponent> getFiles() {
        return this.files;
    }

    public void setFiles(List<FileComponent> files) {
        this.files = files;
    }

    public RequestComponent(){
        this.files = new ArrayList<>();
    }
}
