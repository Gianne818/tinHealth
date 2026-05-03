package org.tin.oop2_capstone.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import javafx.event.ActionEvent;
import java.io.IOException;

public class SignUpController {

    @FXML
    private Button buttonSignUp;

    @FXML
    public void onLoginClicked(MouseEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/tin/oop2_capstone/views/login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setHeight(400);
        stage.setWidth(650);

        String style = getClass().getResource("/org/tin/oop2_capstone/styles/application.css").toExternalForm();
        scene.getStylesheets().add(style);
        stage.setResizable(false);

        stage.setTitle("Health Tracker");
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

}
