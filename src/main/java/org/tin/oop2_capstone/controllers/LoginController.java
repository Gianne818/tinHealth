package org.tin.oop2_capstone.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import org.tin.oop2_capstone.database.repositories.UserRepository;
import org.tin.oop2_capstone.model.entities.User;
import org.tin.oop2_capstone.utils.SceneSwitcher;
import org.tin.oop2_capstone.services.SessionManager;
import java.io.IOException;

public class LoginController {

    @FXML public Button buttonLogin;
    @FXML Label signUpLabel;
    @FXML TextField usernameField;
    @FXML PasswordField passwordField;

    @FXML Label invalidCredentialsLabel;



    @FXML
    public void onLoginButtonClicked(ActionEvent event){
        String username = usernameField.getText();
        String password = passwordField.getText();

        User user = UserRepository.getUser(username, password);
        if(user == null) {
            //todo show error message
            invalidCredentialsLabel.setManaged(true);
            return;
        }
        else {
            SessionManager.getInstance().setCurrentUser(user);
            SceneSwitcher.use(buttonLogin, "main-view")
                    .setCss("application")
                    .setMinDimensions(900, 850)
                    .setMaximized(true)
                    .setResizeable(true)
                    .setTitle("+inHealth")
                    .switchScene();
        }
    }


    @FXML
    public void onSignupClicked(MouseEvent event) throws IOException {
        SceneSwitcher.use(signUpLabel, "signup-view")
                .setCss("application")
                .setPrefDimensions(550, 700)
                .setResizeable(false)
                .setCentered(true)
                .switchScene();
    }
}