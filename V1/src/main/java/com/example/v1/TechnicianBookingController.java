package com.example.v1;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
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
    private final String UserType;

    @FXML
    private Label error;

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
    private DatePicker date;

    @FXML
    private TextField Search;

    private ObservableList<TechnicianDetails> technicianObservableList;

    public TechnicianBookingController(ServerConnection serverConnection,String UserType) {
        this.serverConnection = serverConnection;
        this.UserType=UserType;
    }
    @FXML
    private TextField problem_description;

    @FXML
    void Book_Appointment(ActionEvent event) throws IOException {
        TechnicianDetails selectedTechnician = technicians.getSelectionModel().getSelectedItem();
        String date_ = String.valueOf(date.getValue());
        System.out.println(date_);

        if (selectedTechnician != null && date_ != null) {
            String email = selectedTechnician.getEmail();
            System.out.println("Selected technician's email: " + email);
            serverConnection.sendMessage("BookAppointment");
            serverConnection.sendMessage(email);
            serverConnection.sendMessage(date_);
            serverConnection.sendMessage(problem_description.getText());
            if (serverConnection.receiveMessage().equals("booking done")) {
                error.setText("Booking done");
            } else {
                error.setText("Booking failed");
            }
        } else {
            System.out.println("No technician selected!");
        }
    }

    @FXML
    void go_to_Service_Request(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Service Request.fxml"));
        loader.setControllerFactory(param -> new Service_RequestController(serverConnection,UserType));
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
        serverConnection.sendMessage("TechnicianDetails");

        try {
            String response = serverConnection.receiveMessage();
            System.out.println(response);

            List<TechnicianDetails> technicianList = convertJsonToTechnicians(response);
            technicianObservableList = FXCollections.observableArrayList(technicianList);

            technicians.setItems(technicianObservableList);

            name.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
            email.setCellValueFactory(cellData -> cellData.getValue().emailProperty());
            skills.setCellValueFactory(cellData -> cellData.getValue().skillsProperty());
            hourly_rate.setCellValueFactory(cellData -> cellData.getValue().hourlyRateProperty());
            status.setCellValueFactory(cellData -> cellData.getValue().statusProperty());

            // Add listener to search field
            Search.textProperty().addListener((observable, oldValue, newValue) -> filterTable(newValue));
        } catch (IOException e) {
            throw new RuntimeException("Error receiving technician data from server", e);
        }
    }

    // Filter table based on search input
    private void filterTable(String searchText) {
        ObservableList<TechnicianDetails> filteredList = FXCollections.observableArrayList();

        // Loop through the original technician list and add matching items to the filtered list
        for (TechnicianDetails technician : technicianObservableList) {
            if (technician.getName().toLowerCase().contains(searchText.toLowerCase()) ||
                    technician.getEmail().toLowerCase().contains(searchText.toLowerCase()) ||
                    technician.getSkills().toLowerCase().contains(searchText.toLowerCase()) ||
                    technician.getHourlyRate().toLowerCase().contains(searchText.toLowerCase()) ||
                    technician.getStatus().toLowerCase().contains(searchText.toLowerCase())) {
                filteredList.add(technician);
            }
        }

        // Update the table view with the filtered list
        technicians.setItems(filteredList);
    }
}
