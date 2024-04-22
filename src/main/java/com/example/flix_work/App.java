package com.example.flix_work;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.scene.Parent;
public class App extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception {

        Parent root = (Parent) FXMLLoader.load(this.getClass().getResource("/FXML/Job.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setTitle("Job");
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);

    }


}
