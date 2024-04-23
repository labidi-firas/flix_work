package Controllers;

import com.example.gestionutilisateur.entities.App;
import com.example.gestionutilisateur.Utils.DBConnexion;
import com.example.gestionutilisateur.entities.User;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {
    public TextField txtEmail;
    public PasswordField txtPassword;

    public void btnSignIn(ActionEvent actionEvent) {
        String email = txtEmail.getText();
        String password = txtPassword.getText();

        User authenticatedUser = authenticateUser(email, password);

        if (authenticatedUser != null) {
            if (authenticatedUser.getRole().equals("admin")) {
                openUserManagementApp();
            } else {
                showAlert("Connexion réussie", "Bienvenue !");
            }
        } else {
            showAlert("Échec de connexion", "Adresse e-mail ou mot de passe incorrect.");
        }
    }

    private User authenticateUser(String email, String password) {
        Connection con = DBConnexion.getCon();
        String query = "SELECT * FROM user WHERE email = ?";

        try {
            PreparedStatement preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, email);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String hashedPasswordFromDB = resultSet.getString("password");

                String hashedPassword = PasswordUtils.hashPassword(password);

                if (hashedPassword.equals(hashedPasswordFromDB)) {
                    User user = new User();
                    user.setId(resultSet.getInt("id"));
                    user.setName(resultSet.getString("name"));
                    user.setEmail(resultSet.getString("email"));
                    user.setAddress(resultSet.getString("address"));
                    user.setPassword(resultSet.getString("password"));
                    user.setRole(resultSet.getString("role")); // Récupérer
                    return user;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public class PasswordUtils {

        public static String hashPassword(String password) {
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                byte[] hashedBytes = digest.digest(password.getBytes());
                StringBuilder hexString = new StringBuilder();
                for (byte hashedByte : hashedBytes) {
                    String hex = Integer.toHexString(0xff & hashedByte);
                    if (hex.length() == 1) hexString.append('0');
                    hexString.append(hex);
                }
                return hexString.toString();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
    private void openUserManagementApp() {
        Platform.runLater(() -> {
            App app = new App();
            Stage stage = new Stage();
            try {
                app.start(stage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    public void btnSignUp(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/RegistrationForm.fxml"));
            Parent root = loader.load();

            RegistrationController registrationController = loader.getController();

            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.close();

            Scene scene = new Scene(root);
            Stage newStage = new Stage();
            newStage.setScene(scene);
            newStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




}
