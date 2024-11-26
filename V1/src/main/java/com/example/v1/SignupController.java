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

public class SignupController {
    private final ServerConnection serverConnection;

    @FXML
    private PasswordField Password;

    @FXML
    private TextField UserName;

    @FXML
    private Label error;

    public SignupController(ServerConnection serverConnection) {
        this.serverConnection = serverConnection;
    }

    @FXML
    void LoginPage(ActionEvent event) throws IOException {
        // Create the FXMLLoader and load the SignUp.fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("lOGIN.fxml"));

        // Create an instance of the SignUpController with the ServerConnection
        SignupController signUpController = new SignupController(serverConnection);

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
    void SignUp(ActionEvent event) {

    }

}
