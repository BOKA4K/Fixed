package com.example.v1;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SignupController implements Initializable {
    private final ServerConnection serverConnection;
    @FXML
    private TextField Phone_number;
    @FXML
    private ComboBox<String> Roal;
    @FXML
    private TextField Email;
    @FXML
    private PasswordField Password;
    @FXML
    private TextField UserName;
    @FXML
    private Label error;
    public SignupController(ServerConnection serverConnection) {
        this.serverConnection = serverConnection;
    }
    private boolean areFieldsValid() {
        // Check if any field is null or empty
        if (Phone_number.getText() == null || Phone_number.getText().trim().isEmpty()) {
            return false;
        }
        if (Roal.getValue() == null || Roal.getValue().trim().isEmpty()) {
            return false;
        }
        if (Email.getText() == null || Email.getText().trim().isEmpty()) {
            return false;
        }
        if (Password.getText() == null || Password.getText().trim().isEmpty()) {
            return false;
        }
        if (UserName.getText() == null || UserName.getText().trim().isEmpty()) {
            return false;
        }

        // If none of the fields are null or empty, return true
        return true;
    }
    @FXML
    void SignUp(ActionEvent event) throws IOException {
        if (areFieldsValid()){
        String name = UserName.getText();
        String pass = Password.getText();
        String email = Email.getText();
        String number = Phone_number.getText();
        String selectedRole = Roal.getValue();
        serverConnection.sendMessage("signup");
        serverConnection.sendMessage(name);
        serverConnection.sendMessage(pass);
        serverConnection.sendMessage(email);
        serverConnection.sendMessage(number);
        serverConnection.sendMessage(selectedRole);
        String result = serverConnection.receiveMessage();
        if ("Username registered successfully".equals(result)) {
            error.setText("signup done");
        } else if ("Username already exists".equals(result)) {
            error.setText("Username already exists");
        }else if (name.isEmpty() && pass.isEmpty()) {
            error.setText("Please fill the fields correctly");
        } else {
            error.setText("signup Failed");
        }
    }else {
            error.setText("Fill the Fields Correctly");
        }

    }

    @FXML
    void go_to_Login(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("lOGIN.fxml"));
        loader.setControllerFactory(param -> new LoginController(serverConnection));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Roal.setItems(FXCollections.observableArrayList("customer", "technician"));
    }
}
