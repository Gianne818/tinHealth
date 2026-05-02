package org.tin.oop2_capstone.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ToolTipController {
    @FXML Label dayLabel;
    @FXML Label calInLabel;
    @FXML Label calOutLabel;

    public void setData(String day, Number in, Number out){
        dayLabel.setText(day);
        calInLabel.setText(in + " kcal");
        calOutLabel.setText(out + " kcal");
    }
}
