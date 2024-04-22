package com.example.flix_work.controller;

import com.example.flix_work.dao.DbConnect;
import com.example.flix_work.entity.Job;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

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

public class front implements Initializable {

    @FXML
    private Label DescrptionLabel;

    @FXML
    private Label JobCategoryLabel;

    @FXML
    private Label JobDeadlineLabel;

    @FXML
    private VBox chosenJobCard;

    @FXML
    private GridPane grid;

    @FXML
    private ImageView jobImg;

    @FXML
    private Label jobNameLabel;

    @FXML
    private Label jobPriceLabel;

    @FXML
    private ScrollPane scroll;

    private List<Job> jobs = new ArrayList<>();
    private MyListener myListener;

    private List<Job> getDataFromDatabase() {
        List<Job> jobs = new ArrayList<>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            // Establish connection to the database
            connection = DbConnect.getConnect();

            // Prepare the SQL query to fetch job data
            String query = "SELECT id, title, type, salary FROM job"; // Modify column names as per your database schema
            preparedStatement = connection.prepareStatement(query);

            // Execute the query and retrieve the result set
            resultSet = preparedStatement.executeQuery();

            // Iterate through the result set and create Job objects
            while (resultSet.next()) {
                long id = resultSet.getLong("id");
                String title = resultSet.getString("title");
                String jobType = resultSet.getString("type"); // Corrected column name
                double salary = resultSet.getDouble("salary");

                // Create a Job object with the retrieved data
                Job job = new Job(id, title, jobType, "", LocalDate.now(), salary, "");
                jobs.add(job);
            }
        } catch (SQLException ex) {
            Logger.getLogger(front.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            // Close the resources
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(front.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return jobs;
    }

    private void setChosenJob(Job job) {
        jobNameLabel.setText(job.getTitle());
        jobPriceLabel.setText("$" + job.getSalary());
        DescrptionLabel.setText(job.getDescription()); // Set job description
        JobCategoryLabel.setText(job.getCategoryName()); // Set job category
        JobDeadlineLabel.setText(job.getDeadline().toString()); // Set job deadline

        chosenJobCard.setStyle("-fx-background-color: #FFFFFF;\n" +
                "    -fx-background-radius: 30;");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        jobs.addAll(getDataFromDatabase());
        if (jobs.size() > 0) {
            setChosenJob(jobs.get(0));
            myListener = new MyListener() {
                @Override
                public void onClickListener(Job job) {
                    setChosenJob(job);
                }
            };
        }
        int column = 0;
        int row = 1;
        try {
            for (int i = 0; i < jobs.size(); i++) {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/FXML/card.fxml"));
                AnchorPane anchorPane = fxmlLoader.load();

                CardController cardController = fxmlLoader.getController();
                cardController.setData(jobs.get(i), myListener);

                if (column == 3) {
                    column = 0;
                    row++;
                }

                grid.add(anchorPane, column++, row); //(child,column,row)
                //set grid width
                grid.setMinWidth(Region.USE_COMPUTED_SIZE);
                grid.setPrefWidth(Region.USE_COMPUTED_SIZE);
                grid.setMaxWidth(Region.USE_PREF_SIZE);

                //set grid height
                grid.setMinHeight(Region.USE_COMPUTED_SIZE);
                grid.setPrefHeight(Region.USE_COMPUTED_SIZE);
                grid.setMaxHeight(Region.USE_PREF_SIZE);

                GridPane.setMargin(anchorPane, new Insets(10));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
