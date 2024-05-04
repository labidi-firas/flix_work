package com.example.gestion_formation;

import java.time.LocalDate;

public class Training {

    private  int id;
    private  String title;
    private String description;
    private LocalDate start_date;
    private LocalDate end_date;
    private  int trainer_id;
    private  int category_id;
    private  int number_of_places;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getStart_date() {
        return start_date;
    }

    public void setStart_date(LocalDate start_date) {
        this.start_date = start_date;
    }

    public LocalDate getEnd_date() {
        return end_date;
    }

    public void setEnd_date(LocalDate end_date) {
        this.end_date = end_date;
    }

    public int getTrainer_id() {
        return trainer_id;
    }

    public void setTrainer_id(int trainer_id) {
        this.trainer_id = trainer_id;
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public int getNumber_of_places() {
        return number_of_places;
    }

    public void setNumber_of_places(int number_of_places) {
        this.number_of_places = number_of_places;
    }


}
