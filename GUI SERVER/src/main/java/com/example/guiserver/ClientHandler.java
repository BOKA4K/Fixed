package com.example.guiserver;

import java.io.*;
import java.net.Socket;
import java.sql.SQLException;

public class ClientHandler extends Thread {
    private String name;
    private Socket clientSocket;
    private boolean running;
    private BufferedReader reader;
    private PrintWriter writer;
    private UserService userService;

    public ClientHandler(Socket socket) {
        clientSocket = socket;
        running = true;
        userService = new UserService();
    }

    public void login() throws IOException, SQLException {
        String name = reader.readLine();
        String password = reader.readLine();
        LoginResult res=userService.checkCredentialsExist(name, password);
        if (res.isCredentialsExist()) {
            writer.println("Login successful");
            String UserType= res.UserType();
            int UserId= res.getUserId();
            userService.updateUserStatus(UserId, "online");

            // Transition to LoggedInClientHandler without closing the socket
            System.out.println(name+"loged in ");

            transitionToLoggedInClientHandler(UserType,UserId);
        } else {
            writer.println("Server: Invalid credentials");
        }
    }

    public void signup() throws IOException, SQLException {
        String name = reader.readLine();
        String password = reader.readLine();
        String email = reader.readLine();
        String phone = reader.readLine();
        String role = reader.readLine();

        if (userService.registerUser(name, password, email, phone, role)) {
            writer.println("Username registered successfully");
        } else {
            writer.println("Username already exists");
        }
    }

    public void run() {
        try {
            reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            writer = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true);

            while (running) {
                String message = reader.readLine();
                if (message != null) {
                    switch (message) {
                        case "login" -> login();
                        case "signup" -> signup();
                        default -> writer.println("Server: Invalid command");
                    }
                }
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            close();
        } finally {
        }
    }

    private void close() {
        try {
            writer.close();
            reader.close();
            running = false;
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void transitionToLoggedInClientHandler(String UserType,int UserId) {
        try {
            // Instead of closing the socket, pass it to the LoggedInClientHandler
            LoggedInClientHandler loggedInClientHandler = new LoggedInClientHandler(clientSocket, UserType,UserId, reader, writer, userService);
            running = false;

            loggedInClientHandler.start(); // Start the LoggedInClientHandler thread

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
