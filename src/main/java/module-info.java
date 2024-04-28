module com.example.flix_work {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.flix_work to javafx.fxml;
    exports com.example.flix_work;

    opens com.example.flix_work.controller to javafx.fxml;
    exports com.example.flix_work.controller;
    exports com.example.flix_work.entity;
    opens com.example.flix_work.entity to javafx.fxml;
    exports com.example.flix_work.dao;
    opens com.example.flix_work.dao to javafx.fxml;


}