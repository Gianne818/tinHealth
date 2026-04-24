package org.tin.oop2_capstone.screens.food_log;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;

public class FoodLogController {
    @FXML
    Button buttonAddFood;

    @FXML
    GridPane gridPaneAddEntry;

    @FXML
    ScrollPane foodLogScrollPane;

    public void initialize(){
        foodLogScrollPane.getStyleClass().add("light");
    }

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
