package com.example.gestion_formation;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import jdk.internal.icu.util.CodePointMap;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.ResourceBundle;

public class TrainingController implements Initializable {
    Connection con = null;
    PreparedStatement st = null;
    ResultSet rs = null;


    @FXML
    private Button btnclear;

    @FXML
    private Button btndelete;

    @FXML
    private Button btnsave;

    @FXML
    private Button btnupdate;

    @FXML
    private TextField tcat;

    @FXML
    private TextField tdesc;

    @FXML
    private TextField ted;

    @FXML
    private TextField tnbp;

    @FXML
    private TextField tsd;

    @FXML
    private TextField ttitle;

    @FXML
    private TextField ttrain;

    @FXML
    private TableColumn<Training, Integer> colcat;

    @FXML
    private TableColumn<Training, String> coldesc;

    @FXML
    private TableColumn<Training, Date> colend;

    @FXML
    private TableColumn<Training, Integer> colid;

    @FXML
    private TableColumn<Training, Integer> colnum;

    @FXML
    private TableColumn<Training, Date> colstart;

    @FXML
    private TableColumn<Training, String> coltit;

    @FXML
    private TableColumn<Training, Integer> coltrainer;

    @FXML
    private TableView<Training> tableview;
    int id =0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        showTrainings();

    }
    public ObservableList<Training> getTrainings(){
        ObservableList<Training> trainings = FXCollections.observableArrayList();

        String query = "select * from training";
        con = DBConnexion.getCon();
        try {
            st = con.prepareStatement(query);
            rs = st.executeQuery();
            while(rs.next()){
                Training st = new Training();
                st.setId(rs.getInt("id"));
                st.setTitle(rs.getString("title"));
                st.setDescription(rs.getString("description"));
                st.setStart_date(rs.getDate("start_date"));
                st.setEnd_date(rs.getDate("end_date"));
                st.setTrainer_id(rs.getInt("trainer_id"));
                st.setCategory_id(rs.getInt("category_id"));
                st.setNumber_of_places(rs.getInt("number_of_places"));
                trainings.add(st);

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return trainings;
    }

    public void showTrainings(){
        ObservableList<Training> list = getTrainings();
        tableview.setItems(list);
        colid.setCellValueFactory(new PropertyValueFactory<Training,Integer>("id"));
        coltit.setCellValueFactory(new PropertyValueFactory<Training,String>("title"));
        coldesc.setCellValueFactory(new PropertyValueFactory<Training,String>("description"));
        colstart.setCellValueFactory(new PropertyValueFactory<Training,Date>("start_date"));
        colend.setCellValueFactory(new PropertyValueFactory<Training,Date>("end_date"));
        colnum.setCellValueFactory(new PropertyValueFactory<Training,Integer>("number_of_places"));
        colcat.setCellValueFactory(new PropertyValueFactory<Training,Integer>("category_id"));

    }


    @FXML
    void clearField(ActionEvent event) {
        clear();

    }



    @FXML
    void creatTraining(ActionEvent event) {
        // Vérification des champs obligatoires
        if (ttitle.getText().isEmpty() || tdesc.getText().isEmpty() || tnbp.getText().isEmpty()) {
            showAlert("Erreur de saisie", "Veuillez remplir tous les champs obligatoires.");
            return; // Sortir de la méthode si un champ obligatoire est vide
        }

        // Vérification de la longueur du titre
        if (ttitle.getText().length() < 6) {
            showAlert("Erreur de saisie", "Le titre doit contenir au moins 6 caractères.");
            return; // Sortir de la méthode si le titre est trop court
        }

        // Vérification de la longueur de la description
        if (tdesc.getText().length() < 10) {
            showAlert("Erreur de saisie", "La description doit contenir au moins 10 caractères.");
            return; // Sortir de la méthode si la description est trop courte
        }

        // Vérification du format du nombre de places
        try {
            int numberOfPlaces = Integer.parseInt(tnbp.getText());
            if (numberOfPlaces < 0) {
                showAlert("Erreur de saisie", "Le nombre de places doit être supérieur à zéro.");
                return; // Sortir de la méthode si le nombre de places est invalide
            }
        } catch (NumberFormatException e) {
            showAlert("Erreur de saisie", "Le nombre de places doit être un nombre entier.");
            return; // Sortir de la méthode si le format du nombre de places est invalide
        }

        // Insérer les données dans la base de données
        String insert = "insert into training(title,description,start_date,end_date,number_of_places,category_id,trainer_id) values(?,?,?,?,?,1,19)";
        con = DBConnexion.getCon();
        try {
            st = con.prepareStatement(insert);
            st.setString(1, ttitle.getText());
            st.setString(2, tdesc.getText());
            st.setDate(3, java.sql.Date.valueOf(java.time.LocalDate.now()));
            st.setDate(4, java.sql.Date.valueOf(java.time.LocalDate.now()));
            st.setInt(5, Integer.parseInt(tnbp.getText()));

            st.executeUpdate();
            clear();
            showTrainings();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Méthode pour afficher une boîte de dialogue
    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }




    @FXML
    void getData(MouseEvent event) {
        Training training = tableview.getSelectionModel().getSelectedItem();
        id = training.getId();
        ttitle.setText(training.getTitle());
        tdesc.setText(training.getDescription());
        btnsave.setDisable(true);



    }

    void clear(){
    ttitle.setText(null);
    tdesc.setText(null);
    tsd.setText(null);
    ted.setText(null);
    tnbp.setText(null);
    btnsave.setDisable(false);

    }


    @FXML
    void deleteTraining(ActionEvent event) {

        String delete = "delete from training where id = ?";
        con = DBConnexion.getCon();
        try {
            st = con.prepareStatement(delete);
            st.setInt(1,id);
            st.executeUpdate();
            showTrainings();
            clear();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @FXML
    void updateTraining(ActionEvent event) {

        String update = "update trainings set title = ?, description = ?, start_date = ?, end_date = ?, number_of_places = ?";
        con = DBConnexion.getCon();
        try {
            st = con.prepareStatement(update);
            st.setString(1,ttitle.getText());
            st.setString(2,tdesc.getText());
            st.setDate(3, java.sql.Date.valueOf(java.time.LocalDate.now()));
            st.setDate(4, java.sql.Date.valueOf(java.time.LocalDate.now()));
            st.setInt(5, Integer.parseInt(tnbp.getText()));
            st.executeUpdate();
            showTrainings();
            clear();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }


}

