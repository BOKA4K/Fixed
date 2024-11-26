package com.example.guiserver;

import java.io.*;
import java.net.Socket;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

import static com.example.guiserver.Server.conn;

public class ClientHandler extends Thread {
    private String name;
    private String status;
    private Socket clientSocket;
    private boolean running;
    private BufferedReader reader;
    private PrintWriter writer;

    public ClientHandler(Socket socket) {
        clientSocket = socket;
        running = true;
    }

    // Check if user credentials exist in the database
    private static boolean checkCredentialsExist(String username, String password) {
        try {
            String query = "SELECT * FROM users WHERE name = ? AND password = ?";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();
            boolean credentialsExist = resultSet.next();
            resultSet.close();
            statement.close();
            return credentialsExist;
        } catch (SQLException e) {
            System.err.println("Failed to connect to the database!");
            e.printStackTrace();
        }
        return false;
    }

    // Handle user login
    public void login() throws IOException, SQLException {
        String name = reader.readLine();
        System.out.println("Received name: " + name);
        String password = reader.readLine();
        System.out.println("Received password: " + password);
        if (checkCredentialsExist(name, password)) {
            writer.println("Login successful");
            this.name = name;
            this.status = "online";
            set_status("online");
        } else {
            writer.println("Server: Invalid credentials");
        }
    }

    // Set user status in the database
    public void set_status(String status) {
        String sql = "UPDATE users SET status = ? WHERE name = ?";
        try {
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, status);
            statement.setString(2, name);
            this.status = status;

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Status updated successfully.");
            } else {
                System.out.println("User not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Handle user signup
    public void signup() throws IOException, SQLException {
        String name = reader.readLine();
        System.out.println("Received name: " + name);
        String password = reader.readLine();

        System.out.println("Received password: " + password);
        String query = "INSERT INTO users (name, password) VALUES (?, ?)";

        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, name);
            statement.setString(2, password);
            statement.executeUpdate();
            System.out.println("User registered successfully");
            writer.println("Username registered successfully");
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Username already exists");
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
                    System.out.println("Received message: " + message);

                    switch (message) {
                        case "login" -> {
                            writer.flush();
                            login();
                        }
                        case "signup" -> {
                            writer.flush();
                            signup();
                        }
                        default -> writer.println("Server: Invalid command");
                    }
                }
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }

    private void close() {
        try {
            set_status("offline");
            writer.close();
            reader.close();
            running = false;
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
