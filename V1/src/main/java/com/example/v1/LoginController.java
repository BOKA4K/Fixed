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
        // Create the FXMLLoader and load the SignUp.fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("SignUp.fxml"));

        // Create an instance of the SignUpController with the ServerConnection

        // Set the controller for the loader
        loader.setControllerFactory(param -> new LoginController(serverConnection));

        // Load the root from the FXML file
        Parent root = loader.load();

        // Get the current stage from the event source
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // Set the scene to the signup page
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
        } else if (name.isEmpty() && pass.isEmpty()) {
            error.setText("Please fill the fields correctly");
        } else {
            error.setText("Login Failed");
        }
    }

}
