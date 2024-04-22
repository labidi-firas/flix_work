package com.example.flix_work.entity;

import javafx.beans.property.*;

import java.time.LocalDate;

public class Job {
    private final LongProperty id;
    private final StringProperty title;
    private final StringProperty description;
    private final StringProperty jobType;
    private final ObjectProperty<LocalDate> deadline;
    private final DoubleProperty salary;
    private final StringProperty categoryName;

    public Job(long id, String title, String jobType, String description, LocalDate deadline, double salary, String categoryName) {
        this.id = new SimpleLongProperty(id);
        this.title = new SimpleStringProperty(title);
        this.jobType = new SimpleStringProperty(jobType);
        this.description = new SimpleStringProperty(description);
        this.deadline = new SimpleObjectProperty<>(deadline);
        this.salary = new SimpleDoubleProperty(salary);
        this.categoryName = new SimpleStringProperty(categoryName);
    }

    public long getId() {
        return id.get();
    }

    public LongProperty idProperty() {
        return id;
    }

    public void setId(long id) {
        this.id.set(id);
    }

    public String getTitle() {
        return title.get();
    }

    public StringProperty titleProperty() {
        return title;
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public String getDescription() {
        return description.get();
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public String getJobType() {
        return jobType.get();
    }

    public StringProperty jobTypeProperty() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType.set(jobType);
    }

    public LocalDate getDeadline() {
        return deadline.get();
    }

    public ObjectProperty<LocalDate> deadlineProperty() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline.set(deadline);
    }

    public double getSalary() {
        return salary.get();
    }

    public DoubleProperty salaryProperty() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary.set(salary);
    }

    public String getCategoryName() {
        return categoryName.get();
    }

    public StringProperty categoryNameProperty() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName.set(categoryName);
    }
}
