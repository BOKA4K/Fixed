package com.example.guiserver;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserService {
    public boolean checkCredentialsExist(String username, String password) {
        try {
            String query = "SELECT * FROM users WHERE name = ? AND password = ?";
            PreparedStatement statement = Server.conn.prepareStatement(query);
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
    public List<TechnicianDetails> getTechniciansDetails() {
        List<TechnicianDetails> technicianDetailsList = new ArrayList<>();
        String query = "SELECT name, email, skills, hourlyrate, status FROM users WHERE role = 'technician'";

        try (PreparedStatement statement = Server.conn.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            // Iterate through the result set and populate the list
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                String email = resultSet.getString("email");
                String skills = resultSet.getString("skills");
                String hourlyRate = resultSet.getString("hourlyrate");
                String status = resultSet.getString("status");

                TechnicianDetails technician = new TechnicianDetails(name, email, skills, hourlyRate, status);
                technicianDetailsList.add(technician);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return technicianDetailsList;
    }

    public boolean registerUser(String name, String password, String email, String phone, String role) {
        String query = "INSERT INTO users (name, password, email, phone_number, role) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = Server.conn.prepareStatement(query)) {
            statement.setString(1, name);
            statement.setString(2, password);
            statement.setString(3, email);
            statement.setString(4, phone);
            statement.setString(5, role);
            statement.executeUpdate();
            return true;
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Username already exists");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void updateUserStatus(String name, String status) {
        String sql = "UPDATE users SET status = ? WHERE name = ?";
        try {
            PreparedStatement statement = Server.conn.prepareStatement(sql);
            statement.setString(1, status);
            statement.setString(2, name);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    public HashMap<String, String> getTechniciansSkills() {
        HashMap<String, String> techniciansSkills = new HashMap<>();
        String query = "SELECT email, skills FROM users WHERE role = 'technician' AND skills IS NOT NULL";

        try (PreparedStatement statement = Server.conn.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            // Iterate through the result set and populate the map
            while (resultSet.next()) {
                String email = resultSet.getString("email");
                String skills = resultSet.getString("skills");
                techniciansSkills.put(email, skills);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return techniciansSkills;
    }
}
