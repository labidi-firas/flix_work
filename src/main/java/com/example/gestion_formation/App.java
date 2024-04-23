package com.example.gestion_formation;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Parent parent  = FXMLLoader.load(getClass().getResource("/Fxml/Trainings.fxml"));
        Scene scene = new Scene(parent);
        stage.setTitle("CRUD");
        stage.setScene(scene);
        stage.show();

    }

    public static void main(String[] args) {
        launch();
    }
}