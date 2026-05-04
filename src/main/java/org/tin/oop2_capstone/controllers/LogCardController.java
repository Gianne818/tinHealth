package org.tin.oop2_capstone.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;


public class LogCardController {
    @FXML Label logNameLabel;
    @FXML Label timeLabel;
    @FXML Label numberLabel;
    @FXML Label unitLabel;
    @FXML
    Label kcalLabel;

    public void setData(String logName, String time, double number, String unit, double kcal){
        logNameLabel.setText(logName);
        timeLabel.setText(time);
        numberLabel.setText(String.valueOf(number));
        unitLabel.setText(unit);
        kcalLabel.setText(String.valueOf(kcal));
    }
}
