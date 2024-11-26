package com.example.guiserver;

class TechnicianDetails {
    private String name;
    private String email;
    private String skills;
    private String hourlyRate;
    private String status;

    public TechnicianDetails(String name, String email, String skills, String hourlyRate, String status) {
        this.name = name;
        this.email = email;
        this.skills = skills;
        this.hourlyRate = hourlyRate;
        this.status = status;
    }

    // Getters and toString method
    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getSkills() {
        return skills;
    }

    public String getHourlyRate() {
        return hourlyRate;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "TechnicianDetails{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", skills='" + skills + '\'' +
                ", hourlyRate='" + hourlyRate + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}