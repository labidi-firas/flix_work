package com.example.flix_work.entity;

import javafx.beans.property.*;

import java.time.LocalDate;

public class JobCard {
    private final StringProperty title;
    private final DoubleProperty salary;
    private final ObjectProperty<LocalDate> deadline;
    private final StringProperty jobType;

    public JobCard(Job job) {
        this.title = job.titleProperty();
        this.salary = job.salaryProperty();
        this.deadline = job.deadlineProperty();
        this.jobType = job.jobTypeProperty();
    }

    // Getters for title, salary, deadline, jobType
    public String getTitle() {
        return title.get();
    }

    public Double getSalary() {
        return salary.get();
    }

    public LocalDate getDeadline() {
        return deadline.get();
    }

    public String getJobType() {
        return jobType.get();
    }
}
