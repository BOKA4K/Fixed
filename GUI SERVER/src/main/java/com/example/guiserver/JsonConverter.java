package com.example.guiserver;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class JsonConverter {
    private UserService userService;

    public JsonConverter(UserService userService) {
        this.userService = userService;
    }

    public String convertTechniciansToJson(List<TechnicianDetails> technicians) {
        JSONArray jsonArray = new JSONArray();

        for (TechnicianDetails technician : technicians) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", technician.getName());
            jsonObject.put("email", technician.getEmail());
            jsonObject.put("skills", technician.getSkills());
            jsonObject.put("hourlyRate", technician.getHourlyRate());
            jsonObject.put("status", technician.getStatus());
            jsonArray.put(jsonObject);
        }

        return jsonArray.toString();
    }
    public JSONArray getAppointmentsAsJson(int UserId,String UserType) {
        List<AppointmentDetails> appointments = userService.getAppointmentsByCredentials(UserId,UserType);
        JSONArray appointmentsJsonArray = new JSONArray();

        for (AppointmentDetails appointment : appointments) {
            JSONObject appointmentJson = new JSONObject();
            appointmentJson.put("appointment_id", appointment.getAppointmentId());
            appointmentJson.put("scheduled_time", appointment.getScheduledTime());
            appointmentJson.put("status", appointment.getStatus());
            appointmentJson.put("problem_description", appointment.getProblemDescription());

            // Add the individual appointment JSON object to the array
            appointmentsJsonArray.put(appointmentJson);
        }

        return appointmentsJsonArray;  // Return the JSON array
    }

}
