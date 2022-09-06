package io.kakai.resources;

import java.util.List;

public class Settings {
    public boolean createDb;
    public boolean dropDb;
    public boolean noAction;
    List<String> resources;
    List<String> propertiesFiles;

    public boolean isCreateDb() {
        return createDb;
    }

    public void setCreateDb(boolean createDb) {
        this.createDb = createDb;
    }

    public boolean isDropDb() {
        return dropDb;
    }

    public void setDropDb(boolean dropDb) {
        this.dropDb = dropDb;
    }

    public boolean isNoAction() {
        return noAction;
    }

    public void setNoAction(boolean noAction) {
        this.noAction = noAction;
    }

    public List<String> getResources() {
        return resources;
    }

    public void setResources(List<String> resources) {
        this.resources = resources;
    }

    public List<String> getPropertiesFiles() {
        return propertiesFiles;
    }

    public void setPropertiesFiles(List<String> propertiesFiles) {
        this.propertiesFiles = propertiesFiles;
    }
}
