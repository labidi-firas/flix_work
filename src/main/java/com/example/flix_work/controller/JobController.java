package com.example.flix_work.controller;

import com.example.flix_work.dao.DbConnect;
import com.example.flix_work.entity.Job;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JobController implements Initializable {

    @FXML
    private TableView<Job> jobsTable;
    @FXML
    private TableColumn<Job, Long> idCol;
    @FXML
    private TableColumn<Job, String> titleCol;
    @FXML
    private TableColumn<Job, LocalDate> deadlineCol;
    @FXML
    private TableColumn<Job, String> categoryCol;
    @FXML
    private TableColumn<Job, String> descriptionCol;
    @FXML
    private TableColumn<Job, String> jobTypeCol;
    @FXML
    private TableColumn<Job, String> editCol;

    String query = null;
    Connection connection = null;
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;
    Job job = null;

    ObservableList<Job> jobList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        connection = DbConnect.getConnect();
        refreshTable();

        idCol.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        titleCol.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
        categoryCol.setCellValueFactory(cellData -> cellData.getValue().categoryNameProperty());
        descriptionCol.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
        jobTypeCol.setCellValueFactory(cellData -> cellData.getValue().jobTypeProperty());
        deadlineCol.setCellValueFactory(cellData -> cellData.getValue().deadlineProperty());

        // Add cell of button edit
        Callback<TableColumn<Job, String>, TableCell<Job, String>> cellFactory = (TableColumn<Job, String> param) -> {
            final TableCell<Job, String> cell = new TableCell<Job, String>() {
                @Override
                public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                        setText(null);
                    } else {
                        Button deleteButton = new Button("Delete");
                        Button editButton = new Button("Edit");

                        deleteButton.setStyle("-fx-cursor: hand ; -fx-font-size: 12px;");
                        editButton.setStyle("-fx-cursor: hand ; -fx-font-size: 12px;");

                        deleteButton.setOnAction(event -> {
                            try {
                                job = jobsTable.getSelectionModel().getSelectedItem();
                                if (job != null) {
                                    query = "DELETE FROM `job` WHERE id = ?";
                                    preparedStatement = connection.prepareStatement(query);
                                    preparedStatement.setLong(1, job.getId());
                                    preparedStatement.execute();
                                    refreshTable();
                                }
                            } catch (SQLException ex) {
                                Logger.getLogger(JobController.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        });

                        editButton.setOnAction(event -> {
                            job = jobsTable.getSelectionModel().getSelectedItem();
                            if (job != null) {
                                FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/AddJob.fxml")); // Corrected path
                                try {
                                    Parent parent = loader.load();
                                    AddJobController addJobController = loader.getController();
                                    addJobController.setUpdate(true);
                                    addJobController.setTextField(job.getId(), job.getTitle(), job.getDeadline(), job.getDescription(), job.getJobType(), job.getSalary());
                                    addJobController.setCurrentJob(job); // Pass the selected job to the AddJobController
                                    Stage stage = new Stage();
                                    stage.setScene(new Scene(parent));
                                    stage.initStyle(StageStyle.UTILITY);
                                    stage.show();
                                } catch (IOException ex) {
                                    Logger.getLogger(JobController.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        });

                        HBox manageBtns = new HBox(editButton, deleteButton);
                        manageBtns.setStyle("-fx-alignment:center");
                        HBox.setMargin(deleteButton, javafx.geometry.Insets.EMPTY);
                        HBox.setMargin(editButton, new javafx.geometry.Insets(2, 3, 0, 2));

                        setGraphic(manageBtns);
                        setText(null);
                    }
                }
            };
            return cell;
        };

        editCol.setCellFactory(cellFactory);
        jobsTable.setItems(jobList);

        // Refresh table every 30 seconds
        Timeline timeline = new Timeline(new KeyFrame(javafx.util.Duration.seconds(30), event -> refreshTable()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        // Check for expired jobs and delete them every minute
        Timeline deleteExpiredJobsTimeline = new Timeline(new KeyFrame(javafx.util.Duration.minutes(1), event -> deleteExpiredJobs()));
        deleteExpiredJobsTimeline.setCycleCount(Timeline.INDEFINITE);
        deleteExpiredJobsTimeline.play();
    }

    public void refreshTable() {
        try {
            jobList.clear();

            query = "SELECT j.id, j.title, j.description, j.type, j.deadline, j.salary, c.category_name " +
                    "FROM job j " +
                    "JOIN jobs_category c ON j.category_id = c.id";
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                jobList.add(new Job(
                        resultSet.getInt("id"),
                        resultSet.getString("title"),
                        resultSet.getString("type"),
                        resultSet.getString("description"),
                        resultSet.getDate("deadline").toLocalDate(),
                        resultSet.getDouble("salary"),
                        resultSet.getString("category_name")
                ));
            }
        } catch (SQLException ex) {
            Logger.getLogger(JobController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void close(javafx.scene.input.MouseEvent event) {
        Stage stage = (Stage) jobsTable.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void print(javafx.scene.input.MouseEvent event) {
        // Implement print functionality here
    }

    @FXML
    private void getAddView(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/AddJob.fxml"));
            Parent parent = loader.load();

            // Access the controller of AddJob.fxml if needed
            AddJobController addJobController = loader.getController();

            Scene scene = new Scene(parent);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.initStyle(StageStyle.UTILITY);
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(JobController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void goToFront(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/front.fxml"));
            Parent parent = loader.load();

            Scene scene = new Scene(parent);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.initStyle(StageStyle.UTILITY);
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(JobController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void handleGoToCategory(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Jobs_category.fxml"));
            Parent parent = loader.load();

            Scene scene = new Scene(parent);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.initStyle(StageStyle.UTILITY);
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(JobController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void deleteExpiredJobs() {
        try {
            String deleteQuery = "DELETE FROM job WHERE deadline < ?";
            preparedStatement = connection.prepareStatement(deleteQuery);
            preparedStatement.setDate(1, java.sql.Date.valueOf(LocalDate.now()));
            int deletedRows = preparedStatement.executeUpdate();
            Logger.getLogger(JobController.class.getName()).log(Level.INFO, "Deleted {0} expired jobs", deletedRows);
            refreshTable();
        } catch (SQLException ex) {
            Logger.getLogger(JobController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


}
