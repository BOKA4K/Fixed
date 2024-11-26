package com.example.guiserver;

public class AppointmentDetails {
    private int appointmentId;
    private String scheduledTime;
    private String status;
    private String problemDescription;

    public AppointmentDetails(int appointmentId, String scheduledTime, String status, String problemDescription) {
        this.appointmentId = appointmentId;
        this.scheduledTime = scheduledTime;
        this.status = status;
        this.problemDescription = problemDescription;
    }

    // Getters and setters
    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(String scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProblemDescription() {
        return problemDescription;
    }

    public void setProblemDescription(String problemDescription) {
        this.problemDescription = problemDescription;
    }

}
