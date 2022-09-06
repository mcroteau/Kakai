package io.shape.model;

import io.shape.Polygon;
import java.util.List;

public class Effort {
    Long id;
    Long startDate;
    Long endDate;
    Long startingStatusId;
    Long endingStatusId;
    Status startingStatus;
    Status endingStatus;
    Long prospectId;
    Prospect prospect;
    Boolean success;
    Boolean finished;

    List<ProspectActivity> prospectActivities;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStartDate() {
        return startDate;
    }

    public void setStartDate(Long startDate) {
        this.startDate = startDate;
    }

    public Long getEndDate() {
        return endDate;
    }

    public void setEndDate(Long endDate) {
        this.endDate = endDate;
    }

    public Long getStartingStatusId() {
        return startingStatusId;
    }

    public void setStartingStatusId(Long startingStatusId) {
        this.startingStatusId = startingStatusId;
    }

    public Long getEndingStatusId() {
        return endingStatusId;
    }

    public void setEndingStatusId(Long endingStatusId) {
        this.endingStatusId = endingStatusId;
    }

    public Status getStartingStatus() {
        return startingStatus;
    }

    public void setStartingStatus(Status startingStatus) {
        this.startingStatus = startingStatus;
    }

    public Status getEndingStatus() {
        return endingStatus;
    }

    public void setEndingStatus(Status endingStatus) {
        this.endingStatus = endingStatus;
    }

    public Long getProspectId() {
        return prospectId;
    }

    public void setProspectId(Long prospectId) {
        this.prospectId = prospectId;
    }

    public Prospect getProspect() {
        return prospect;
    }

    public void setProspect(Prospect prospect) {
        this.prospect = prospect;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Boolean getFinished() {
        return finished;
    }

    public void setFinished(Boolean finished) {
        this.finished = finished;
    }

    public String getPrettyStartDate() {
        return Polygon.getPretty(startDate);
    }

    public String getPrettyEndDate() {
        return Polygon.getPretty(endDate);
    }

    public List<ProspectActivity> getProspectActivities() {
        return prospectActivities;
    }

    public void setProspectActivities(List<ProspectActivity> prospectActivities) {
        this.prospectActivities = prospectActivities;
    }
}
