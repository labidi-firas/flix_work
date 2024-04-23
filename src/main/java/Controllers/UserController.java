package Controllers;


import java.io.IOException;
import java.sql.Connection;

import com.example.gestionutilisateur.Utils.DBConnexion;
import com.example.gestionutilisateur.entities.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UserController implements Initializable {
    Connection con = null;
    PreparedStatement st = null;
    ResultSet rs = null;

    @FXML
    private Button btnClear;

    @FXML
    private Button btnDelete;

    @FXML
    private Button btnSave;
    @FXML
    private Button btnBan;
    @FXML
    private Button btnSave1;


    @FXML
    private Button btnUpdate;

    @FXML
    private Button btnUnban;
    @FXML
    private PasswordField tPassword;
    @FXML
    private TextField tAdresse;
    @FXML
    private TextField searchField;
    @FXML
    private TextField tEmail;

    @FXML
    private TextField tUsername;
    @FXML
    private TextField tRole;
    @FXML
    private TableColumn<User,String> colAdress;

    @FXML
    private TableColumn<User,String> colEmail;

    @FXML
    private TableColumn<User,Integer> colId;

    @FXML
    private TableColumn<User,String> colUsername;
    @FXML
    private TableColumn<User, String> colPassword;
    @FXML
    private TableColumn<User,String> colRole;
    @FXML
    private TableColumn<User, String> colBanned;

    @FXML
    private TableView<User> table;
    int id=0;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
showUser();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    public ObservableList<User> getUsers(){
        ObservableList<User> user = FXCollections.observableArrayList();
        String query = "select* from user";
        con = (Connection) DBConnexion.getCon();
        try {
            st = con.prepareStatement(query);
            rs = st.executeQuery();
            while (rs.next()){
                User us = new User();
                us.setId(rs.getInt("id"));
                us.setName(rs.getString("name"));
                us.setEmail(rs.getString("email"));
                us.setAddress(rs.getString("address"));
                us.setPassword(rs.getString("password"));
                us.setRole(rs.getString("role"));
                us.setBanned(rs.getString("banned"));





                user.add(us);

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return user;
    }
    public void showUser(){
        ObservableList<User> list = getUsers();
        table.setItems(list);
        colId.setCellValueFactory(new PropertyValueFactory<User,Integer>("id"));
        colUsername.setCellValueFactory(new PropertyValueFactory<User,String>("name"));
        colEmail.setCellValueFactory(new PropertyValueFactory<User,String>("email"));
        colAdress.setCellValueFactory(new PropertyValueFactory<User,String>("address"));
        colPassword.setCellValueFactory(new PropertyValueFactory<User,String>("password"));
        colRole.setCellValueFactory(new PropertyValueFactory<User,String>("role"));
        colBanned.setCellValueFactory(new PropertyValueFactory<User,String>("banned"));
    }
    @FXML
    void clearField(ActionEvent event) {
clear();
    }

    @FXML
    void createUser(ActionEvent event) {
        String password = hashPassword(tPassword.getText());

        String insert = "insert into user(name,email,address,password,role) values(?,?,?,?,?)";
        con = DBConnexion.getCon();
        try {
            PreparedStatement st = con.prepareStatement(insert);
            st.setString(1, tUsername.getText());
            st.setString(2, tEmail.getText());
            st.setString(3, tAdresse.getText());
            st.setString(4, password);
            st.setString(5, "user");
            st.executeUpdate();
            clear();
            showUser();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private String hashPassword(String password) {
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
    @FXML
    void getData(MouseEvent event) {
        User user = table.getSelectionModel().getSelectedItem();
        id = user.getId();
        tUsername.setText(user.getName());
        tEmail.setText(user.getEmail());
        tAdresse.setText(user.getAddress());
        tRole.setText(user.getRole());

        tPassword.setText(user.getPassword());
        btnSave.setDisable(true);

    }
    void clear(){
        tUsername.setText(null);
        tEmail.setText(null);
        tAdresse.setText(null);
        tPassword.setText(null);
        tRole.setText(null);

        btnSave.setDisable(false);
    }

    @FXML
    void deleteUser(ActionEvent event) {
        String delete = "delete from user where id = ?";
        con = DBConnexion.getCon();
        try {
            st = con.prepareStatement(delete);
            st.setInt(1,id);
            st.executeUpdate();
            showUser();
            clear();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @FXML
    void updateUser(ActionEvent event) {
String update ="update user set name=?, email=?,address=?,password=?,role=? where id=?";
con = DBConnexion.getCon();
        try {
            st = con.prepareStatement(update);
            st.setString(1,tUsername.getText());
            st.setString(2,tEmail.getText());
            st.setString(3,tAdresse.getText());
            st.setString(4,tPassword.getText());
            st.setString(5,tRole.getText());

            st.setInt(6,id);
            st.executeUpdate();
            showUser();
            clear();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @FXML
    void LogOut(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/LoginForm.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    void BanUser(ActionEvent event) {
        if (id != 0) {
            if (banUserInDatabase(id)) {
                showAlert("Utilisateur Banni", "L'utilisateur a été banni avec succès.");
                showUser();
                clear();
            } else {
                showAlert("Erreur", "Une erreur s'est produite lors du bannissement de l'utilisateur.");
            }
        } else {
            showAlert("Avertissement", "Veuillez sélectionner un utilisateur à bannir.");
        }
    }

    @FXML
    void unBanUser(ActionEvent event) {
        if (id != 0) {
            if (unBanUserInDatabase(id)) {
                showAlert("Utilisateur Débanni", "L'utilisateur a été débanni avec succès.");
                showUser();
                clear();
            } else {
                showAlert("Erreur", "Une erreur s'est produite lors du débannissement de l'utilisateur.");
            }
        } else {
            showAlert("Avertissement", "Veuillez sélectionner un utilisateur à débannir.");
        }
    }
    private boolean banUserInDatabase(int userId) {
        String updateQuery = "UPDATE user SET banned = ? WHERE id = ?";
        con = DBConnexion.getCon();
        try {
            PreparedStatement statement = con.prepareStatement(updateQuery);
            statement.setBoolean(1, true);
            statement.setInt(2, userId);
            int rowsUpdated = statement.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    private boolean unBanUserInDatabase(int userId) {
        String updateQuery = "UPDATE user SET banned = ? WHERE id = ?";
        con = DBConnexion.getCon();
        try {
            PreparedStatement statement = con.prepareStatement(updateQuery);
            statement.setBoolean(1, false); // Définir le statut de bannissement sur faux pour débannir l'utilisateur
            statement.setInt(2, userId);
            int rowsUpdated = statement.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    @FXML
    void SearchUser(ActionEvent event) {
        String searchText = searchField.getText().toLowerCase();
        ObservableList<User> filteredList = FXCollections.observableArrayList();
        ObservableList<User> userList = getUsers();

        for (User user : userList) {
            if (user.getName().toLowerCase().contains(searchText)) {
                filteredList.add(user);
            }
        }

        table.setItems(filteredList);
    }
    @FXML
    void ClearSearch(ActionEvent event) {
        searchField.clear();
        showUser();
    }







}

