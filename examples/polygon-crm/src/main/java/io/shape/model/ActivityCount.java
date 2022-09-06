package io.shape.model;

import java.util.List;

public class ActivityCount {
    String name;
    Integer count;
    List<ActivityCount> children;

    Integer index;

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<ActivityCount> getChildren() {
        return children;
    }

    public void setChildren(List<ActivityCount> children) {
        this.children = children;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
