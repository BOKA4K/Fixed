package com.example.guiserver;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import static com.example.guiserver.Server.main1;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("server_home.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 700, 600);
        stage.setTitle("DashBoard");
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> {
            // Terminate the application when the user closes the window
            Platform.exit();
            System.out.println("server closed");
            System.exit(0);
        });
        stage.show();

    }

    public static void main(String[] args) throws InterruptedException {
        Thread thread= new Thread(() -> {
        main1();
        });
        thread.start();
        Thread.sleep(1000);
        launch();

    }
}