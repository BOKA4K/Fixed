package com.example.v1;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class AppointmentDetails {
    private int appointmentId;
    private String scheduledTime;
    private String status;
    private String problemDescription;

    private final StringProperty appointmentIdProperty = new SimpleStringProperty();
    private final StringProperty scheduledTimeProperty = new SimpleStringProperty();
    private final StringProperty statusProperty = new SimpleStringProperty();
    private final StringProperty problemDescriptionProperty = new SimpleStringProperty();

    public AppointmentDetails(int appointmentId, String scheduledTime, String status, String problemDescription) {
        this.appointmentId = appointmentId;
        this.scheduledTime = scheduledTime;
        this.status = status;
        this.problemDescription = problemDescription;

        // Initialize properties
        appointmentIdProperty.set(String.valueOf(appointmentId));
        scheduledTimeProperty.set(scheduledTime);
        statusProperty.set(status);
        problemDescriptionProperty.set(problemDescription);
    }

    // Getters for the properties
    public StringProperty appointmentIdProperty() {
        return appointmentIdProperty;
    }

    public StringProperty scheduledTimeProperty() {
        return scheduledTimeProperty;
    }

    public StringProperty statusProperty() {
        return statusProperty;
    }

    public StringProperty problemDescriptionProperty() {
        return problemDescriptionProperty;
    }
}
