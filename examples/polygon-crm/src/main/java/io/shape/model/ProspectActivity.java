package io.shape.model;

import io.shape.Polygon;

public class ProspectActivity {

    Long id;
    Long prospectId;
    Long activityId;
    Long effortId;
    Long taskDate;
    Long completeDate;
    Boolean completed;
    Long completedByUserId;
    String phones;
    Boolean fiveReminder;
    Boolean fifteenReminder;
    Boolean thirtyReminder;
    Boolean notifiedFive;
    Boolean notifiedFifteen;
    Boolean notifiedThirty;
    String timeZone;

    String name;
    String prospectName;
    String prettyTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProspectId() {
        return prospectId;
    }

    public void setProspectId(Long prospectId) {
        this.prospectId = prospectId;
    }

    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }

    public Long getEffortId() {
        return effortId;
    }

    public void setEffortId(Long effortId) {
        this.effortId = effortId;
    }

    public Long getTaskDate() {
        return taskDate;
    }

    public void setTaskDate(Long taskDate) {
        this.taskDate = taskDate;
    }

    public Long getCompleteDate() {
        return completeDate;
    }

    public void setCompleteDate(Long completeDate) {
        this.completeDate = completeDate;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public String getDate() {
        return Polygon.getPretty(completeDate);
    }

    public Long getCompletedByUserId() {
        return completedByUserId;
    }

    public void setCompletedByUserId(Long completedByUserId) {
        this.completedByUserId = completedByUserId;
    }

    public String getPhones() {
        return phones;
    }

    public void setPhones(String phones) {
        this.phones = phones;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProspectName() {
        return prospectName;
    }

    public void setProspectName(String prospectName) {
        this.prospectName = prospectName;
    }

    public Boolean getFiveReminder() {
        return fiveReminder;
    }

    public void setFiveReminder(Boolean fiveReminder) {
        this.fiveReminder = fiveReminder;
    }

    public Boolean getFifteenReminder() {
        return fifteenReminder;
    }

    public void setFifteenReminder(Boolean fifteenReminder) {
        this.fifteenReminder = fifteenReminder;
    }

    public Boolean getThirtyReminder() {
        return thirtyReminder;
    }

    public void setThirtyReminder(Boolean thirtyReminder) {
        this.thirtyReminder = thirtyReminder;
    }

    public Boolean isNotifiedFive() {
        return notifiedFive;
    }

    public void setNotifiedFive(Boolean notifiedFive) {
        this.notifiedFive = notifiedFive;
    }

    public Boolean isNotifiedFifteen() {
        return notifiedFifteen;
    }

    public void setNotifiedFifteen(Boolean notifiedFifteen) {
        this.notifiedFifteen = notifiedFifteen;
    }

    public Boolean isNotifiedThirty() {
        return notifiedThirty;
    }

    public void setNotifiedThirty(Boolean notifiedThirty) {
        this.notifiedThirty = notifiedThirty;
    }

    public String getPrettyTime() {
        return prettyTime;
    }

    public void setPrettyTime(String prettyTime) {
        this.prettyTime = prettyTime;
    }
}
