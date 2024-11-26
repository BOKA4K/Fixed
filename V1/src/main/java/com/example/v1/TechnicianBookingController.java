package com.example.v1;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class TechnicianBookingController implements Initializable {
    private final ServerConnection serverConnection;

    @FXML
    private TableColumn<TechnicianDetails, String> email;

    @FXML
    private TableColumn<TechnicianDetails, String> hourly_rate;

    @FXML
    private TableColumn<TechnicianDetails, String> name;

    @FXML
    private TableColumn<TechnicianDetails, String> skills;

    @FXML
    private TableColumn<TechnicianDetails, String> status;

    @FXML
    private TableView<TechnicianDetails> technicians;

    @FXML
    private TextField Search;

    public TechnicianBookingController(ServerConnection serverConnection) {
        this.serverConnection = serverConnection;
    }

    @FXML
    void Book_Appointment(ActionEvent event) {
        // Logic to book an appointment
    }

    @FXML
    void go_to_Service_Request(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Service Request.fxml"));
        loader.setControllerFactory(param -> new Service_RequestController(serverConnection));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    public static List<TechnicianDetails> convertJsonToTechnicians(String json) {
        List<TechnicianDetails> technicians = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(json);

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String name = jsonObject.getString("name");
            String email = jsonObject.getString("email");
            String skills = jsonObject.getString("skills");
            String hourlyRate = jsonObject.getString("hourlyRate");
            String status = jsonObject.getString("status");

            TechnicianDetails technician = new TechnicianDetails(name, email, skills, hourlyRate, status);
            technicians.add(technician);
        }

        return technicians;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Send request to server for technician details
        serverConnection.sendMessage("TechnicianDetails");

        try {
            String response = serverConnection.receiveMessage(); // Receive technician data from server

            // Convert JSON response to list of TechnicianDetails
            List<TechnicianDetails> technicianList = convertJsonToTechnicians(response);

            // Set up TableView columns and populate data
            technicians.getItems().setAll(technicianList);
        } catch (IOException e) {
            throw new RuntimeException("Error receiving technician data from server", e);
        }
    }
}
