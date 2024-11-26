module com.example.guiserver {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.json;


    opens com.example.guiserver to javafx.fxml;
    exports com.example.guiserver;
}