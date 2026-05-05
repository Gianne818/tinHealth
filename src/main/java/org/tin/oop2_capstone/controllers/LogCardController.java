package org.tin.oop2_capstone.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.shape.SVGPath;


public class LogCardController {
    @FXML Label logNameLabel;
    @FXML Label timeLabel;
    @FXML Label numberLabel;
    @FXML Label unitLabel;
    @FXML Label kcalLabel;
    @FXML SVGPath deleteSVG;

    @FXML Separator bottomSeparator;

    public void setData(String logName, String time, double number, String unit, double kcal, boolean showSeparator, boolean showTrash){
        logNameLabel.setText(logName);
        timeLabel.setText(time);
        numberLabel.setText(String.valueOf(number));
        unitLabel.setText(unit);
        kcalLabel.setText(String.valueOf(kcal));
        bottomSeparator.setManaged(showSeparator);
        deleteSVG.setVisible(showTrash);
        deleteSVG.setManaged(showTrash);
    }
}
