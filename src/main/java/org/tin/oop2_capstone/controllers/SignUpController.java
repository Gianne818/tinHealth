package org.tin.oop2_capstone.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
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

    @FXML Label loginLabel;
    @FXML VBox createAccountVBox;
    @FXML Button signUpButton;
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

    private GridPane currSelectedActivity;


    int curPanel = 0;

    private void changeElementAccessibility(Node n, boolean visibility, boolean disability){
        n.setVisible(visibility);
        n.setDisable(disability);
    }

    public void initialize(){
        panels = FXCollections.observableArrayList();
        activityLevels = FXCollections.observableArrayList();
        panels.addAll(onBoardingVBox1, onBoardingVBox2, onBoardingVBox3);
        activityLevels.addAll(sedentaryGridPane, lightlyActiveGridPane, moderatelyActiveGridPane, veryActiveGridPane, extremelyActiveGridPane);
        changeElementAccessibility(backButton,  true, true);
        onBoardingProgressBar.setProgress(0.05);
    }


    public void onBackButtonClick(ActionEvent event){
        changeElementAccessibility(panels.get(curPanel), false, true);
        curPanel--;

        if(curPanel == 0){
            changeElementAccessibility(backButton,  true, true);
        }

        changeElementAccessibility(panels.get(curPanel), true, false);
        onBoardingProgressBar.setProgress(onBoardingProgressBar.getProgress()-0.33);
    }

    public void onNextButtonClick(ActionEvent event){
        changeElementAccessibility(panels.get(curPanel), false, true);
        curPanel++;

        if(curPanel!=0){
            changeElementAccessibility(backButton, true, false);
        }
        if(curPanel == panels.size()){
            changeElementAccessibility(onBoardingBorderPane, false, true);
            changeElementAccessibility(continueVBox, true, false);
            return;
        }

        changeElementAccessibility(panels.get(curPanel), true, false);
        onBoardingProgressBar.setProgress(onBoardingProgressBar.getProgress()+0.33);
    }

    public void onSignUpButtonClick(ActionEvent event){
        // todo: create user and add to db
        changeElementAccessibility(createAccountVBox,false, true);
        changeElementAccessibility(onBoardingBorderPane, true, false);
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
