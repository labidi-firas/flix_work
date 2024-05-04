package com.example.gestion_formation;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.awt.Desktop;
import java.io.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class TrainingController implements Initializable {
    Connection con = null;
    PreparedStatement st = null;
    ResultSet rs = null;

    @FXML
    private Button btnclear;

    @FXML
    private Button btncat_tra;

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
    private TableColumn<Training, LocalDate> colend;

    @FXML
    private TableColumn<Training, Integer> colid;

    @FXML
    private TableColumn<Training, Integer> colnum;

    @FXML
    private TableColumn<Training, LocalDate> colstart;

    @FXML
    private TableColumn<Training, String> coltit;

    @FXML
    private TableColumn<Training, Integer> coltrainer;

    @FXML
    private TableView<Training> tableview;

    int id = 0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        showTrainings();
    }

    public ObservableList<Training> getTrainings() {
        ObservableList<Training> trainings = FXCollections.observableArrayList();

        String query = "SELECT * FROM training";
        con = DBConnexion.getCon();
        try {
            st = con.prepareStatement(query);
            rs = st.executeQuery();
            while (rs.next()) {
                Training st = new Training();
                st.setId(rs.getInt("id"));
                st.setTitle(rs.getString("title"));
                st.setDescription(rs.getString("description"));
                st.setStart_date(rs.getDate("start_date").toLocalDate());
                st.setEnd_date(rs.getDate("end_date").toLocalDate());
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

    public void showTrainings() {
        ObservableList<Training> list = getTrainings();
        tableview.setItems(list);
        colid.setCellValueFactory(new PropertyValueFactory<Training,Integer>("id"));
        coltit.setCellValueFactory(new PropertyValueFactory<Training,String>("title"));
        coldesc.setCellValueFactory(new PropertyValueFactory<Training,String>("description"));
        colstart.setCellValueFactory(new PropertyValueFactory<Training,LocalDate>("start_date"));
        colend.setCellValueFactory(new PropertyValueFactory<Training,LocalDate>("end_date"));
        colnum.setCellValueFactory(new PropertyValueFactory<Training,Integer>("number_of_places"));
        colcat.setCellValueFactory(new PropertyValueFactory<Training,Integer>("category_id"));
    }

    @FXML
    void clearField(ActionEvent event) {
        clear();
    }

    @FXML
    void creatTraining(ActionEvent event) {
        if (ttitle.getText().isEmpty() || tdesc.getText().isEmpty() || tnbp.getText().isEmpty()) {
            showAlert("Erreur de saisie", "Veuillez remplir tous les champs obligatoires.");
            return;
        }

        if (ttitle.getText().length() < 6) {
            showAlert("Erreur de saisie", "Le titre doit contenir au moins 6 caractères.");
            return;
        }

        if (tdesc.getText().length() < 10) {
            showAlert("Erreur de saisie", "La description doit contenir au moins 10 caractères.");
            return;
        }

        try {
            int numberOfPlaces = Integer.parseInt(tnbp.getText());
            if (numberOfPlaces < 0) {
                showAlert("Erreur de saisie", "Le nombre de places doit être supérieur à zéro.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Erreur de saisie", "Le nombre de places doit être un nombre entier.");
            return;
        }

        String insert = "INSERT INTO training (title, description, start_date, end_date, number_of_places, category_id, trainer_id) VALUES (?, ?, ?, ?, ?, 1, 19)";
        con = DBConnexion.getCon();
        try {
            st = con.prepareStatement(insert);
            st.setString(1, ttitle.getText());
            st.setString(2, tdesc.getText());

            LocalDate startDate = LocalDate.of(2024, 5, 4);
            LocalDate endDate = LocalDate.of(2024, 5, 10);
            st.setDate(3, java.sql.Date.valueOf(startDate));
            st.setDate(4, java.sql.Date.valueOf(endDate));

            st.setInt(5, Integer.parseInt(tnbp.getText()));

            st.executeUpdate();
            clear();
            showTrainings();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
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

    void clear() {
        ttitle.setText(null);
        tdesc.setText(null);
        tsd.setText(null);
        ted.setText(null);
        tnbp.setText(null);
        btnsave.setDisable(false);
    }

    @FXML
    void deleteTraining(ActionEvent event) {
        String delete = "DELETE FROM training WHERE id = ?";
        con = DBConnexion.getCon();
        try {
            st = con.prepareStatement(delete);
            st.setInt(1, id);
            st.executeUpdate();
            showTrainings();
            clear();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void updateTraining(ActionEvent event) {
        String update = "UPDATE training SET title = ?, description = ?, start_date = ?, end_date = ?, number_of_places = ? WHERE id = ?";
        con = DBConnexion.getCon();
        try {
            st = con.prepareStatement(update);
            st.setString(1, ttitle.getText());
            st.setString(2, tdesc.getText());

            // Convertir les champs de date en LocalDate
            LocalDate startDate = LocalDate.parse(tsd.getText());
            LocalDate endDate = LocalDate.parse(ted.getText());

            // Utiliser les dates pour la mise à jour
            st.setDate(3, java.sql.Date.valueOf(startDate));
            st.setDate(4, java.sql.Date.valueOf(endDate));

            st.setInt(5, Integer.parseInt(tnbp.getText()));
            st.setInt(6, id);
            st.executeUpdate();
            showTrainings();
            clear();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void generatePDF(ActionEvent actionEvent) {
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream("C:\\Users\\Latrache Hedi\\Desktop\\flixwork-java\\trainings.pdf"));
            document.open();
            PdfPTable table = new PdfPTable(7); // Nombre de colonnes
            // Ajoutez les en-têtes de colonne
            table.addCell("ID");
            table.addCell("Titre");
            table.addCell("Description");
            table.addCell("Date de début");
            table.addCell("Date de fin");
            table.addCell("Nombre de places");
            table.addCell("Catégorie");
            // Ajoutez les données de la liste de formations à la table
            for (Training training : tableview.getItems()) {
                table.addCell(String.valueOf(training.getId()));
                table.addCell(training.getTitle());
                table.addCell(training.getDescription());
                table.addCell(training.getStart_date().toString());
                table.addCell(training.getEnd_date().toString());
                table.addCell(String.valueOf(training.getNumber_of_places()));
                table.addCell(String.valueOf(training.getCategory_id()));
            }
            document.add(table);
            document.close();
            File pdfFile = new File("C:\\Users\\Latrache Hedi\\Desktop\\flixwork-java\\trainings.pdf");
            Desktop.getDesktop().open(pdfFile);
            showAlert("Succès", "Le fichier PDF a été généré avec succès.");
        } catch (DocumentException | IOException e) {
            showAlert("Erreur", "Une erreur est survenue lors de la génération du fichier PDF.");
            e.printStackTrace();
        }
    }

    @FXML
    void generateEXEL(ActionEvent actionEvent) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Trainings");
        int rownum = 0;
        Row row = sheet.createRow(rownum++);
        // Ajoutez les en-têtes de colonne
        row.createCell(0).setCellValue("ID");
        row.createCell(1).setCellValue("Titre");
        row.createCell(2).setCellValue("Description");
        row.createCell(3).setCellValue("Date de début");
        row.createCell(4).setCellValue("Date de fin");
        row.createCell(5).setCellValue("Nombre de places");
        row.createCell(6).setCellValue("Catégorie");
        // Ajoutez les données de la liste de formations à la feuille de calcul
        for (Training training : tableview.getItems()) {
            row = sheet.createRow(rownum++);
            row.createCell(0).setCellValue(training.getId());
            row.createCell(1).setCellValue(training.getTitle());
            row.createCell(2).setCellValue(training.getDescription());
            row.createCell(3).setCellValue(training.getStart_date().toString());
            row.createCell(4).setCellValue(training.getEnd_date().toString());
            row.createCell(5).setCellValue(training.getNumber_of_places());
            row.createCell(6).setCellValue(training.getCategory_id());
        }
        try {
            FileOutputStream out = new FileOutputStream(new File("C:\\Users\\Latrache Hedi\\Desktop\\flixwork-java\\trainings.xlsx"));
            workbook.write(out);
            out.close();
            File excelFile = new File("C:\\Users\\Latrache Hedi\\Desktop\\flixwork-java\\trainings.xlsx");
            Desktop.getDesktop().open(excelFile);
            showAlert("Succès", "Le fichier Excel a été généré avec succès.");
        } catch (IOException e) {
            showAlert("Erreur", "Une erreur est survenue lors de la génération du fichier Excel.");
            e.printStackTrace();
        }
    }

    public void gotocat(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/TrainingCategory.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
