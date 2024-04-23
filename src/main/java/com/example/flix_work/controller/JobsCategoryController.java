package com.example.flix_work.controller;

import com.example.flix_work.dao.DbConnect;
import com.example.flix_work.entity.JobsCategory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;


import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JobsCategoryController implements Initializable {

    @FXML
    private TextField search;
    @FXML
    private TableView<JobsCategory> categoryTable;
    @FXML
    private TableColumn<JobsCategory, Long> idCol;
    @FXML
    private TableColumn<JobsCategory, String> categoryNameCol;
    @FXML
    private TableColumn<JobsCategory, Void> actionsCol; // Updated TableColumn type
    @FXML
    private TextField categoryNameField;
    @FXML
    private Button addButton;

    private ObservableList<JobsCategory> categoryList;
    private Connection connection;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        connection = DbConnect.getConnect();
        categoryList = FXCollections.observableArrayList();
        loadCategoryData();

        idCol.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        categoryNameCol.setCellValueFactory(cellData -> cellData.getValue().categoryNameProperty());

        setupActionsColumn(); // Setting up the Actions column
        categoryTable.setItems(categoryList);

        // Limit input length to 20 characters and allow only alphabetic characters
        UnaryOperator<TextFormatter.Change> filter = change -> {
            if (change.isContentChange()) {
                String newText = change.getControlNewText();
                if (!newText.matches("[a-zA-Z]*") || newText.length() > 20) {
                    return null; // reject change
                }
            }
            return change;
        };
        categoryNameField.setTextFormatter(new TextFormatter<>(filter));
    }

    private void setupActionsColumn() {
        actionsCol.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");
            private final HBox container = new HBox(editButton, deleteButton);

            {
                // Edit button action
                editButton.setOnAction(event -> {
                    JobsCategory category = getTableView().getItems().get(getIndex());
                    handleEditCategory(category);
                });

                // Delete button action
                deleteButton.setOnAction(event -> {
                    JobsCategory category = getTableView().getItems().get(getIndex());
                    handleDeleteCategory(category);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(container);
                }
            }
        });
    }

    @FXML
    private void handleAddCategory() {
        String categoryName = categoryNameField.getText().trim();
        if (!categoryName.isEmpty()) {
            if (categoryList.stream().noneMatch(category -> category.getCategoryName().equalsIgnoreCase(categoryName))) {
                try {
                    String query = "INSERT INTO jobs_category (category_name) VALUES (?)";
                    preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setString(1, categoryName);
                    preparedStatement.executeUpdate();
                    loadCategoryData(); // Refresh table after adding a new category
                    categoryNameField.clear();
                } catch (SQLException ex) {
                    Logger.getLogger(JobsCategoryController.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setHeaderText(null);
                alert.setContentText("Category already exists!");
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Category name cannot be empty!");
            alert.showAndWait();
        }
    }


    private void loadCategoryData() {
        try {
            categoryList.clear();
            String query = "SELECT * FROM jobs_category";
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                categoryList.add(new JobsCategory(resultSet.getLong("id"), resultSet.getString("category_name")));
            }
        } catch (SQLException ex) {
            Logger.getLogger(JobsCategoryController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    private void handleEditCategory(JobsCategory selectedCategory) {
        TextInputDialog dialog = new TextInputDialog(selectedCategory.getCategoryName());
        dialog.setTitle("Edit Category");
        dialog.setHeaderText(null);
        dialog.setContentText("Enter new category name:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(newCategoryName -> {
            if (!newCategoryName.trim().isEmpty()) {
                if (newCategoryName.length() <= 20 && newCategoryName.matches("[a-zA-Z]*")) {
                    if (categoryList.stream().noneMatch(category -> category.getCategoryName().equalsIgnoreCase(newCategoryName))) {
                        try {
                            String query = "UPDATE jobs_category SET category_name = ? WHERE id = ?";
                            preparedStatement = connection.prepareStatement(query);
                            preparedStatement.setString(1, newCategoryName);
                            preparedStatement.setLong(2, selectedCategory.getId());
                            preparedStatement.executeUpdate();
                            loadCategoryData(); // Refresh table after editing a category
                        } catch (SQLException ex) {
                            Logger.getLogger(JobsCategoryController.class.getName()).log(Level.SEVERE, null, ex);
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setHeaderText(null);
                            alert.setContentText("Error occurred while editing category: " + ex.getMessage());
                            alert.showAndWait();
                        }
                    } else {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setHeaderText(null);
                        alert.setContentText("Category already exists!");
                        alert.showAndWait();
                    }
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText(null);
                    alert.setContentText("Category name should be under 20 characters and contain only alphabetic characters!");
                    alert.showAndWait();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText(null);
                alert.setContentText("Category name cannot be empty!");
                alert.showAndWait();
            }
        });
    }


    private void handleDeleteCategory(JobsCategory selectedCategory) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Are you sure you want to delete this category?");
        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                String query = "DELETE FROM jobs_category WHERE id = ?";
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setLong(1, selectedCategory.getId());
                preparedStatement.executeUpdate();
                loadCategoryData(); // Refresh table after deleting a category
            } catch (SQLException ex) {
                Logger.getLogger(JobsCategoryController.class.getName()).log(Level.SEVERE, null, ex);
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText(null);
                alert.setContentText("Error occurred while deleting category: " + ex.getMessage());
                alert.showAndWait();
            }
        }
    }

    @FXML
    private void handleSearch(KeyEvent event) {
        String searchText = search.getText().trim();
        if (!searchText.isEmpty()) {
            ObservableList<JobsCategory> filteredList = FXCollections.observableArrayList();
            for (JobsCategory category : categoryList) {
                // Check if category name or category ID contains the search text
                if (String.valueOf(category.getId()).contains(searchText) ||
                        category.getCategoryName().toLowerCase().contains(searchText.toLowerCase())) {
                    filteredList.add(category);
                }
            }
            categoryTable.setItems(filteredList);
        } else {
            // If search text is empty, show all categories
            categoryTable.setItems(categoryList);
        }
    }


}
