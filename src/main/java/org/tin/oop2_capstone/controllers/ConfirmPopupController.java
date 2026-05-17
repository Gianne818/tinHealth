package org.tin.oop2_capstone.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.awt.event.ActionEvent;


public class ConfirmPopupController {

    @FXML private Label confirmMessageLabel;
    @FXML private Button confirmActionButton;
    @FXML private Button cancelActionButton;

    @FXML
    private void onCancelButtonClick(){
        Stage stage = (Stage) cancelActionButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void onConfirmButtonClick(){

    }
}
