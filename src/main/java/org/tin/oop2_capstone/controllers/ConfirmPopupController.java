package org.tin.oop2_capstone.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.io.IOException;


public class ConfirmPopupController {

    @FXML private Label confirmMessageLabel;
    @FXML private Button confirmActionButton;
    @FXML private Button cancelActionButton;
    @FXML private Label confirmTitleLabel;

    private Runnable onConfirmAction;

    @FXML private StackPane popupRoot;
    private Node mainContentToBlur;

    @FXML
    private void onCancelButtonClick(){
        closeWindow();
    }

    @FXML
    private void onConfirmButtonClick(){
        if(onConfirmAction!=null){
            onConfirmAction.run();
        }
        closeWindow();
    }

    public void setOnConfirmAction(Runnable action){
        this.onConfirmAction = action;
    }

    public void setConfirmPopupTitle(String title){
        confirmTitleLabel.setText(title);
    }


    public static void showPopup(String title,String message, Runnable onConfirmAction) {
        try {
            FXMLLoader loader = new FXMLLoader(ConfirmPopupController.class.getResource("/org/tin/oop2_capstone/views/confirm-popup-view.fxml"));
            Parent root = loader.load();

            ConfirmPopupController controller = loader.getController();
            controller.setConfirmMessage(message);
            controller.setOnConfirmAction(onConfirmAction);
            controller.setConfirmPopupTitle(title);

            Scene scene = new Scene(root);
            scene.getStylesheets().add(ConfirmPopupController.class.getResource("/org/tin/oop2_capstone/styles/application.css").toExternalForm());

            //Create and show
            Stage popupStage = new Stage();
            popupStage.initStyle(StageStyle.UNDECORATED);
            popupStage.initModality(Modality.NONE);
            popupStage.setTitle(title);
            popupStage.setScene(scene);

            //add blur
            Node mainAppRoot = null;
            for (Window window : Window.getWindows()) {
                if (window.isShowing() && window.getScene() != null) {
                    mainAppRoot = window.getScene().getRoot();
                    mainAppRoot.setEffect(new GaussianBlur(15));
                    break;
                }
            }

            popupStage.showAndWait();

            //remove blur after show is exited
            if (mainAppRoot != null) {
                mainAppRoot.setEffect(null);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setConfirmMessage(String message) {
        if(this.confirmMessageLabel != null) {
            this.confirmMessageLabel.setText(message);
        }
    }

    // Handles inline overlay setup with customizable blur and button text
    public void setupDeleteMode(String itemName, Runnable onConfirm, Node mainContent) {
        this.onConfirmAction = onConfirm;
        this.mainContentToBlur = mainContent;
        this.confirmMessageLabel.setText("Are you sure you want to delete \"" + itemName + "\"?");
        this.confirmActionButton.setText("Yes, Delete");
        this.cancelActionButton.setText("No, Cancel");

        if (mainContentToBlur != null) {
            mainContentToBlur.setEffect(new BoxBlur(6, 6, 3));
        }
    }

    private void closeWindow(){
        if (mainContentToBlur != null) {
            mainContentToBlur.setEffect(null); // Clear background blur
        }

        // Closes inline overlays gracefully, falls back to closing a separate Window stage
        if (popupRoot != null && popupRoot.getParent() instanceof javafx.scene.layout.Pane) {
            ((javafx.scene.layout.Pane) popupRoot.getParent()).getChildren().remove(popupRoot);
        } else {
            Stage stage = (Stage) cancelActionButton.getScene().getWindow();
            stage.close();
        }
    }
}
