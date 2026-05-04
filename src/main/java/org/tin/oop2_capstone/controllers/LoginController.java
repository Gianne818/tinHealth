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

    private UserRepository userRepository = new UserRepository();

    @FXML
    public void onLoginButtonClicked(ActionEvent event) throws IOException {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (userRepository.validateLogin(username, password)) {
            // Get user and save to session
            User user = userRepository.getUserByUsername(username);
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