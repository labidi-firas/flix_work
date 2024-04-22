package com.example.flix_work.entity;

public class Recruiter {
    private long id;
    private String companyName;
    private String activityField;
    private String description;

    // Constructor
    public Recruiter(long id, String companyName, String activityField, String description) {
        this.id = id;
        this.companyName = companyName;
        this.activityField = activityField;
        this.description = description;
    }

    // Getters and setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getActivityField() {
        return activityField;
    }

    public void setActivityField(String activityField) {
        this.activityField = activityField;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
