package org.tin.oop2_capstone.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.SVGPath;

public class SelectedFoodController {

    @FXML private Label foodNameLabel;
    @FXML private StackPane deleteButtonSVGPath;
    @FXML private HBox selectedFoodHBox;

    private Runnable onDeleteAction;

    public void setFoodNameLabel(String text){
        foodNameLabel.setText(text);

        selectedFoodHBox.setOnMouseEntered(e -> {
            deleteButtonSVGPath.setVisible(true);
            deleteButtonSVGPath.setManaged(true);
        });

        selectedFoodHBox.setOnMouseExited(e -> {
            deleteButtonSVGPath.setVisible(false);
            deleteButtonSVGPath.setManaged(false);
        });
    }

    public void setOnDeleteAction(Runnable action){
        this.onDeleteAction = action;
    }


    @FXML
    private void deleteCurrent(){
        if(onDeleteAction!=null){
            onDeleteAction.run();
        }
    }
}
