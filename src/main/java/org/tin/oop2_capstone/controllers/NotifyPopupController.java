package org.tin.oop2_capstone.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

public class NotifyPopupController {
    @FXML private VBox notifyPopupVBox;
    @FXML private SVGPath notifySVGPath;
    @FXML private Label notifyTitleLabel;
    @FXML private Label notifyMessageLabel;

    public void setNotifySVGPath(String path, Color color) {
        notifySVGPath.setContent(path);
        notifySVGPath.setFill(color);
    }

    public void setNotifyTitleLabel(String title) {
        this.notifyTitleLabel.setText(title);
    }

    public void setNotifyMessageLabel(String message) {
        this.notifyMessageLabel.setText(message);
    }

    public void setupWarningPopup(String title, String message){
        setNotifyTitleLabel(title);
        setNotifyMessageLabel(message);
        setNotifySVGPath("M11 15h2v2h-2zm0-8h2v6h-2zm.99-5C6.47 2 2 6.48 2 12s4.47 10 9.99 10C17.52 22 22 17.52 22 12S17.52 2 11.99 2zM12 20c-4.42 0-8-3.58-8-8s3.58-8 8-8 8 3.58 8 8-3.58 8-8 8z",
                Color.rgb(255, 0, 4));
    }

    public void setupSuccessPopup(String title, String message){
        setNotifyTitleLabel(title);
        setNotifyMessageLabel(message);
        setNotifySVGPath("M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z",
                Color.rgb(34, 197, 94));
    }

}
