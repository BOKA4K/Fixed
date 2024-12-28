package com.example.v1;

import javafx.beans.property.SimpleObjectProperty;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class Service_overviewController implements Initializable {
    private final ServerConnection serverConnection;
    private final String UserType;


    public Service_overviewController(ServerConnection serverConnection,String UserType) {
        this.serverConnection = serverConnection;
        this.UserType=UserType;
    }

    @FXML
    private TableColumn<AppointmentDetails, Integer> Appointment;

    @FXML
    private TableColumn<AppointmentDetails, String> Appointment_Description;

    @FXML
    private TableColumn<AppointmentDetails, String> Appointment_Status;

    @FXML
    private TableColumn<AppointmentDetails, String> scheduledTime;

    @FXML
    private TableView<AppointmentDetails> appointmentTable;

    public List<AppointmentDetails> jsonToAppointmentsList(JSONArray appointmentsJsonArray) {
        List<AppointmentDetails> appointmentsList = new ArrayList<>();

        // Iterate over the JSON array and convert each JSONObject to AppointmentDetails
        for (int i = 0; i < appointmentsJsonArray.length(); i++) {
            JSONObject appointmentJson = appointmentsJsonArray.getJSONObject(i);

            // Extract data from JSONObject
            int appointmentId = appointmentJson.getInt("appointment_id");
            String scheduledTime = appointmentJson.getString("scheduled_time");
            String status = appointmentJson.getString("status");
            String problemDescription = appointmentJson.getString("problem_description");

            // Create a new AppointmentDetails object and add it to the list
            AppointmentDetails appointment = new AppointmentDetails(appointmentId, scheduledTime, status, problemDescription);
            appointmentsList.add(appointment);
        }

        return appointmentsList;  // Return the list of AppointmentDetails
    }

    @FXML
    void Cancel_appointment(ActionEvent event) throws IOException {
        AppointmentDetails selectedAppointment = appointmentTable.getSelectionModel().getSelectedItem();
        if (selectedAppointment!=null){
        serverConnection.sendMessage("Cancel_appointment");
        serverConnection.sendMessage(String.valueOf(selectedAppointment.getAppointmentId()));
        String result = serverConnection.receiveMessage();
        if (("appointment Canceled".equals(result))){
            System.out.println("appointment cancelled");
            selectedAppointment.setStatus("cancelled");
        }
    }
    }


    @FXML
    void go_to_dashboard(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Dashboard.fxml"));
        loader.setControllerFactory(param -> new DashboardController(serverConnection,UserType));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        serverConnection.sendMessage("Service_overview");
        try {
            // Receive the response containing appointment data
            String response = serverConnection.receiveMessage();

            // Convert the response string to a JSONArray
            JSONArray appointmentsJsonArray = new JSONArray(response);

            // Convert the JSONArray to a list of AppointmentDetails
            List<AppointmentDetails> appointmentsList = jsonToAppointmentsList(appointmentsJsonArray);

            // Bind the list to the TableView
            ObservableList<AppointmentDetails> appointmentsObservableList = FXCollections.observableArrayList(appointmentsList);
            appointmentTable.setItems(appointmentsObservableList);

            // Set up the columns to display the data
            Appointment.setCellValueFactory(cellData -> {
                // Auto-number the rows by returning the row index
                return new SimpleObjectProperty<>(appointmentTable.getItems().indexOf(cellData.getValue()) + 1);
            });
            Appointment_Description.setCellValueFactory(cellData -> cellData.getValue().problemDescriptionProperty());
            Appointment_Status.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
            scheduledTime.setCellValueFactory(cellData -> cellData.getValue().scheduledTimeProperty());
            // Add a double-click listener to the TableView
            if (!UserType.equals("customer"))
            appointmentTable.setOnMouseClicked(this::handleRowDoubleClick);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void showStatusChangeDialog(AppointmentDetails selectedAppointment) {
        // Create a ComboBox with possible status options
        ComboBox<String> statusComboBox = new ComboBox<>();
        statusComboBox.getItems().addAll("Scheduled", "Cancelled", "Completed");

        // Set the current status as the default value
        statusComboBox.setValue(selectedAppointment.getStatus());

        // Create a VBox to contain the ComboBox
        VBox vbox = new VBox(statusComboBox);

        // Create a custom dialog to show the ComboBox
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Change Appointment Status");
        alert.setHeaderText("Choose the new status for the appointment:");
        alert.getDialogPane().setContent(vbox);

        // Show the dialog and wait for the user to confirm the selection
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                String newStatus = statusComboBox.getValue();
                // Update the appointment status locally
                selectedAppointment.setStatus(newStatus);
                try {
                    updateAppointmentStatus(selectedAppointment, newStatus);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                if ("Completed".equals(newStatus)) {

                    showPaymentDialog(selectedAppointment.getAppointmentId());
                }
                // Send the updated status to the server

            }
        });
    }
    private void showPaymentDialog(int appointmentId) {
        TextInputDialog paymentDialog = new TextInputDialog();
        paymentDialog.setTitle("Payment Required");
        paymentDialog.setHeaderText("Enter the payment amount:");
        paymentDialog.setContentText("Amount:");

        paymentDialog.showAndWait().ifPresent(input -> {
            try {
                double amount = Double.parseDouble(input);
                payment(amount,appointmentId);
            } catch (NumberFormatException | IOException e) {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Invalid Input");
                errorAlert.setHeaderText("Invalid amount entered");
                errorAlert.setContentText("Please enter a valid number.");
                errorAlert.showAndWait();
            }
        });
    }
    private void payment(double amount,int appointmentId) throws IOException {
        serverConnection.sendMessage("addPayment");
        serverConnection.sendMessage(String.valueOf(appointmentId));
        serverConnection.sendMessage(String.valueOf(amount));
        String result = serverConnection.receiveMessage();
        if ("payment added".equals(result)){
            System.out.println("payment added successfully");
        }

    }
    private void updateAppointmentStatus(AppointmentDetails selectedAppointment, String newStatus) throws IOException {
        // Send the updated status to the server
        serverConnection.sendMessage("Update_appointment_status");
        serverConnection.sendMessage(String.valueOf(selectedAppointment.getAppointmentId()));
        serverConnection.sendMessage(newStatus);

        // Get the response from the server
        String result = serverConnection.receiveMessage();
        if ("status updated".equals(result)) {
            System.out.println("Status updated successfully");

        } else {
            System.out.println("Failed to update status");
        }
    }
    @FXML
    private void handleRowDoubleClick(MouseEvent event) {
        if (event.getClickCount() == 2) {
            AppointmentDetails selectedAppointment = appointmentTable.getSelectionModel().getSelectedItem();
            if (selectedAppointment != null) {
                showStatusChangeDialog(selectedAppointment);
            }
        }
}}
