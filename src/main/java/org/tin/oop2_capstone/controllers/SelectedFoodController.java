package org.tin.oop2_capstone.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.SVGPath;

public class SelectedFoodController {

    @FXML private Label foodNameLabel;
    @FXML private StackPane deleteButtonSVGPath;
    @FXML private HBox selectedFoodHBox;

    private Runnable onDeleteAction;
    private double origMinWidth;

    private static SelectedFoodController instance;

    public void initialize(){
        instance = this;
    }

    public static SelectedFoodController getInstance() {
        return instance;
    }

    public void setFoodNameLabel(String text){
        foodNameLabel.setText(text);

        selectedFoodHBox.setOnMouseEntered(e -> {
            origMinWidth = selectedFoodHBox.getMinWidth();
            selectedFoodHBox.setMinWidth(Control.USE_PREF_SIZE);
            deleteButtonSVGPath.setVisible(true);
            deleteButtonSVGPath.setManaged(true);
        });

        selectedFoodHBox.setOnMouseExited(e -> {
            selectedFoodHBox.setMinWidth(origMinWidth);
            deleteButtonSVGPath.setVisible(false);
            deleteButtonSVGPath.setManaged(false);
        });
    }

    public void setOnDeleteAction(Runnable action){
        this.onDeleteAction = action;
    }


    @FXML
    public void deleteCurrent(){
        if(onDeleteAction!=null){
            onDeleteAction.run();
        }
    }
}
