package org.tin.oop2_capstone.controllers;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;


public class WarningPopupController {

    @FXML private Label warningPopupTitle;
    @FXML private Label warningMessageLabel;

    public static void showPopup(String title, String message) {
        try {
            FXMLLoader loader = new FXMLLoader(WarningPopupController.class.getResource("/org/tin/oop2_capstone/views/warning-popup-view.fxml"));
            Parent root = loader.load();

            WarningPopupController controller = loader.getController();
            controller.setWarningMessage(message);
            controller.setWarningPopupTitle(title);

            Scene scene = new Scene(root);
            scene.getStylesheets().add(ConfirmPopupController.class.getResource("/org/tin/oop2_capstone/styles/application.css").toExternalForm());

            //Create and show
            Stage popupStage = new Stage();
            popupStage.initStyle(StageStyle.UNDECORATED);
            popupStage.initModality(Modality.NONE);
            popupStage.setTitle(title);
            popupStage.setScene(scene);

            //Outside area closer
            popupStage.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
                if (!isNowFocused) {
                    popupStage.close();
                }
            });

            popupStage.show();

            //Timed display duration 2 seconds
            PauseTransition delay = new PauseTransition(Duration.seconds(5));
            delay.setOnFinished(event -> popupStage.close());
            delay.play();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setWarningMessage(String message) {
        if(warningMessageLabel != null) {
            this.warningMessageLabel.setText(message);
        }
    }

    public void setWarningPopupTitle(String title) {
        if(warningPopupTitle != null) {
            this.warningPopupTitle.setText(title);
        }
    }

}
