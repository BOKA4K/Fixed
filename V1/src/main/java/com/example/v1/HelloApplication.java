package com.example.v1;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.*;


public class HelloApplication extends Application {
    private ServerConnection serverConnection;

    @Override
    public void start(Stage stage) throws IOException {
        serverConnection = new ServerConnection();
        try {
            serverConnection.connect("localhost", 9999);
        } catch (IOException e) {
            showErrorDialog(e.getMessage());
        }
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("LOGIN.fxml"));
        fxmlLoader.setControllerFactory(param -> new LoginController(serverConnection));
        Scene scene = new Scene(fxmlLoader.load(), 400, 400);
        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();
    }

    private void showErrorDialog(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Connection Error");
            alert.setHeaderText("Failed to connect to the server");
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    @Override
    public void stop() throws Exception {
        if (serverConnection != null) {
            serverConnection.close();
        }
        super.stop();
    }
}
