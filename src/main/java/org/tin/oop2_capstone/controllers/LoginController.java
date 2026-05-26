package org.tin.oop2_capstone.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import org.tin.oop2_capstone.database.RetrieveData;
import org.tin.oop2_capstone.database.repositories.ActivityRepository;
import org.tin.oop2_capstone.database.repositories.MealRepository;
import org.tin.oop2_capstone.database.repositories.UserPrefRepository;
import org.tin.oop2_capstone.database.repositories.UserRepository;
import org.tin.oop2_capstone.model.entities.User;
import org.tin.oop2_capstone.services.DependencyService;
import org.tin.oop2_capstone.utils.SceneSwitcher;
import org.tin.oop2_capstone.services.SessionManager;
import java.io.IOException;

public class LoginController {

    @FXML public Button buttonLogin;
    @FXML private Label signUpLabel;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    @FXML private Label invalidCredentialsLabel;

    private UserRepository userRepository;
    private MealRepository mealRepository;
    private ActivityRepository activityRepository;
    private UserPrefRepository userPrefRepository;

    public LoginController() {
        this.userRepository = DependencyService.getUserRepository();
        this.mealRepository = DependencyService.getMealRepository();
        this.activityRepository = DependencyService.getActivityRepository();
        this.userPrefRepository = DependencyService.getUserPrefRepository();
    }

    @FXML
    public void onLoginButtonClicked(ActionEvent event){
        String username = usernameField.getText();
        String password = passwordField.getText();

        userRepository.login(username, password);
        User user = userRepository.getUser();
        if(user == null) {
            //todo show error message

            invalidCredentialsLabel.setManaged(true);
            return;
        }
        else {
            SessionManager.getInstance().setCurrentUser(user);

            userPrefRepository.fetchUserPrefs(user.getUid());
            SessionManager.getInstance().setCurrentUserPrefs(userPrefRepository.getUserPreferences());

            activityRepository.fetchInitialActivityData(user.getUid());
            mealRepository.fetchInitialMealData(user.getUid());

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
                .setStyleClasses(new String[]{"light"})
                .setPrefDimensions(550, 700)
                .setResizeable(false)
                .setCentered(true)
                .switchScene();
    }
}