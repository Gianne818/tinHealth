package org.tin.oop2_capstone.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javafx.event.ActionEvent;
import org.tin.oop2_capstone.database.InsertData;
import org.tin.oop2_capstone.database.RetrieveData;
import org.tin.oop2_capstone.model.entities.User;
import org.tin.oop2_capstone.model.entities.UserPreferences;
import org.tin.oop2_capstone.utils.SceneSwitcher;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class SignUpController {

    @FXML Label loginLabel;
    @FXML VBox createAccountVBox;
    @FXML Button signUpButton;
    @FXML Label passwordErrorLabel;
    @FXML Label genericErrorLabel;
    @FXML Label usernameErrorLabel;
    @FXML Label emailErrorLabel;

    @FXML TextField fullNameTextField;
    @FXML TextField userNameTextField;
    @FXML TextField emailTextField;
    @FXML TextField passwordTextField;
    @FXML TextField confirmPasswordTextField;
    ObservableList<TextField> fields;

    @FXML BorderPane onBoardingBorderPane;
    @FXML ProgressBar onBoardingProgressBar;
    @FXML VBox onBoardingVBox1;
    @FXML VBox onBoardingVBox2;
    @FXML VBox onBoardingVBox3;
    ObservableList<VBox> panels;
    @FXML Button nextButton;
    @FXML Button backButton;

    @FXML GridPane sedentaryGridPane;
    @FXML GridPane lightlyActiveGridPane;
    @FXML GridPane moderatelyActiveGridPane;
    @FXML GridPane veryActiveGridPane;
    @FXML GridPane extremelyActiveGridPane;
    ObservableList<GridPane> activityLevels;

    @FXML VBox continueVBox;
    @FXML Button continueButton;

    @FXML DatePicker bdayDatePicker;
    @FXML ChoiceBox<String> genderChoiceBox;
    @FXML TextField currentHeightTextField, currentWeightTextField, targetWeightTextField;

    private GridPane currSelectedActivity;
    int curPanel = 0;
    private User user;
    private UserPreferences userPref;

    private void checkIfEnableNext(int curPanel){
        // todo check if fields are valid for each panel then setVisible if okay na
        // todo (future: handle diff input cases)
        switch (curPanel){
            case 0:
                if(bdayDatePicker.getValue() != null && genderChoiceBox.getValue() != null){
                    nextButton.setDisable(false);
                } else {
                    nextButton.setDisable(true);
                }
                break;
            case 1:
                if(!currentHeightTextField.getText().isEmpty() &&
                        !currentWeightTextField.getText().isEmpty()){
                    nextButton.setDisable(false);
                } else {
                    nextButton.setDisable(true);
                }
                break;
            case 2:
                for(GridPane g : activityLevels){
                    if(g.getStyleClass().contains("activityLevelSelected")){
                        nextButton.setDisable(false);
                        return;
                    }
                }
                nextButton.setDisable(true);
                break;

        }
    }


    private void changeElementAccessibility(Node n, boolean visibility, boolean disability, boolean manageability){
        n.setVisible(visibility);
        n.setDisable(disability);
        n.setManaged(manageability);
    }

    public void initialize(){
        fields = FXCollections.observableArrayList();
        panels = FXCollections.observableArrayList();
        activityLevels = FXCollections.observableArrayList();

        fields.addAll(fullNameTextField, emailTextField, userNameTextField, passwordTextField, confirmPasswordTextField);
        panels.addAll(onBoardingVBox1, onBoardingVBox2, onBoardingVBox3);
        activityLevels.addAll(sedentaryGridPane, lightlyActiveGridPane, moderatelyActiveGridPane, veryActiveGridPane, extremelyActiveGridPane);

        genderChoiceBox.getItems().addAll("Male", "Female");
;
        changeElementAccessibility(backButton,  true, true, true);
        changeElementAccessibility(nextButton,  true, true, true);
        onBoardingProgressBar.setProgress(0.05);

        bdayDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            checkIfEnableNext(curPanel);
        });
        genderChoiceBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            checkIfEnableNext(curPanel);
        });
        currentHeightTextField.textProperty().addListener((obs, oldVal, newVal) -> {
            checkIfEnableNext(curPanel);
        });
        currentWeightTextField.textProperty().addListener((obs, oldVal, newVal) -> {
            checkIfEnableNext(curPanel);
        });

        /* Initialize nako daan ang user and userpref so that i can just use setters for each fields */
        user = new User();
        userPref = new UserPreferences();
    }

    public void onBackButtonClick(ActionEvent event){
        changeElementAccessibility(panels.get(curPanel), false, true, true);
        curPanel--;

        if(curPanel == 0){
            changeElementAccessibility(backButton,  true, true, true);
        }

        changeElementAccessibility(panels.get(curPanel), true, false, true);
        onBoardingProgressBar.setProgress(onBoardingProgressBar.getProgress()-0.33);
         checkIfEnableNext(curPanel);
    }

    public void onNextButtonClick(ActionEvent event){
        /* set attributes for user for different panel */
        switch (curPanel) {
            case 0:
                /* set user attributes for second sign-up page */
                user.setDateOfBirth(bdayDatePicker.getValue());
                user.setMale(genderChoiceBox.getValue().equals("Male"));
                break;
            case 1:
                /* set User attributes for third sign-up page */
                user.setHeightCm(Double.parseDouble(currentHeightTextField.getText()));
                user.setWeightKg(Double.parseDouble(currentWeightTextField.getText()));
                userPref.setTargetWeightKG(targetWeightTextField.getText().isBlank() ? 0 : Double.parseDouble(targetWeightTextField.getText()));
                break;
            case 2:
                /* set user attributes for activity level pge */
                user.setActivityLevel(currSelectedActivity.equals(sedentaryGridPane) ? "Sedentary" :
                        currSelectedActivity.equals(lightlyActiveGridPane) ? "Lightly" :
                        currSelectedActivity.equals(moderatelyActiveGridPane) ? "Moderately" :
                        currSelectedActivity.equals(veryActiveGridPane) ? "Very Active" :
                        "Extremely Active");
                break;
        }

        changeElementAccessibility(panels.get(curPanel), false, true, true);
        curPanel++;

        if(curPanel!=0){
            changeElementAccessibility(backButton, true, false, true);
        }
        if(curPanel == panels.size()){
            changeElementAccessibility(onBoardingBorderPane, false, true, true);
            changeElementAccessibility(continueVBox, true, false, true);
            return;
        }

        changeElementAccessibility(panels.get(curPanel), true, false, true);
        onBoardingProgressBar.setProgress(onBoardingProgressBar.getProgress()+0.33);
        checkIfEnableNext(curPanel);
    }

    public void onSignUpButtonClick(ActionEvent event){
        // todo: store values starting with user

        String fullName = fullNameTextField.getText();
        String email = emailTextField.getText();
        String userName = userNameTextField.getText();
        String confirmPass = confirmPasswordTextField.getText();
        String pass = passwordTextField.getText();


        boolean allFilled = false;
        boolean passwordsMatch = false;
        boolean uniqueUsername = false;
        boolean uniqueEmail = false;

        for(TextField txtfield : fields){
            if(txtfield.getText().isEmpty()){
                changeElementAccessibility(genericErrorLabel, true, false, true);
                allFilled = false;
                break;
            } else {
                changeElementAccessibility(genericErrorLabel, false, true, false);
                allFilled = true;
            }
        }

        if(!confirmPass.equals(pass)){
            changeElementAccessibility(passwordErrorLabel,true, false, true);
        } else {
            changeElementAccessibility(passwordErrorLabel, false, true, false);
            passwordsMatch = true;
        }

        // todo check existing username and email
        if(RetrieveData.checkUsername(userName)){
            changeElementAccessibility(usernameErrorLabel, true, false, true);
        } else {
            changeElementAccessibility(usernameErrorLabel, false, true, false);
            uniqueUsername = true;
        }

        if(RetrieveData.checkEmail(email)){
            changeElementAccessibility(emailErrorLabel, true, false, true);
        } else {
            changeElementAccessibility(emailErrorLabel, false, true, false);
            uniqueEmail = true;
        }

        if(allFilled && passwordsMatch && uniqueUsername && uniqueEmail) {
            /* set user attributes found in the first sign-up panel */
            user.setFullname(fullName);
            user.setEmail(email);
            user.setUsername(userName);
            user.setPassword(pass);

            changeElementAccessibility(createAccountVBox, false, true, true);
            changeElementAccessibility(onBoardingBorderPane, true, false, true);
        }

    }

    public void onLoginClicked(MouseEvent event) throws IOException {
        SceneSwitcher.use(loginLabel, "login-view").setCss("application").setPrefDimensions(650, 400).setTitle("+inHealth - Login").setResizeable(false).setCentered(true).switchScene();
    }

    public void onActivityLevelClick(MouseEvent event){
        GridPane button = (GridPane) event.getSource();

        if(button.getStyleClass().contains("activityLevelSelected")){
            button.getStyleClass().remove("activityLevelSelected");
            currSelectedActivity = null;
        } else {
            setSelectedActivityLevel(button);
            currSelectedActivity = button;
        }
        checkIfEnableNext(curPanel);

    }

    public void onContinueButtonClick(ActionEvent event) throws SQLException {
        // todo: do the storing of ALL user data in here to the database (this is to avoid null values when creating a user)
        int user_id = InsertData.insertUser(user);

        if (user_id != -1) {
            InsertData.insertUserPref(userPref, user_id);
        } else {
            System.out.println("User insert failed, skipping userprefs.");
        }

        SceneSwitcher.use(backButton, "main-view").setCss("application").setMinDimensions(900, 850).setMaximized(true).setResizeable(true).setTitle("+inHealth").switchScene();

    }

    private void setSelectedActivityLevel(Node n){
        for(GridPane g : activityLevels){
            g.getStyleClass().remove("activityLevelSelected");
        }

        n.getStyleClass().add("activityLevelSelected");

    }




}
