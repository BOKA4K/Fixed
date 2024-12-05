package com.example.v1;

import javafx.beans.property.SimpleStringProperty;

class
TechnicianDetails {
    private SimpleStringProperty name;
    private SimpleStringProperty email;
    private SimpleStringProperty skills;
    private SimpleStringProperty hourlyRate;
    private SimpleStringProperty status;

    public TechnicianDetails(String name, String email, String skills, String hourlyRate, String status) {
        this.name = new SimpleStringProperty(name);
        this.email = new SimpleStringProperty(email);
        this.skills = new SimpleStringProperty(skills);
        this.hourlyRate = new SimpleStringProperty(hourlyRate);
        this.status = new SimpleStringProperty(status);
    }

    public String getName() {
        return name.get();
    }

    public String getEmail() {
        return email.get();
    }

    public String getSkills() {
        return skills.get();
    }

    public String getHourlyRate() {
        return hourlyRate.get();
    }

    public String getStatus() {
        return status.get();
    }

    // Optionally, expose SimpleStringProperty for direct binding
    public SimpleStringProperty nameProperty() {
        return name;
    }

    public SimpleStringProperty emailProperty() {
        return email;
    }

    public SimpleStringProperty skillsProperty() {
        return skills;
    }

    public SimpleStringProperty hourlyRateProperty() {
        return hourlyRate;
    }

    public SimpleStringProperty statusProperty() {
        return status;
    }
}
