package com.example.v1;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {
    private final ServerConnection serverConnection;
    private final String UserType;

    public DashboardController(ServerConnection serverConnection,String UserType) {
        this.serverConnection = serverConnection;
        this.UserType=UserType;
        System.out.println(UserType);
    }
    @FXML
    void Go_to_Payment(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("PaymentDashboard.fxml"));
        loader.setControllerFactory(param -> new Service_RequestController(serverConnection,UserType));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    void Go_to_Service_Booking(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Service Request.fxml"));
        loader.setControllerFactory(param -> new Service_RequestController(serverConnection,UserType));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    void Go_to_Service_Overview(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Service Overview.fxml"));
        loader.setControllerFactory(param -> new Service_overviewController(serverConnection,UserType));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    void Logout(ActionEvent event) throws IOException {
        serverConnection.close();
        ServerConnection newserverConnection = new ServerConnection();
        try {
            newserverConnection.connect("localhost", 9999);
        } catch (IOException e) {

        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("lOGIN.fxml"));
        loader.setControllerFactory(param -> new LoginController(newserverConnection));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
    @FXML
    private Button bookingbtn;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (!UserType.equals("customer")){
            bookingbtn.setDisable(true);
            bookingbtn.setVisible(false);
        }
    }
}
