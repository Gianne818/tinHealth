package org.tin.oop2_capstone.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import org.tin.oop2_capstone.model.entities.User;
import org.tin.oop2_capstone.services.SessionManager;
import org.tin.oop2_capstone.utils.TimeFormatter;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class ProfileController {

    private final User u = SessionManager.getInstance().getCurrentUser();
    @FXML
    private ScrollPane profileScrollPane;
    @FXML
    private TextField emailTextField,
            fullnameTextField,
            birthdateTextField,
            weightTextField,
            heightTextField;
    @FXML
    private ChoiceBox<String> genderChoiceBox, activityLevelChoiceBox;
    @FXML
    private Label fullnameLabel, emailLabel;

    public void initialize(){
        String gender = u.getIsMale() ? "Male" : "Female";
//        System.out.println(u);

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);
        String formattedDOB = dateTimeFormatter.format(u.getDateOfBirth());

        emailTextField.setText(u.getEmail());
        fullnameTextField.setText(u.getFullname());
        birthdateTextField.setText(formattedDOB);
        weightTextField.setText(String.format("%.1f", u.getWeightKg()));
        genderChoiceBox.getItems().addAll("Male", "Female");
        activityLevelChoiceBox.getItems().addAll(
                "Sedentary - Little to no exercise",
                "Lightly Active - Exercise 1-3 days/week",
                "Moderately Active - Exercise 3-5 days/week",
                "Very Active - Exercise 6-7 days/week",
                "Extremely Active - Athlete/Physical Job"
                );
        activityLevelChoiceBox.setValue(u.getActivityLevel());
        genderChoiceBox.setValue(gender);
        fullnameLabel.setText(u.getUsername());
        heightTextField.setText(String.format("%.0f", u.getHeightCm()));
        emailLabel.setText(u.getEmail());
    }


}