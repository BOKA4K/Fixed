package com.example.guiserver;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserService {
    public LoginResult checkCredentialsExist(String username, String password) {
        try {
            String query = "SELECT user_id, role FROM users WHERE name = ? AND password = ?";
            PreparedStatement statement = Server.conn.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int userId = resultSet.getInt("user_id"); // Retrieve user_id
                String role = resultSet.getString("role"); // Retrieve role
                System.out.println("User ID: " + userId);
                System.out.println("Role: " + role);

                // Save userId and role to variables or a class-level object for further use


                resultSet.close();
                statement.close();
                return new LoginResult(true,role,userId); // Credentials exist
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            System.err.println("Failed to connect to the database!");
            e.printStackTrace();
        }
        // Credentials do not exist
        return new LoginResult(false,null,0);
    }

    public boolean bookAppointment(int UserId, String technicianEmail, String appointmentDate,String problem_description) {
        try {
            // Step 1: Check if the user's credentials are valid
            String query = "SELECT user_id FROM users WHERE user_id = ?";
            PreparedStatement statement = Server.conn.prepareStatement(query);
            statement.setString(1, String.valueOf(UserId));
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
            String serviceRequestQuery = "INSERT INTO service_requests (user_id, technician_id,  problem_description, status, requested_at, appointment_time) VALUES (?, ?, ? , 'pending', CURRENT_TIMESTAMP, ?)";
            PreparedStatement serviceRequestStatement = Server.conn.prepareStatement(serviceRequestQuery);
            serviceRequestStatement.setInt(1, userId);
            serviceRequestStatement.setInt(2, technicianId);
            serviceRequestStatement.setString(3, problem_description);
            serviceRequestStatement.setString(4, appointmentDate);


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

    public void updateUserStatus(int Userid, String status) {
        String sql = "UPDATE users SET status = ? WHERE user_id = ?";
        try {
            PreparedStatement statement = Server.conn.prepareStatement(sql);
            statement.setString(1, status);
            statement.setInt(2, Userid);
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
    public boolean cancelAppointment(int appointmentId) {
        String query = "UPDATE appointments SET status = 'cancelled' WHERE appointment_id = ?";

        try (PreparedStatement statement = Server.conn.prepareStatement(query)) {
            // Set the appointmentId parameter
            statement.setInt(1, appointmentId);

            // Execute the update query
            int rowsAffected = statement.executeUpdate();

            // If rowsAffected > 0, the appointment was updated successfully
            if (rowsAffected > 0) {
                System.out.println("Appointment with ID " + appointmentId + " has been cancelled.");
                return true;
            } else {
                System.out.println("No appointment found with ID " + appointmentId);
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Return false if an error occurs
        }
    }
    public boolean ChangeAppointmentStatus(int appointmentId,String status) {
        String query = "UPDATE appointments SET status = ? WHERE appointment_id = ?";

        try (PreparedStatement statement = Server.conn.prepareStatement(query)) {
            // Set the appointmentId parameter
            statement.setString(1, status);

            statement.setInt(2, appointmentId);

            // Execute the update query
            int rowsAffected = statement.executeUpdate();

            // If rowsAffected > 0, the appointment was updated successfully
            if (rowsAffected > 0) {
                System.out.println("Appointment with ID " + appointmentId + " has been cancelled.");
                return true;
            } else {
                System.out.println("No appointment found with ID " + appointmentId);
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Return false if an error occurs
        }
    }
    public boolean addPayment(int appointmentId, double amount) {
        try {
            // Step 1: Fetch the corresponding service_request_id using appointment_id
            String serviceRequestQuery = "SELECT service_request_id FROM appointments WHERE appointment_id = ?";
            PreparedStatement serviceRequestStmt = Server.conn.prepareStatement(serviceRequestQuery);
            serviceRequestStmt.setInt(1, appointmentId);
            ResultSet resultSet = serviceRequestStmt.executeQuery();

            if (!resultSet.next()) {
                System.out.println("No service request found for the given appointment ID.");
                resultSet.close();
                serviceRequestStmt.close();
                return false; // Appointment ID is invalid
            }

            int serviceRequestId = resultSet.getInt("service_request_id");
            resultSet.close();
            serviceRequestStmt.close();

            // Step 2: Insert the payment record into the payments table
            String paymentQuery = "INSERT INTO payments (service_request_id, amount, payment_method, payment_status) VALUES (?, ?, 'credit_card', 'pending')";
            PreparedStatement paymentStmt = Server.conn.prepareStatement(paymentQuery);
            paymentStmt.setInt(1, serviceRequestId);
            paymentStmt.setDouble(2, amount);

            int rowsAffected = paymentStmt.executeUpdate();
            paymentStmt.close();

            // Step 3: Return true if the payment was successfully recorded
            if (rowsAffected > 0) {
                System.out.println("Payment successfully added for service request ID: " + serviceRequestId);
                return true;
            }

        } catch (SQLException e) {
            System.err.println("An error occurred while adding the payment.");
            e.printStackTrace();
        }

        return false; // Return false if the operation failed
    }

    public String getUsernameByUserId(int userId) {
        String username = null; // Default to null if user not found
        try {
            String query = "SELECT name FROM users WHERE user_id = ?";
            PreparedStatement statement = Server.conn.prepareStatement(query);
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                username = resultSet.getString("name");
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            System.err.println("Failed to fetch username from the database!");
            e.printStackTrace();
        }
        return username;
    }

    public List<AppointmentDetails> getAppointmentsByCredentials(int userId,String UserType) {
        List<AppointmentDetails> appointments = new ArrayList<>();

        try {

            // Step 2: Fetch appointments for this user
            String appointmentQuery = "SELECT a.appointment_id, a.scheduled_time, a.status, s.problem_description " +
                    "FROM appointments a " +
                    "JOIN service_requests s ON a.service_request_id = s.request_id ";
            if (UserType.equals("technician")){
                appointmentQuery +=
                        "WHERE s.technician_id = ?";
            }else {
                appointmentQuery+= "WHERE s.user_id = ?";
            }
            PreparedStatement appointmentStatement = Server.conn.prepareStatement(appointmentQuery);

            appointmentStatement.setInt(1, userId);
            System.out.println(appointmentQuery);
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
