package com.example.flix_work.controller;

import com.example.flix_work.dao.DbConnect;
import com.example.flix_work.entity.Job;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AddJobController implements Initializable {

    @FXML
    private DatePicker DeadlineFld;

    @FXML
    private TextField DescriptionFld;

    @FXML
    private TextField JobTypeFld;

    @FXML
    private TextField SalaryFld;

    @FXML
    private TextField TitleFild;

    @FXML
    private ComboBox<String> Categoriefld;

    private ObservableList<String> categoryList;

    String query = null;
    Connection connection = null;
    PreparedStatement preparedStatement;
    private boolean update;
    int jobId;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize category list
        categoryList = FXCollections.observableArrayList();
        // Populate category list
        populateCategoryList();
        // Set category list as options for the ComboBox
        Categoriefld.setItems(categoryList);
    }

    // Method to populate the category list
    private void populateCategoryList() {
        try {
            // Fetch categories from the database
            connection = DbConnect.getConnect();
            String sql = "SELECT category_name FROM jobs_category"; // Changed column name from 'name' to 'category_name'
            preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                categoryList.add(resultSet.getString("category_name"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(AddJobController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void Clean(MouseEvent event) {
        clearFields();
    }

    @FXML
    private void Save(MouseEvent event) {
        connection = DbConnect.getConnect();
        int recruiterId = 4; // Change this according to your actual implementation

        String title = TitleFild.getText();
        LocalDate deadline = DeadlineFld.getValue();
        String description = DescriptionFld.getText();
        String jobType = JobTypeFld.getText();
        String salaryText = SalaryFld.getText();

        // Check if deadline is greater than today's date
        if (deadline != null && deadline.isBefore(LocalDate.now())) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Deadline must be greater than today's date");
            alert.showAndWait();
            return; // Exit the method if deadline is not valid
        }

        // Check if job type exceeds 30 characters
        if (jobType.length() > 30) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Job type cannot exceed 30 characters");
            alert.showAndWait();
            return; // Exit the method if job type is not valid
        }

        // Check if job type is empty
        if (jobType.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Job type cannot be empty");
            alert.showAndWait();
            return; // Exit the method if job type is empty
        }

        // Check if salary is empty or non-numeric
        if (salaryText.isEmpty() || !salaryText.matches("\\d+(\\.\\d+)?")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Please enter a valid salary");
            alert.showAndWait();
            return; // Exit the method if salary is empty or non-numeric
        }

        // Convert salary to double
        double salary = Double.parseDouble(salaryText);

        // Check if salary is negative or zero
        if (salary <= 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Salary must be a positive number");
            alert.showAndWait();
            return; // Exit the method if salary is negative or zero
        }

        // Check if other fields are empty or not valid
        if (title.isEmpty() || deadline == null || description.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Please Fill All DATA");
            alert.showAndWait();
            return; // Exit the method if any field is not valid
        }

        // Check if the title already exists in the database
        if (isDuplicateTitle(title)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("A job with the same title already exists.");
            alert.showAndWait();
            return; // Exit the method if the title already exists
        }

        if (update) {
            updateJob();
        } else {
            insert(recruiterId);
        }

        clearFields();
    }

    // Method to check if the title already exists in the database
    private boolean isDuplicateTitle(String title) {
        try {
            String query = "SELECT COUNT(*) AS count FROM job WHERE title = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, title);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt("count");
                return count > 0; // Return true if the count is greater than 0, indicating a duplicate title
            }
        } catch (SQLException ex) {
            Logger.getLogger(AddJobController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false; // Return false if an error occurs or the count is 0
    }

    private void clearFields() {
        TitleFild.setText(null);
        DeadlineFld.setValue(null);
        DescriptionFld.setText(null);
        JobTypeFld.setText(null);
        SalaryFld.setText(null);
    }

    private void insert(int recruiterId) {
        try {
            // Fetch the selected category name from the combo box
            String categoryName = Categoriefld.getValue();

            // Fetch the category ID based on the category name
            int categoryId = fetchCategoryId(categoryName);

            // Prepare the SQL query to insert the job
            String query = "INSERT INTO job (recruiter_id, category_id, title, deadline, description, type, salary) VALUES (?, ?, ?, ?, ?, ?, ?)";
            preparedStatement = connection.prepareStatement(query);

            // Set values for the prepared statement
            preparedStatement.setInt(1, recruiterId); // Set the recruiter ID
            preparedStatement.setInt(2, categoryId); // Set the category ID
            preparedStatement.setString(3, TitleFild.getText()); // Set the job title
            preparedStatement.setDate(4, java.sql.Date.valueOf(DeadlineFld.getValue())); // Set the job deadline
            preparedStatement.setString(5, DescriptionFld.getText()); // Set the job description
            preparedStatement.setString(6, JobTypeFld.getText()); // Set the job type
            preparedStatement.setDouble(7, Double.parseDouble(SalaryFld.getText())); // Set the job salary

            // Execute the update
            preparedStatement.executeUpdate();

            // Optionally, you can show a success message or perform other actions upon successful insertion
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setContentText("Job added successfully");
            alert.showAndWait();

        } catch (SQLException ex) {
            Logger.getLogger(AddJobController.class.getName()).log(Level.SEVERE, null, ex);
            // Optionally, you can show an error message or perform other error handling actions
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Error occurred while adding job: " + ex.getMessage());
            alert.showAndWait();
        }
    }

    private void updateJob() {
        // Retrieve existing job details
        String title = TitleFild.getText();
        LocalDate deadline = DeadlineFld.getValue();
        String description = DescriptionFld.getText();
        String jobType = JobTypeFld.getText();
        String salaryText = SalaryFld.getText();

        // Check if deadline is greater than today's date
        if (deadline != null && deadline.isBefore(LocalDate.now())) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Deadline must be greater than today's date");
            alert.showAndWait();
            return; // Exit the method if deadline is not valid
        }

        // Check if job type exceeds 30 characters
        if (jobType.length() > 30) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Job type cannot exceed 30 characters");
            alert.showAndWait();
            return; // Exit the method if job type is not valid
        }

        // Check if job type is empty
        if (jobType.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Job type cannot be empty");
            alert.showAndWait();
            return; // Exit the method if job type is empty
        }

        // Check if salary is empty or non-numeric
        if (salaryText.isEmpty() || !salaryText.matches("\\d+(\\.\\d+)?")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Please enter a valid salary");
            alert.showAndWait();
            return; // Exit the method if salary is empty or non-numeric
        }

        // Convert salary to double
        double salary = Double.parseDouble(salaryText);

        // Check if salary is negative or zero
        if (salary <= 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Salary must be a positive number");
            alert.showAndWait();
            return; // Exit the method if salary is negative or zero
        }

        // Check if other fields are empty or not valid
        if (title.isEmpty() || deadline == null || description.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Please Fill All DATA");
            alert.showAndWait();
            return; // Exit the method if any field is not valid
        }

        try {
            // Fetch the selected category name from the combo box
            String categoryName = Categoriefld.getValue();

            // Fetch the category ID based on the category name
            int categoryId = fetchCategoryId(categoryName);

            // Prepare the SQL query to update the job
            String query = "UPDATE job SET category_id=?, title=?, deadline=?, description=?, type=?, salary=? WHERE id=?";
            preparedStatement = connection.prepareStatement(query);

            // Set values for the prepared statement
            preparedStatement.setInt(1, categoryId); // Set the category ID
            preparedStatement.setString(2, title); // Set the job title
            preparedStatement.setDate(3, java.sql.Date.valueOf(deadline)); // Set the job deadline
            preparedStatement.setString(4, description); // Set the job description
            preparedStatement.setString(5, jobType); // Set the job type
            preparedStatement.setDouble(6, salary); // Set the job salary
            preparedStatement.setInt(7, jobId); // Set the job ID

            // Execute the update
            preparedStatement.executeUpdate();

            // Optionally, you can show a success message or perform other actions upon successful update
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setContentText("Job updated successfully");
            alert.showAndWait();

        } catch (SQLException ex) {
            Logger.getLogger(AddJobController.class.getName()).log(Level.SEVERE, null, ex);
            // Optionally, you can show an error message or perform other error handling actions
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Error occurred while updating job: " + ex.getMessage());
            alert.showAndWait();
        }
    }


    private int fetchCategoryId(String categoryName) throws SQLException {
        int categoryId = 0;
        try (Connection connection = DbConnect.getConnect();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT id FROM jobs_category WHERE category_name = ?")) {
            preparedStatement.setString(1, categoryName); // Set the category name parameter
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    categoryId = resultSet.getInt("id");
                }
            }
        }
        return categoryId;
    }

    public void setTextField(long id, String title, LocalDate deadline, String description, String jobType, double salary) {
        jobId = (int) id;
        TitleFild.setText(title);
        DeadlineFld.setValue(deadline);
        DescriptionFld.setText(description);
        JobTypeFld.setText(jobType);
        SalaryFld.setText(String.valueOf(salary));
        update = true;
    }

    public void setUpdate(boolean b) {
        this.update = b;
    }

    public void setCurrentJob(Job job) {
        if (job != null) {
            TitleFild.setText(job.getTitle());
            DeadlineFld.setValue(job.getDeadline());
            DescriptionFld.setText(job.getDescription());
            JobTypeFld.setText(job.getJobType());
            SalaryFld.setText(String.valueOf(job.getSalary()));

            // Select the appropriate category in the ComboBox
            String category = job.getCategoryName();
            if (category != null && !category.isEmpty()) {
                Categoriefld.getSelectionModel().select(category);
            }
        }
    }


}
