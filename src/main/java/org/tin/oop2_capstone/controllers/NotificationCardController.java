package org.tin.oop2_capstone.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.SVGPath;

public class NotificationCardController {

    @FXML GridPane notifParentGridPane;
    @FXML StackPane imageStackPane;
    @FXML ImageView iconImageView;
    @FXML Label notifTitleLabel;
    @FXML SVGPath newNotifIndicatorSVGPath;
    @FXML Label notifDescriptionLabel;
    @FXML Label timeAgoLabel;
    @FXML StackPane dismissNotifStackPane;

    private Runnable onDeleteAction;

    public void initialize(){
        dismissNotifStackPane.setVisible(false);
        dismissNotifStackPane.setManaged(false);

        notifParentGridPane.setOnMouseEntered(e -> {
            dismissNotifStackPane.setManaged(true);
            dismissNotifStackPane.setVisible(true);
        });

        notifParentGridPane.setOnMouseExited(e -> {
            dismissNotifStackPane.setManaged(false);
            dismissNotifStackPane.setVisible(false);
        });
    }

    public void setTitle(String title){
        notifTitleLabel.setText(title);
    }

    public void setMessage(String msg){
        notifDescriptionLabel.setText(msg);
    }

    public void setPassedTime(String time){
        timeAgoLabel.setText(time);
    }

    public void setIcon(String path){
        iconImageView.setImage(new Image(getClass().getResource(path).toExternalForm()));
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
