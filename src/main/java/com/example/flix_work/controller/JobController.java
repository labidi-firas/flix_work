package com.example.flix_work.controller;

import com.example.flix_work.dao.DbConnect;
import com.example.flix_work.entity.Job;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import javafx.util.Pair;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
public class JobController implements Initializable {



    @FXML
    private Button btnDownload;


    @FXML
    private TableView<Job> tableView;

    @FXML
    private Button btnGoToChart;


    @FXML
    private PieChart jobCategoryChart;

    @FXML
    private TableView<Job> jobsTable;
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
    @FXML
    private TableColumn<Job, Integer> idCol;



    @FXML
    private PieChart pieChart;


    @FXML
    private TextField search;

    String query = null;
    Connection connection = null;
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;
    Job job = null;

    ObservableList<Job> jobList = FXCollections.observableArrayList();

    @Override

    public void initialize(URL url, ResourceBundle rb) {
        btnDownload.setOnAction(this::downloadAllJobs);
        btnGoToChart.setOnAction(event -> goToChart());
        pieChart = new PieChart();
        connection = DbConnect.getConnect();
        refreshTable();
        idCol.setCellValueFactory(cellData -> {
            Job job = cellData.getValue();
            Long jobId = job.getId();
            ObservableValue<Integer> observableValue = new SimpleObjectProperty<>(jobId.intValue());
            return observableValue;
        });

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

    // Add method to create pie chart


    private void goToChart() {
        // Get the data for the chart from the database
        ObservableList<PieChart.Data> pieChartData = fetchDataFromDatabase();

        // Set the data for the PieChart
        jobCategoryChart.setData(pieChartData);
    }




    // Call createPieChart() method in refreshTable() method
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

            // Create PieChart after updating jobList
            createPieChart();
        } catch (SQLException ex) {
            Logger.getLogger(JobController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void createPieChart() {
        ObservableList<PieChart.Data> pieChartData = fetchDataFromDatabase();

        jobCategoryChart.setData(pieChartData);
        jobCategoryChart.setLabelsVisible(true); // Show labels with percentages
    }

    @FXML
    private ObservableList<PieChart.Data> fetchDataFromDatabase() {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

        try (Connection connection = DbConnect.getConnect()) {
            String query = "SELECT c.category_name, COUNT(*) AS count " +
                    "FROM job j " +
                    "JOIN jobs_category c ON j.category_id = c.id " +
                    "GROUP BY c.category_name";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            // Récupération des données dans une liste
            List<Pair<String, Integer>> data = new ArrayList<>();
            int totalJobs = 0;
            while (resultSet.next()) {
                String category = resultSet.getString("category_name");
                int count = resultSet.getInt("count");
                data.add(new Pair<>(category, count));
                totalJobs += count;
            }

            // Calcul des pourcentages et création des données pour le PieChart
            for (Pair<String, Integer> entry : data) {
                String category = entry.getKey();
                int count = entry.getValue();
                double percentage = ((double) count / totalJobs) * 100;
                pieChartData.add(new PieChart.Data(category + " (" + String.format("%.2f", percentage) + "%)", count));
            }

        } catch (SQLException ex) {
            Logger.getLogger(JobController.class.getName()).log(Level.SEVERE, null, ex);
        }

        return pieChartData;
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
    @FXML
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




    // Add an event handler to the search TextField
    @FXML
    private void handleSearch(KeyEvent event) {
        String searchText = search.getText().trim();
        if (!searchText.isEmpty()) {
            try {
                jobList.clear();

                // Update the SQL query to include search criteria for title, description, type, category name, and category ID
                query = "SELECT j.id, j.title, j.description, j.type, j.deadline, j.salary, c.category_name " +
                        "FROM job j " +
                        "JOIN jobs_category c ON j.category_id = c.id " +
                        "WHERE j.title LIKE ? OR j.description LIKE ? OR j.type LIKE ? OR c.category_name LIKE ? OR CAST(j.id AS CHAR) LIKE ?";
                preparedStatement = connection.prepareStatement(query);
                for (int i = 1; i <= 5; i++) {
                    preparedStatement.setString(i, "%" + searchText + "%");
                }
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
        } else {
            // If search text is empty, refresh the table to show all jobs
            refreshTable();
        }
    }





    @FXML
    private void goToChart(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/chartjob.fxml"));
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
    private void downloadAllJobs(ActionEvent event) {
        try {
            // Open a FileWriter to write to a file named "jobs.csv"
            FileWriter writer = new FileWriter("jobs.csv");

            // Write the header line to the CSV file
            writer.append("Title,Deadline,Category,Description,Job Type\n");

            // Iterate through the jobList and write each job's information to the CSV file
            for (Job job : jobList) {
                writer.append(job.getTitle())
                        .append(",")
                        .append(job.getDeadline().toString())
                        .append(",")
                        .append(job.getCategoryName())
                        .append(",")
                        .append(job.getDescription())
                        .append(",")
                        .append(job.getJobType())
                        .append("\n");
            }

            // Close the FileWriter
            writer.close();

            // Notify the user that the download is complete
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Download Complete");
            alert.setHeaderText(null);
            alert.setContentText("All jobs have been downloaded to jobs.csv");
            alert.showAndWait();
        } catch (IOException ex) {
            // Handle any IOException that occurs during file writing
            Logger.getLogger(JobController.class.getName()).log(Level.SEVERE, null, ex);
            // Notify the user about the error
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("An error occurred while downloading jobs.");
            alert.showAndWait();
        }
    }



}

