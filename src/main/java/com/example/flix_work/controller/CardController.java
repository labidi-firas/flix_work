package com.example.flix_work.controller;

import com.example.flix_work.entity.Job;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

public class CardController {
    @FXML
    private Label jobTitleLabel;

    @FXML
    private Label jobTypeLabel;

    @FXML
    private Label jobPriceLabel;

    @FXML
    private ImageView jobImageView1;

    @FXML
    private AnchorPane cardRoot; // Assuming the root element of the card is an AnchorPane

    private Job job;
    private MyListener myListener;

    public void setData(Job job, MyListener myListener) {
        this.job = job;
        this.myListener = myListener;

        // Set job data to labels
        jobTitleLabel.setText(job.getTitle());
        jobTypeLabel.setText(job.getJobType());
        jobPriceLabel.setText("$" + job.getSalary());

        // Set job image
        // You'll need to implement a method in your Job entity to get the image URL
        // For now, assuming a method getJobImageURL() returns the image URL
        // Example usage: jobImageView1.setImage(new Image(job.getJobImageURL()));
    }

    @FXML
    public void click(MouseEvent mouseEvent) {
        if (myListener != null) {
            myListener.onClickListener(job);
        }
    }
}
