package com.example.guiserver;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Duration;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.HashMap;

import static com.example.guiserver.Server.conn;

public class ServerHome implements Initializable {

    private static final String ONLINE_STATUS = "online";
    private static final String OFFLINE_STATUS = "offline";
    private static final String AWAY_STATUS = "away";

    private ObservableList<User> personList = FXCollections.observableArrayList();

    @FXML
    private TableView<User> onlinepersons;
    @FXML
    private TableView<User> offlinepersons;
    @FXML
    private TableView<User> awaypersons;

    @FXML
    private TableColumn<User, String> awayName;
    @FXML
    private TableColumn<User, String> awaystatus;
    @FXML
    private TableColumn<User, String> offlineName;
    @FXML
    private TableColumn<User, String> offlinestatus;
    @FXML
    private TableColumn<User, String> onlineName;
    @FXML
    private TableColumn<User, String> onlinestatus;

    @FXML
    void Go_To_Home(ActionEvent event) throws SQLException {
        // Navigation logic, if any
    }

    @FXML
    void refresh(ActionEvent event) {
        loadUserData();
    }

    private void loadUserData() {
        personList.clear(); // Clear existing data
        String sql = "SELECT name, status FROM users";

        try (PreparedStatement statement = conn.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String username = resultSet.getString("name");
                String status = resultSet.getString("status");
                personList.add(new User(username, status));
            }

            updateTableViews(groupUsersByStatus());

        } catch (SQLException e) {
            e.printStackTrace(); // Consider logging the error instead
        }
    }

    private Map<String, ObservableList<User>> groupUsersByStatus() {
        Map<String, ObservableList<User>> statusMap = new HashMap<>();
        statusMap.put(ONLINE_STATUS, FXCollections.observableArrayList());
        statusMap.put(OFFLINE_STATUS, FXCollections.observableArrayList());
        statusMap.put(AWAY_STATUS, FXCollections.observableArrayList());

        for (User person : personList) {
            String status = person.getStatus();
            if (statusMap.containsKey(status)) {
                statusMap.get(status).add(person);
            }
        }

        return statusMap;
    }

    private void updateTableViews(Map<String, ObservableList<User>> statusMap) {
        // Set the items for each table
        onlinepersons.setItems(statusMap.get(ONLINE_STATUS));
        offlinepersons.setItems(statusMap.get(OFFLINE_STATUS));
        awaypersons.setItems(statusMap.get(AWAY_STATUS));

        // Set the column mappings once, in the initialize method
        if (onlinepersons.getItems().isEmpty()) {
            initializeColumns(); // Initialize column bindings only once
        }
    }

    private void initializeColumns() {
        onlineName.setCellValueFactory(new PropertyValueFactory<>("name"));
        onlinestatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        offlineName.setCellValueFactory(new PropertyValueFactory<>("name"));
        offlinestatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        awayName.setCellValueFactory(new PropertyValueFactory<>("name"));
        awaystatus.setCellValueFactory(new PropertyValueFactory<>("status"));


    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeColumns(); // Initialize the table columns
        loadUserData(); // Load initial data
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0.3), e -> loadUserData())
        );
        timeline.setCycleCount(Timeline.INDEFINITE); // Loop indefinitely
        timeline.play(); // Start the Timeline
    }
}
