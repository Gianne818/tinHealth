package org.tin.oop2_capstone.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.SVGPath;

public class NotificationCardController {
    @FXML GridPane notifParentGridPane;
    @FXML StackPane imageStackPane;
    @FXML Circle iconCircle;
    @FXML ImageView iconImageView;
    @FXML Label notifTitleLabel;
    @FXML SVGPath newNotifIndicatorSVGPath;
    @FXML Label notifDescriptionLabel;
    @FXML Label timeAgoLabel;
    @FXML StackPane dismissNotifStackPane;

    private Runnable onDeleteAction;

    public void initialize() {
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
        notifParentGridPane.setOnMouseClicked(e -> markAsRead());
    }

    public void setTitle(String title) {
        notifTitleLabel.setText(title);
    }

    public void setMessage(String msg) {
        notifDescriptionLabel.setText(msg);
    }

    public void setPassedTime(String time) {
        timeAgoLabel.setText(time);
    }

    public void setType(String type) {
        String pngPath = switch (type) {
            case "achievement" -> "/org/tin/oop2_capstone/assets/icons/trophy.png";
            case "goal"        -> "/org/tin/oop2_capstone/assets/icons/target.png";
            case "activity"    -> "/org/tin/oop2_capstone/assets/icons/activity.png";
            default            -> "/org/tin/oop2_capstone/assets/icons/bell.png";
        };
        String bgColor = switch (type) {
            case "achievement" -> "#FEF3C7";
            case "goal"        -> "#D1FAE5";
            case "activity"    -> "#FEE2E2";
            default            -> "#EDE9FE";
        };
        iconImageView.setImage(new Image(getClass().getResource(pngPath).toExternalForm()));
        iconCircle.setFill(Color.web(bgColor));
    }

    public void setRead(boolean read) {
        newNotifIndicatorSVGPath.setVisible(!read);
        newNotifIndicatorSVGPath.setManaged(!read);
        if (read) notifParentGridPane.setOpacity(0.55);
    }

    public void setOnDeleteAction(Runnable action) {
        this.onDeleteAction = action;
    }

    private void markAsRead() {
        newNotifIndicatorSVGPath.setVisible(false);
        newNotifIndicatorSVGPath.setManaged(false);
        notifParentGridPane.setOpacity(0.55);
    }

    @FXML
    public void deleteCurrent() {
        if (onDeleteAction != null) {
            onDeleteAction.run();
        }
    }
}