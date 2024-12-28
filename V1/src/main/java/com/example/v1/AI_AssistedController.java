package com.example.v1;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class AI_AssistedController {
    private final ServerConnection serverConnection;
    private final String UserType;

    public AI_AssistedController(ServerConnection serverConnection,String UserType) {
        this.serverConnection = serverConnection;
        this.UserType=UserType;
    }

    @FXML
    private Label ai_respons;

    @FXML
    private TextField proplem_description;

    @FXML
    private Label recommend_technician;

    @FXML
    void Get_Recommendation(ActionEvent event) throws IOException {
        serverConnection.sendMessage("Ai-assisted");

        serverConnection.sendMessage(proplem_description.getText());
        String response=serverConnection.receiveMessage();
        ai_respons.setText(response);
    }


    @FXML
    void go_to_Service_Request(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Service Request.fxml"));
        loader.setControllerFactory(param -> new Service_RequestController(serverConnection,UserType));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();

    }

}
