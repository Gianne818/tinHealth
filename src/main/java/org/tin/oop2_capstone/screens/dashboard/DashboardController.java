package org.tin.oop2_capstone.screens.dashboard;

import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;


public class DashboardController {
    @FXML
    ScrollPane dashboardScrollPane;

    public void initialize(){
        dashboardScrollPane.getStyleClass().add("light");
    }
}
