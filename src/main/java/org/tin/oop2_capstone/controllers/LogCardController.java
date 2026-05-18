package org.tin.oop2_capstone.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.shape.SVGPath;

public class LogCardController {
    @FXML private Label logNameLabel;
    @FXML private Label timeLabel;
    @FXML private Label numberLabel;
    @FXML private Label unitLabel;
    @FXML private Label kcalLabel;
    @FXML private SVGPath deleteSVG;

    @FXML Separator bottomSeparator;

    public void setData(String logName, String time, double number, String unit, double kcal, boolean showSeparator, boolean showTrash, Runnable onDeleteAction) {
        logNameLabel.setText(logName);
        timeLabel.setText(time);
        // Check if it's food (empty unit) to hide the number and unit displays
        boolean isFood = unit == null || unit.trim().isEmpty();
        numberLabel.setText(String.valueOf(number));
        numberLabel.setVisible(!isFood);
        numberLabel.setManaged(!isFood);

        unitLabel.setText(unit);
        unitLabel.setVisible(!isFood);
        unitLabel.setManaged(!isFood);

        kcalLabel.setText(String.valueOf(kcal));
        bottomSeparator.setManaged(showSeparator);
        deleteSVG.setVisible(showTrash);
        deleteSVG.setManaged(showTrash);

        deleteSVG.setOnMouseClicked(event -> {
//            System.out.println("HALA NA CLICKED ANG TRASH ICON !");
            if (onDeleteAction != null) {
                onDeleteAction.run();
            }
        });
    }


}
