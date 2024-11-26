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
    private Socket clientSocket;
    private BufferedReader reader;
    private PrintWriter writer;
    private UserService userService;
    private   AI_assisted aiAssisted;
    public LoggedInClientHandler(Socket socket, String name, BufferedReader reader, PrintWriter writer, UserService userService) {
        this.clientSocket = socket;
        this.name = name;
        this.reader = reader;
        this.writer = writer;
        this.userService = userService;
        aiAssisted=new AI_assisted();
    }
    public void get_Ai_Recommendation() throws Exception {

        String problemDescription=reader.readLine();
        if (problemDescription!=null||problemDescription.isEmpty()){
            System.out.println(problemDescription);

            String response=aiAssisted.getRecommendedTechnician(problemDescription,userService.getTechniciansSkills());
            writer.println(response);
            System.out.println(response);
        }

    }
    public static String convertTechniciansToJson(List<TechnicianDetails> technicians) {
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
        String xx=convertTechniciansToJson( userService.getTechniciansDetails());
        writer.println(xx);


    }
    public void run() {
        try {
            String message;
            while ((message = reader.readLine()) != null) {
                System.out.println("Message from " + name + ": " + message);
                switch (message) {
                    case "Ai-assisted" -> get_Ai_Recommendation();
                    case "TechnicianDetails" -> TechnicianDetails();

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
