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
    private JsonConverter jsonConverter;

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
    public void TechnicianDetails() {
        System.out.println(userService.getTechniciansDetails());
        String xx=jsonConverter.convertTechniciansToJson( userService.getTechniciansDetails());
        System.out.println(xx);
        writer.println(xx);

    }
    private void Book_Appointment() throws IOException {
        String email = reader.readLine();
        String date = reader.readLine();
        String problem_description = reader.readLine();
        if (email != null && !email.isEmpty() && date != null && !date.isEmpty()) {
            boolean booking_status=userService.bookAppointment(name, password, email, date,problem_description);
            if (booking_status){
                writer.println("booking done");
            }
        } else {
            System.out.println("Email or date cannot be null or empty.");
        }


    }
        private void Service_overview(){
            writer.println(jsonConverter.getAppointmentsAsJson(name,password));
        }
        public void run() {
        try {
            jsonConverter=new JsonConverter(userService);
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
