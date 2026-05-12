package org.tin.oop2_capstone.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import org.tin.oop2_capstone.model.entities.User;
import org.tin.oop2_capstone.services.SessionManager;

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
    private ChoiceBox<String> genderChoiceBox;
    @FXML
    private Label fullnameLabel, emailLabel;

    public void initialize(){
        String gender = u.getIsMale() ? "Male" : "Female";
//        System.out.println(u);

        emailTextField.setText(u.getEmail());
        fullnameTextField.setText(u.getFullname());
        birthdateTextField.setText("");
        weightTextField.setText(String.format("%.1f", u.getWeightKg()));
        genderChoiceBox.getItems().addAll("Male", "Female");
        genderChoiceBox.setValue(gender);
        fullnameLabel.setText(u.getUsername());
        heightTextField.setText(String.format("%.0f", u.getHeightCm()));
    }


}