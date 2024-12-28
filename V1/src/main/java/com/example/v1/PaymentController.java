package com.example.v1;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;

public class PaymentController {

    private final ServerConnection serverConnection;
    private final String userType;

    public PaymentController(ServerConnection serverConnection, String userType) {
        this.serverConnection = serverConnection;
        this.userType = userType;
    }

    @FXML
    private TableColumn<AppointmentDetails, Integer> Appointment;

    @FXML
    private TableColumn<AppointmentDetails, String> Appointment_Description;

    @FXML
    private TableColumn<AppointmentDetails, Double> Amount;

    @FXML
    private TableView<AppointmentDetails> appointmentTable;

    @FXML
    void Pay(ActionEvent event) {
        AppointmentDetails selectedAppointment = appointmentTable.getSelectionModel().getSelectedItem();
        if (selectedAppointment != null) {
            showPaymentDialog(selectedAppointment.getAppointmentId());
        } else {
            showAlert("No Appointment Selected", "Please select an appointment to process the payment.", Alert.AlertType.WARNING);
        }
    }

    @FXML
    void go_to_dashboard(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Dashboard.fxml"));
        loader.setControllerFactory(param -> new DashboardController(serverConnection, userType));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    private void showPaymentDialog(int appointmentId) {
        TextInputDialog paymentDialog = new TextInputDialog();
        paymentDialog.setTitle("Payment Required");
        paymentDialog.setHeaderText("Enter the payment amount:");
        paymentDialog.setContentText("Amount:");

        paymentDialog.showAndWait().ifPresent(input -> {
            try {
                double amount = Double.parseDouble(input);
                processPayment(appointmentId, amount);
            } catch (NumberFormatException e) {
                showAlert("Invalid Input", "Please enter a valid numeric amount.", Alert.AlertType.ERROR);
            } catch (IOException e) {
                showAlert("Error", "Failed to process payment. Please try again.", Alert.AlertType.ERROR);
            }
        });
    }

    private void processPayment(int appointmentId, double amount) throws IOException {
        serverConnection.sendMessage("addPayment");
        serverConnection.sendMessage(String.valueOf(appointmentId));
        serverConnection.sendMessage(String.valueOf(amount));

        String result = serverConnection.receiveMessage();
        if ("payment added".equals(result)) {
            showAlert("Payment Successful", "The payment has been processed successfully.", Alert.AlertType.INFORMATION);
        } else {
            showAlert("Payment Failed", "Failed to add payment. Please try again.", Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void initialize() {

    }
}
