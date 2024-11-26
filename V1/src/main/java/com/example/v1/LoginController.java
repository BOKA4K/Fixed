package com.example.v1;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
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
    void SgnUp_Page(ActionEvent event) {

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
