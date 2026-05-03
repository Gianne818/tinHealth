package org.tin.oop2_capstone.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {
    @FXML public Button buttonLogin;

    @FXML public void onLoginButtonClicked(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/tin/oop2_capstone/views/main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        String style = getClass().getResource("/org/tin/oop2_capstone/styles/application.css").toExternalForm();
        scene.getStylesheets().add(style);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setResizable(true);

        stage.setMinWidth(900);
        stage.setMinHeight(850);

        stage.setTitle("Health Tracker");
        stage.setScene(scene);

        stage.setMaximized(true);
        stage.show();

    }

    @FXML
    public void onSignupClicked(MouseEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/tin/oop2_capstone/views/signup-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());


        String style = getClass().getResource("/org/tin/oop2_capstone/styles/application.css").toExternalForm();
        scene.getStylesheets().add(style);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.setHeight(700);
        stage.setWidth(550);
        stage.setResizable(false);
        stage.centerOnScreen();
        stage.show();
    }

}
