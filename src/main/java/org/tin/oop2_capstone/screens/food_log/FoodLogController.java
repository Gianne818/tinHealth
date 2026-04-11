package org.tin.oop2_capstone.screens.food_log;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

public class FoodLogController {
    @FXML public Button buttonAddFood;
    @FXML public GridPane gridPaneAddEntry;

    private boolean addEntryisVisible = false;
    @FXML public void onButtonAddFoodClicked(ActionEvent event){
        if(!addEntryisVisible){
            gridPaneAddEntry.setVisible(true);
        } else {
            gridPaneAddEntry.setVisible(false);
        }

        addEntryisVisible = !addEntryisVisible;
    }

}
