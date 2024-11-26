package com.example.v1;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
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

    public Service_overviewController(ServerConnection serverConnection) {
        this.serverConnection = serverConnection;
    }

    @FXML
    private TableView<AppointmentDetails> appointmentTable;

    @FXML
    private TableColumn<AppointmentDetails, String> Appointment;

    @FXML
    private TableColumn<AppointmentDetails, String> Appointment_Description;

    @FXML
    private TableColumn<AppointmentDetails, String> Appointment_Status;

    @FXML
    private TableColumn<AppointmentDetails, String> scheduledTime;

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
    void Cancel_appointment(ActionEvent event) {
        // Implementation to cancel appointment
    }

    @FXML
    void go_to_dashboard(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Dashboard.fxml"));
        loader.setControllerFactory(param -> new DashboardController(serverConnection));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Send request for service overview data
        serverConnection.sendMessage("Service_overview");

        try {
            // Receive response as a JSON string
            String response = serverConnection.receiveMessage();

            // Convert the response string to a JSONArray
            JSONArray appointmentsJsonArray = new JSONArray(response);

            // Convert JSONArray to List<AppointmentDetails>
            List<AppointmentDetails> appointmentsList = jsonToAppointmentsList(appointmentsJsonArray);

            // Create an ObservableList to be used by the TableView
            ObservableList<AppointmentDetails> appointmentsObservableList = FXCollections.observableArrayList(appointmentsList);

            // Set up the TableView with the data
            appointmentTable.setItems(appointmentsObservableList);

            // Bind the TableView columns to the AppointmentDetails properties

            Appointment_Description.setCellValueFactory(cellData -> cellData.getValue().problemDescriptionProperty());
            Appointment_Status.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
            scheduledTime.setCellValueFactory(cellData -> cellData.getValue().scheduledTimeProperty());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
