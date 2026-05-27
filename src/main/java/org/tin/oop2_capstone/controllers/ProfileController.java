package org.tin.oop2_capstone.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import org.tin.oop2_capstone.model.entities.User;
import org.tin.oop2_capstone.services.SessionManager;
import org.tin.oop2_capstone.utils.InputManager;
import org.tin.oop2_capstone.utils.SceneSwitcher;
import org.tin.oop2_capstone.utils.TimeFormatter;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class ProfileController {

    private final User u = SessionManager.getInstance().getCurrentUser();
    @FXML
    private ScrollPane profileScrollPane;

    @FXML private DatePicker bdayDatePicker;
    @FXML
    private TextField emailTextField,
            fullnameTextField,
            weightTextField,
            heightTextField;
    @FXML
    private ChoiceBox<String> genderChoiceBox, activityLevelChoiceBox;
    @FXML
    private Label fullnameLabel, emailLabel;

    @FXML
    Button signOutButton, saveSettingsButton;
    @FXML
    private StackPane profileRootPane;

    public void initialize(){
        String gender = u.getIsMale() ? "Male" : "Female";
//        System.out.println(u);

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);
        String formattedDOB = dateTimeFormatter.format(u.getDateOfBirth());

        emailTextField.setText(u.getEmail());
        fullnameTextField.setText(u.getFullname());
        bdayDatePicker.getEditor().setText(formattedDOB);
        weightTextField.setText(String.format("%.1f", u.getWeightKg()));
        genderChoiceBox.getItems().addAll("Male", "Female");
        activityLevelChoiceBox.getItems().addAll(
                "Sedentary - Little to no exercise",
                "Lightly Active - Exercise 1-3 days/week",
                "Moderately Active - Exercise 3-5 days/week",
                "Very Active - Exercise 6-7 days/week",
                "Extremely Active - Athlete/Physical Job"
                );
        activityLevelChoiceBox.setValue(u.getActivityLevel());
        genderChoiceBox.setValue(gender);
        fullnameLabel.setText(u.getUsername());
        heightTextField.setText(String.format("%.0f", u.getHeightCm()));
        emailLabel.setText(u.getEmail());

        InputManager.acceptOnlyDouble(weightTextField);
        InputManager.acceptOnlyDouble(heightTextField);
    }


    @FXML
    private void onSignOutButtonClick() {
        showConfirmPopup(popupController -> {
            popupController.setConfirmMessage("Are you sure you want to sign out?");
            popupController.setOnConfirmAction(() ->
                    SceneSwitcher.use(signOutButton, "login-view")
                            .setCss("application")
                            .setPrefDimensions(650, 400)
                            .setMaximized(false)
                            .setResizeable(false)
                            .setTitle("+inHealth")
                            .switchScene()
            );
            getMainContent().setEffect(new javafx.scene.effect.BoxBlur(6, 6, 3));
        });
    }

    @FXML
    private void onSaveSettingsClick() {
        showConfirmPopup(popupController -> popupController.setupSaveMode(
                () -> {
                    //IMPLEMENT CORRECT SAVE SETTING
                    //System.out.println("Settings saved.");
                },
                getMainContent()
        ));
    }


    private void showConfirmPopup(java.util.function.Consumer<ConfirmPopupController> setup) {
        try {
            var url = getClass().getResource("/org/tin/oop2_capstone/views/confirm-popup-view.fxml");
            //System.out.println("Popup URL: " + url);

            FXMLLoader loader = new FXMLLoader(url);
            Node popupNode = loader.load();
            ConfirmPopupController controller = loader.getController();
            setup.accept(controller);

            AnchorPane contentPane = MainController.getInstance().getAnchorPaneContent();
            AnchorPane.setTopAnchor(popupNode, 0.0);
            AnchorPane.setBottomAnchor(popupNode, 0.0);
            AnchorPane.setLeftAnchor(popupNode, 0.0);
            AnchorPane.setRightAnchor(popupNode, 0.0);
            contentPane.getChildren().add(popupNode);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Node getMainContent() {
        return profileScrollPane;
    }



}