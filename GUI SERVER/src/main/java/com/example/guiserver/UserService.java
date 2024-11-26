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
    public boolean bookAppointment(String username, String password, String technicianEmail, String appointmentDate) {
        try {
            // Step 1: Check if the user's credentials are valid
            String query = "SELECT user_id FROM users WHERE name = ? AND password = ?";
            PreparedStatement statement = Server.conn.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();

            if (!resultSet.next()) {
                System.out.println("Invalid credentials");
                resultSet.close();
                statement.close();
                return false; // User doesn't exist or password is incorrect
            }

            int userId = resultSet.getInt("user_id");
            resultSet.close();
            statement.close();

            // Step 2: Check if the technician exists
            String technicianQuery = "SELECT user_id FROM users WHERE email = ? AND role = 'technician'";
            PreparedStatement technicianStatement = Server.conn.prepareStatement(technicianQuery);
            technicianStatement.setString(1, technicianEmail);
            ResultSet technicianResultSet = technicianStatement.executeQuery();

            if (!technicianResultSet.next()) {
                System.out.println("Technician not found");
                technicianResultSet.close();
                technicianStatement.close();
                return false; // Technician not found
            }

            int technicianId = technicianResultSet.getInt("user_id");
            technicianResultSet.close();
            technicianStatement.close();

            // Step 3: Create a service request for the customer
            String serviceRequestQuery = "INSERT INTO service_requests (user_id, technician_id, status, requested_at, appointment_time) VALUES (?, ?, 'pending', CURRENT_TIMESTAMP, ?)";
            PreparedStatement serviceRequestStatement = Server.conn.prepareStatement(serviceRequestQuery);
            serviceRequestStatement.setInt(1, userId);
            serviceRequestStatement.setInt(2, technicianId);
            serviceRequestStatement.setString(3, appointmentDate);
            serviceRequestStatement.executeUpdate();

            // Step 4: Schedule an appointment for the service request
            String appointmentQuery = "SELECT request_id FROM service_requests WHERE user_id = ? AND technician_id = ? AND appointment_time = ?";
            PreparedStatement appointmentStatement = Server.conn.prepareStatement(appointmentQuery);
            appointmentStatement.setInt(1, userId);
            appointmentStatement.setInt(2, technicianId);
            appointmentStatement.setString(3, appointmentDate);
            ResultSet appointmentResultSet = appointmentStatement.executeQuery();

            if (appointmentResultSet.next()) {
                int serviceRequestId = appointmentResultSet.getInt("request_id");

                String scheduleAppointmentQuery = "INSERT INTO appointments (service_request_id, technician_id, scheduled_time, status) VALUES (?, ?, ?, 'scheduled')";
                PreparedStatement scheduleAppointmentStatement = Server.conn.prepareStatement(scheduleAppointmentQuery);
                scheduleAppointmentStatement.setInt(1, serviceRequestId);
                scheduleAppointmentStatement.setInt(2, technicianId);
                scheduleAppointmentStatement.setString(3, appointmentDate);
                scheduleAppointmentStatement.executeUpdate();

                appointmentResultSet.close();
                scheduleAppointmentStatement.close();
                return true; // Appointment successfully booked
            }

            appointmentResultSet.close();
            appointmentStatement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Failed to book appointment
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
    public List<AppointmentDetails> getAppointmentsByCredentials(String username, String password) {
        List<AppointmentDetails> appointments = new ArrayList<>();

        try {
            // Step 1: Validate user credentials
            String query = "SELECT user_id FROM users WHERE name = ? AND password = ?";
            PreparedStatement statement = Server.conn.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();

            if (!resultSet.next()) {
                System.out.println("Invalid credentials");
                resultSet.close();
                statement.close();
                return appointments;  // Empty list if user is invalid
            }

            int userId = resultSet.getInt("user_id");
            resultSet.close();
            statement.close();

            // Step 2: Fetch appointments for this user
            String appointmentQuery = "SELECT a.appointment_id, a.scheduled_time, a.status, s.problem_description " +
                    "FROM appointments a " +
                    "JOIN service_requests s ON a.service_request_id = s.request_id " +
                    "WHERE s.user_id = ?";
            PreparedStatement appointmentStatement = Server.conn.prepareStatement(appointmentQuery);
            appointmentStatement.setInt(1, userId);
            ResultSet appointmentResultSet = appointmentStatement.executeQuery();

            // Step 3: Process the results and populate the appointments list
            while (appointmentResultSet.next()) {
                int appointmentId = appointmentResultSet.getInt("appointment_id");
                String scheduledTime = appointmentResultSet.getString("scheduled_time");
                String status = appointmentResultSet.getString("status");
                String problemDescription = appointmentResultSet.getString("problem_description");

                AppointmentDetails appointment = new AppointmentDetails(appointmentId, scheduledTime, status, problemDescription);
                appointments.add(appointment);
            }

            appointmentResultSet.close();
            appointmentStatement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return appointments;  // Returns the list of appointments
    }
}
