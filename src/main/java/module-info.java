module com.example.gestion_formation {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;
    requires itextpdf;
    requires java.desktop;


    opens com.example.gestion_formation to javafx.fxml;
    exports com.example.gestion_formation;
}