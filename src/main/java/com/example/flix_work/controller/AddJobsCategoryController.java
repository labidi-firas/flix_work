package com.example.flix_work.controller;

import com.example.flix_work.dao.DbConnect;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AddJobsCategoryController implements Initializable {

    @FXML
    private TextField categoryNameField;

    @FXML
    private Button addButton;

    @FXML
    private Button deleteButton; // New button for delete operation

    private ObservableList<String> categoryList;

    private Connection connection;
    private PreparedStatement preparedStatement;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        categoryList = FXCollections.observableArrayList();
        populateCategoryList();
    }

    // Method to populate the category list
    private void populateCategoryList() {
        try {
            // Fetch categories from the database
            connection = DbConnect.getConnect();
            String sql = "SELECT category_name FROM jobs_category";
            preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                categoryList.add(resultSet.getString("category_name"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(AddJobsCategoryController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void handleAddCategory(ActionEvent event) {
        String categoryName = categoryNameField.getText().trim();
        if (!categoryName.isEmpty()) {
            try {
                String query = "INSERT INTO jobs_category (category_name) VALUES (?)";
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, categoryName);
                preparedStatement.executeUpdate();
                // Refresh category list
                categoryList.add(categoryName);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText(null);
                alert.setContentText("Category added successfully");
                alert.showAndWait();
            } catch (SQLException ex) {
                Logger.getLogger(AddJobsCategoryController.class.getName()).log(Level.SEVERE, null, ex);
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText(null);
                alert.setContentText("Error occurred while adding category: " + ex.getMessage());
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Category name cannot be empty!");
            alert.showAndWait();
        }
    }

    @FXML
    private void handleClear(ActionEvent event) {
        categoryNameField.clear();
    }

    @FXML
    private void handleDeleteCategory(ActionEvent event) {
        String selectedCategory = categoryNameField.getText().trim();
        if (!selectedCategory.isEmpty()) {
            try {
                String query = "DELETE FROM jobs_category WHERE category_name = ?";
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, selectedCategory);
                preparedStatement.executeUpdate();
                // Refresh category list
                categoryList.remove(selectedCategory);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText(null);
                alert.setContentText("Category deleted successfully");
                alert.showAndWait();
            } catch (SQLException ex) {
                Logger.getLogger(AddJobsCategoryController.class.getName()).log(Level.SEVERE, null, ex);
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText(null);
                alert.setContentText("Error occurred while deleting category: " + ex.getMessage());
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Please select a category to delete!");
            alert.showAndWait();
        }
    }
}
