module com.example.v1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.json;


    opens com.example.v1 to javafx.fxml;
    exports com.example.v1;
}