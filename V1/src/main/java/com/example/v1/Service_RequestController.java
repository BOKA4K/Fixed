package com.example.v1;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Service_RequestController {
    private final ServerConnection serverConnection;
    private final String UserType;


    public Service_RequestController(ServerConnection serverConnection,String UserType) {
        this.serverConnection = serverConnection;
        this.UserType=UserType;
    }

    @FXML
    void go_to_AI_Assisted(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("AI-Assisted.fxml"));
        loader.setControllerFactory(param -> new AI_AssistedController(serverConnection,UserType));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    void go_to_DashBoard(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Dashboard.fxml"));
        loader.setControllerFactory(param -> new DashboardController(serverConnection,UserType));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    void go_to_manual_booking(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("technicianbook.fxml"));
        loader.setControllerFactory(param -> new TechnicianBookingController(serverConnection,UserType));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}
