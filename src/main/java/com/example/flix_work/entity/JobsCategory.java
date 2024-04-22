package com.example.flix_work.entity;

import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class JobsCategory {
    private final LongProperty id;
    private final StringProperty categoryName;

    // Constructor
    public JobsCategory(long id, String categoryName) {
        this.id = new SimpleLongProperty(id);
        this.categoryName = new SimpleStringProperty(categoryName);
    }

    // Getter for id property
    public long getId() {
        return id.get();
    }

    // Setter for id property
    public void setId(long id) {
        this.id.set(id);
    }

    // Getter for categoryName property
    public String getCategoryName() {
        return categoryName.get();
    }

    // Setter for categoryName property
    public void setCategoryName(String categoryName) {
        this.categoryName.set(categoryName);
    }

    // Getter for id property as a JavaFX property
    public LongProperty idProperty() {
        return id;
    }

    // Getter for categoryName property as a JavaFX property
    public StringProperty categoryNameProperty() {
        return categoryName;
    }
}
