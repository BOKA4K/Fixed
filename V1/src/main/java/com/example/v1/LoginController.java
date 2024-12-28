package com.example.v1;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class LoginController {
    private final ServerConnection serverConnection;

    @FXML
    private TextField UserName;

    @FXML
    private PasswordField Password;

    @FXML
    private Label error;


    public LoginController(ServerConnection serverConnection) {
        this.serverConnection = serverConnection;
    }
    @FXML
    void SgnUp_Page(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("SignUp.fxml"));
        loader.setControllerFactory(param -> new SignupController(serverConnection));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
    void go_to_Dashboard(ActionEvent event,String UserType) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Dashboard.fxml"));
        loader.setControllerFactory(param -> new DashboardController(serverConnection,UserType));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
    @FXML
    void Login(ActionEvent event) throws IOException {
        String name = UserName.getText();
        String pass = Password.getText();
        serverConnection.sendMessage("login");
        serverConnection.sendMessage(name);
        serverConnection.sendMessage(pass);
        String result = serverConnection.receiveMessage();
        if ("Login successful".equals(result)) {
            System.out.println("Login done");
            serverConnection.sendMessage("get_UserType");
            go_to_Dashboard(event,serverConnection.receiveMessage());


        } else if (name.isEmpty() && pass.isEmpty()) {
            error.setText("Please fill the fields correctly");
        } else {
            error.setText("Login Failed");
        }
    }

}
