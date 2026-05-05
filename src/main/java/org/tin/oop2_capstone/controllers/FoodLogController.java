package org.tin.oop2_capstone.controllers;

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
        gridPaneAddEntry.setVisible(!addEntryisVisible);
        gridPaneAddEntry.setManaged(!addEntryisVisible);

        addEntryisVisible = !addEntryisVisible;
    }

}
