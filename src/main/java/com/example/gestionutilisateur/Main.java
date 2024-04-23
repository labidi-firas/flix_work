// App.java
package com.example.gestionutilisateur;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Charger le fichier FXML de la page de connexion
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/LoginForm.fxml"));
            Parent root = loader.load();

            // Configurer la scène avec le contenu chargé
            Scene scene = new Scene(root);

            // Définir la scène sur la fenêtre principale
            primaryStage.setScene(scene);
            primaryStage.setTitle("Application de gestion des utilisateurs");
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
