package org.tin.oop2_capstone.controllers;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.StackPane;

public class DeleteConfirmationController {
    @FXML private StackPane popupRoot;
    @FXML private Label messageLabel;

    private Runnable onConfirm;
    private Node mainContentToBlur;

    public void setData(String itemName, Runnable onConfirm, Node mainContent) {
        this.messageLabel.setText("Are you sure you want to delete \"" + itemName + "\"?");
        this.onConfirm = onConfirm;
        this.mainContentToBlur = mainContent;

        // Apply blur to background screen
        if (mainContentToBlur != null) {
            mainContentToBlur.setEffect(new BoxBlur(6, 6, 3));
        }
    }

    @FXML private void onYesClick() {
        if (onConfirm != null) onConfirm.run();
        close();
    }

    @FXML private void onNoClick() {
        close();
    }

    private void close() {
        if (mainContentToBlur != null) mainContentToBlur.setEffect(null); // Remove blur

        // Check for Pane so it works for both AnchorPane and StackPane layouts
        if (popupRoot.getParent() instanceof javafx.scene.layout.Pane) {
            ((javafx.scene.layout.Pane) popupRoot.getParent()).getChildren().remove(popupRoot);
        }
    }
}