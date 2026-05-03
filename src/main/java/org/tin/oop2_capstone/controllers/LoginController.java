package org.tin.oop2_capstone.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.tin.oop2_capstone.utils.SceneSwitcher;

import java.io.IOException;

public class LoginController {
    @FXML public Button buttonLogin;
    @FXML Label signUpLabel;

    @FXML public void onLoginButtonClicked(ActionEvent event) throws IOException {
        SceneSwitcher.use(buttonLogin, "main-view").setCss("application").setMinDimensions(900, 850).setMaximized(true).setResizeable(true).setTitle("+inHealth").switchScene();
    }

    @FXML
    public void onSignupClicked(MouseEvent event) throws IOException {
        SceneSwitcher.use(signUpLabel, "signup-view").setCss("application").setPrefDimensions(550, 700).setResizeable(false).setCentered(true).switchScene();
    }

}
