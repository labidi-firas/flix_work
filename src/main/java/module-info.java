module com.example.gestionutilisateur {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires mysql.connector.j;
    requires java.sql;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;

    opens com.example.gestionutilisateur to javafx.fxml;
    exports com.example.gestionutilisateur;
    exports Controllers;
    opens Controllers to javafx.fxml;
    exports com.example.gestionutilisateur.entities;
    opens com.example.gestionutilisateur.entities to javafx.fxml;
    exports com.example.gestionutilisateur.Utils;
    opens com.example.gestionutilisateur.Utils to javafx.fxml;
}