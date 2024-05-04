package com.example.gestion_formation;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
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
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class TrainingCategoryController implements Initializable {

    public Button btnsave;
    public Button btnupdate;
    public Button btnclear;
    public Button btndelete;
    @FXML
    private TextField tcategory;

    @FXML
    private TableColumn<TrainingCategory, Integer> colid;

    @FXML
    private TableColumn<TrainingCategory, String> colcategory;

    @FXML
    private TableView<TrainingCategory> tableview;

    private int id = 0;

    private Connection con = null;
    private PreparedStatement st = null;
    private ResultSet rs = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        showTrainingCategories();
    }

    public ObservableList<TrainingCategory> getTrainingCategories() {
        ObservableList<TrainingCategory> categories = FXCollections.observableArrayList();

        String query = "SELECT * FROM training_category";
        try {
            con = DBConnexion.getCon();
            st = con.prepareStatement(query);
            rs = st.executeQuery();
            while (rs.next()) {
                TrainingCategory category = new TrainingCategory();
                category.setId(rs.getInt("id"));
                category.setCategory_name(rs.getString("category_name"));
                categories.add(category);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            closeResources();
        }

        return categories;
    }

    public void showTrainingCategories() {
        ObservableList<TrainingCategory> list = getTrainingCategories();
        tableview.setItems(list);
        colid.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getId()).asObject());
        colcategory.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCategory_name()));
    }

    @FXML
    void clearField(ActionEvent event) {
        clear();
    }

    @FXML
    void createTrainingCategory(ActionEvent event) {
        String categoryName = tcategory.getText().trim();
        if (categoryName.isEmpty()) {
            showAlert("Erreur de saisie", "Veuillez entrer le nom de la catégorie.");
            return;
        }

        if (categoryName.length() < 5) {
            showAlert("Erreur de saisie", "Le nom de la catégorie doit contenir au moins 5 caractères.");
            return;
        }

        String insert = "INSERT INTO training_category(category_name) VALUES (?)";
        try {
            con = DBConnexion.getCon();
            st = con.prepareStatement(insert);
            st.setString(1, categoryName);
            st.executeUpdate();
            clear();
            showTrainingCategories();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            closeResources();
        }
    }

    @FXML
    void getData(MouseEvent event) {
        TrainingCategory category = tableview.getSelectionModel().getSelectedItem();
        if (category != null) {
            id = category.getId();
            tcategory.setText(category.getCategory_name());
        }
    }

    void clear() {
        tcategory.setText("");
    }

    @FXML
    void deleteTrainingCategory(ActionEvent event) {
        String delete = "DELETE FROM training_category WHERE id = ?";
        try {
            con = DBConnexion.getCon();
            st = con.prepareStatement(delete);
            st.setInt(1, id);
            st.executeUpdate();
            showTrainingCategories();
            clear();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            closeResources();
        }
    }

    @FXML
    void updateTrainingCategory(ActionEvent event) {
        String categoryName = tcategory.getText().trim();
        if (categoryName.isEmpty()) {
            showAlert("Erreur de saisie", "Veuillez entrer le nom de la catégorie.");
            return;
        }

        if (categoryName.length() < 5) {
            showAlert("Erreur de saisie", "Le nom de la catégorie doit contenir au moins 5 caractères.");
            return;
        }

        // Récupérer la catégorie sélectionnée
        TrainingCategory selectedCategory = tableview.getSelectionModel().getSelectedItem();
        if (selectedCategory == null) {
            showAlert("Erreur", "Aucune catégorie sélectionnée.");
            return;
        }

        // Vérifier si le nom de la catégorie a été modifié
        if (categoryName.equals(selectedCategory.getCategory_name())) {
            showAlert("Information", "Le nom de la catégorie n'a pas été modifié.");
            return;
        }

        String update = "UPDATE training_category SET category_name = ? WHERE id = ?";
        try {
            con = DBConnexion.getCon();
            st = con.prepareStatement(update);
            st.setString(1, categoryName);
            st.setInt(2, selectedCategory.getId());
            st.executeUpdate();
            showTrainingCategories();
            clear();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            closeResources();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void closeResources() {
        try {
            if (rs != null) rs.close();
            if (st != null) st.close();
            if (con != null) con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void generatePDF1(ActionEvent actionEvent) {
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream("C:\\Users\\Latrache Hedi\\Desktop\\flixwork-java\\categories.pdf"));
            document.open();
            PdfPTable table = new PdfPTable(2); // Number of columns
            // Add column headers
            table.addCell("ID");
            table.addCell("Catégorie");
            // Add data from the categories list to the table
            for (TrainingCategory category : tableview.getItems()) {
                table.addCell(String.valueOf(category.getId()));
                table.addCell(category.getCategory_name());
            }
            document.add(table);
            document.close();
            File pdfFile = new File("C:\\Users\\Latrache Hedi\\Desktop\\flixwork-java\\categories.pdf");
            Desktop.getDesktop().open(pdfFile);
            showAlert("Succès", "Le fichier PDF des catégories a été généré avec succès.");
        } catch (DocumentException | IOException e) {
            showAlert("Erreur", "Une erreur est survenue lors de la génération du fichier PDF.");
            e.printStackTrace();
        }
    }

    @FXML
    void generateEXEL1(ActionEvent actionEvent) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Categories");
        int rownum = 0;
        Row row = sheet.createRow(rownum++);
        // Add column headers
        row.createCell(0).setCellValue("ID");
        row.createCell(1).setCellValue("Catégorie");
        // Add data from the categories list to the worksheet
        for (TrainingCategory category : tableview.getItems()) {
            row = sheet.createRow(rownum++);
            row.createCell(0).setCellValue(category.getId());
            row.createCell(1).setCellValue(category.getCategory_name());
        }
        try {
            FileOutputStream out = new FileOutputStream(new File("C:\\Users\\Latrache Hedi\\Desktop\\flixwork-java\\categories.xlsx"));
            workbook.write(out);
            out.close();
            File excelFile = new File("C:\\Users\\Latrache Hedi\\Desktop\\flixwork-java\\categories.xlsx");
            Desktop.getDesktop().open(excelFile);
            showAlert("Succès", "Le fichier Excel des catégories a été généré avec succès.");
        } catch (IOException e) {
            showAlert("Erreur", "Une erreur est survenue lors de la génération du fichier Excel.");
            e.printStackTrace();
        }
    }

    public void backTo(ActionEvent actionEvent) {
        try {
            // Charger la vue des formations (trainings.fxml)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/trainings.fxml"));
            Parent root = loader.load();

            // Créer une nouvelle scène avec la vue des formations
            Scene scene = new Scene(root);

            // Obtenir la fenêtre actuelle
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

            // Afficher la nouvelle scène
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




}






