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
import org.tin.oop2_capstone.utils.SceneSwitcher;

import java.io.IOException;
import java.util.ArrayList;

public class SignUpController {

    @FXML private Label loginLabel;
    @FXML private VBox createAccountVBox;
    @FXML private Button signUpButton;
    @FXML private Label passwordErrorLabel;
    @FXML private Label genericErrorLabel;

    @FXML private TextField fullNameTextField;
    @FXML private TextField userNameTextField;
    @FXML private TextField emailTextField;
    @FXML private TextField passwordTextField;
    @FXML private TextField confirmPasswordTextField;
    private ObservableList<TextField> fields;

    @FXML private BorderPane onBoardingBorderPane;
    @FXML private ProgressBar onBoardingProgressBar;
    @FXML private VBox onBoardingVBox1;
    @FXML private VBox onBoardingVBox2;
    @FXML private VBox onBoardingVBox3;
    private ObservableList<VBox> panels;
    @FXML private Button nextButton;
    @FXML private Button backButton;

    @FXML private GridPane sedentaryGridPane;
    @FXML private GridPane lightlyActiveGridPane;
    @FXML private GridPane moderatelyActiveGridPane;
    @FXML private GridPane veryActiveGridPane;
    @FXML private GridPane extremelyActiveGridPane;
    private ObservableList<GridPane> activityLevels;

    @FXML private VBox continueVBox;
    @FXML private Button continueButton;

    @FXML private DatePicker bdayDatePicker;
    @FXML private ChoiceBox<String> genderChoiceBox;
    @FXML private TextField currentHeightTextField, currentWeightTextField, targetWeightTextField;

    private GridPane currSelectedActivity;
    int curPanel = 0;

    private void checkIfEnableNext(int curPanel){
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
        // todo: create user and add to db

        String fullName = fullNameTextField.getText();
        String email = emailTextField.getText();
        String userName = userNameTextField.getText();
        String confirmPass = confirmPasswordTextField.getText();
        String pass = passwordTextField.getText();


        boolean allFilled = false;
        boolean passwordsMatch = false;

        for(TextField t : fields){
            if(t.getText().isEmpty()){
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
            passwordsMatch = false;
        } else {
            changeElementAccessibility(passwordErrorLabel, false, true, false);
            passwordsMatch = true;
        }

        if(allFilled && passwordsMatch) {
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
        // todo: based on currSelectedActivity, we set goals automatically. User can change them in settings
    }

    public  void onContinueButtonClick(ActionEvent event){
        SceneSwitcher.use(backButton, "main-view").setCss("application").setMinDimensions(900, 850).setMaximized(true).setResizeable(true).setTitle("+inHealth").switchScene();
    }

    private void setSelectedActivityLevel(Node n){
        for(GridPane g : activityLevels){
            g.getStyleClass().remove("activityLevelSelected");
        }

        n.getStyleClass().add("activityLevelSelected");

    }




}
