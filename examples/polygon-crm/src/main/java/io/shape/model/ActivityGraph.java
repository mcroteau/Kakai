package io.shape.model;

import java.util.List;

public class ActivityGraph {
    String name;
    ActivityCount activityCount;
    List<ActivityCount> children;

    public ActivityCount getActivityCount() {
        return activityCount;
    }

    public void setActivityCount(ActivityCount activityCount) {
        this.activityCount = activityCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ActivityCount> getChildren() {
        return children;
    }

    public void setChildren(List<ActivityCount> children) {
        this.children = children;
    }
}
