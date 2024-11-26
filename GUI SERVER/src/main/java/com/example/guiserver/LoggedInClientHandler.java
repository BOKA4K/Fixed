package com.example.guiserver;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

public class LoggedInClientHandler extends Thread {
    private String name;
    private String password;


    private Socket clientSocket;
    private BufferedReader reader;
    private PrintWriter writer;
    private UserService userService;
    private   AI_assisted aiAssisted;
    public LoggedInClientHandler(Socket socket, String name,String password, BufferedReader reader, PrintWriter writer, UserService userService) {
        this.clientSocket = socket;
        this.name = name;
        this.password=password;
        this.reader = reader;
        this.writer = writer;
        this.userService = userService;
        aiAssisted=new AI_assisted();
    }
    private void get_Ai_Recommendation() throws Exception {

        String problemDescription=reader.readLine();
        if (problemDescription!=null||problemDescription.isEmpty()){
            System.out.println(problemDescription);
            String response=aiAssisted.getRecommendedTechnician(problemDescription,userService.getTechniciansSkills());
            writer.println(response);
            System.out.println(response);
        }

    }
    private static String convertTechniciansToJson(List<TechnicianDetails> technicians) {
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
    public void TechnicianDetails() {
        System.out.println(userService.getTechniciansDetails());
        String xx=convertTechniciansToJson( userService.getTechniciansDetails());
        System.out.println(xx);
        writer.println(xx);

    }
    private void Book_Appointment() throws IOException {
        String email = reader.readLine();
        String date = reader.readLine();

        if (email != null && !email.isEmpty() && date != null && !date.isEmpty()) {
            boolean booking_status=userService.bookAppointment(name, password, email, date);
            if (booking_status){
                writer.println("booking done");
            }
        } else {
            System.out.println("Email or date cannot be null or empty.");
        }


    }
    public JSONArray getAppointmentsAsJson(String name, String password) {
        List<AppointmentDetails> appointments = userService.getAppointmentsByCredentials(name, password);
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
        private void Service_overview(){
            writer.println(getAppointmentsAsJson(name,password));
        }
        public void run() {
        try {
            String message;
            while ((message = reader.readLine()) != null) {
                System.out.println("Message from " + name + ": " + message);
                switch (message) {
                    case "Ai-assisted" -> get_Ai_Recommendation();
                    case "TechnicianDetails" -> TechnicianDetails();
                    case "BookAppointment" -> Book_Appointment();

                    case "Service_overview" -> Service_overview();

                    default -> System.out.println("Server: Invalid command");
                }            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            close();
        }
    }

    private void close() {
        try {
            writer.close();
            reader.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
