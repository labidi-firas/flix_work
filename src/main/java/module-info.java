module com.example.gestion_formation {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.gestion_formation to javafx.fxml;
    exports com.example.gestion_formation;
}