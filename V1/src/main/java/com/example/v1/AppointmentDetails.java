package com.example.v1;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class AppointmentDetails {
    private final IntegerProperty appointmentId;
    private final StringProperty scheduledTime;
    private final StringProperty status;
    private final StringProperty problemDescription;

    public AppointmentDetails(int appointmentId, String scheduledTime, String status, String problemDescription) {
        this.appointmentId = new SimpleIntegerProperty(appointmentId);
        this.scheduledTime = new SimpleStringProperty(scheduledTime);
        this.status = new SimpleStringProperty(status);
        this.problemDescription = new SimpleStringProperty(problemDescription);
    }

    public IntegerProperty appointmentIdProperty() {
        return appointmentId;
    }

    public StringProperty scheduledTimeProperty() {
        return scheduledTime;
    }

    public StringProperty statusProperty() {
        return status;
    }

    public StringProperty problemDescriptionProperty() {
        return problemDescription;
    }

    public int getAppointmentId() {
        return appointmentId.get();
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId.set(appointmentId);
    }

    public String getScheduledTime() {
        return scheduledTime.get();
    }

    public void setScheduledTime(String scheduledTime) {
        this.scheduledTime.set(scheduledTime);
    }

    public String getStatus() {
        return status.get();
    }

    public void setStatus(String status) {
        this.status.set(status);
    }

    public String getProblemDescription() {
        return problemDescription.get();
    }

    public void setProblemDescription(String problemDescription) {
        this.problemDescription.set(problemDescription);
    }
}
