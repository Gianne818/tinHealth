package org.tin.oop2_capstone.controllers;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.tin.oop2_capstone.database.repositories.SettingsRepository;
import org.tin.oop2_capstone.model.entities.UserPreferences;
import org.tin.oop2_capstone.services.SessionManager;
import org.tin.oop2_capstone.utils.InputManager;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.IntStream;

public class SettingsController implements Initializable {
    @FXML public ScrollPane settingsScrollPane;
    @FXML public ToggleButton exercisePrompts;
    @FXML public ChoiceBox<Integer> exerciseIntensity;
    @FXML public Slider promptFrequency;
    @FXML public ChoiceBox<String> theme;
    @FXML public ToggleButton exerciseReminders;
    @FXML public ToggleButton mealReminders;
    @FXML public ToggleButton achievementNotifications;
    @FXML public ComboBox<Integer> weeklyActivityGoalComboBox;
    @FXML public Button saveSettings;

    @FXML public ComboBox<Integer> caloriesGoalInComboBox;
    @FXML public ComboBox<Integer> caloriesGoalBurnedComboBox;
    @FXML TextField targetWeightTextField;

    private SettingsRepository settingsRepository;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        settingsRepository = SettingsRepository.getInstance();
        initializeControls();
        loadUserPreferences();
    }

    private void initializeControls() {
        exerciseIntensity.getItems().addAll(IntStream.rangeClosed(1, 10).boxed().toList());
        theme.getItems().addAll("Light", "Dark");
        weeklyActivityGoalComboBox.getItems().addAll(IntStream.rangeClosed(1, 7).boxed().toList());
        System.out.println("TEST");

        //populate options for calorieG cbox
        for (int i = 500; i <= 5000; i += 100) {
            caloriesGoalInComboBox.getItems().add(i);
        }

        //populate options for calorieBurnG cbox
        for (int i = 100; i <= 3000; i += 100) {
            caloriesGoalBurnedComboBox.getItems().add(i);
        }
        caloriesGoalInComboBox.setVisibleRowCount(6);
        caloriesGoalBurnedComboBox.setVisibleRowCount(6);
        weeklyActivityGoalComboBox.setVisibleRowCount(6);

        promptFrequency.setMin(1);
        promptFrequency.setMax(8);
        promptFrequency.setValue(6);
        promptFrequency.setMajorTickUnit(1);
        promptFrequency.setMinorTickCount(0);
        promptFrequency.setSnapToTicks(true);
        promptFrequency.setShowTickLabels(true);
        promptFrequency.setShowTickMarks(true);

        InputManager.acceptOnlyDouble(targetWeightTextField);
    }

    private void showConfirmPopup(Runnable onConfirm){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/tin/oop2_capstone/views/confirm-popup-view.fxml"));
            StackPane popup = loader.load();
            ConfirmPopupController controller = loader.getController();

            AnchorPane root = (javafx.scene.layout.AnchorPane) settingsScrollPane.getScene().getRoot();
            Node mainContent = root.getChildren().getFirst();

            AnchorPane.setTopAnchor(popup, 0.0);
            AnchorPane.setBottomAnchor(popup, 0.0);
            AnchorPane.setLeftAnchor(popup, 0.0);
            AnchorPane.setRightAnchor(popup, 0.0);

            controller.setupSaveMode(onConfirm, mainContent);
            root.getChildren().add(popup);
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void showWarningPopup(String title, String message){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/tin/oop2_capstone/views/notify-popup-view.fxml"));
            StackPane popup = loader.load();
            NotifyPopupController controller = loader.getController();
            AnchorPane root = (javafx.scene.layout.AnchorPane) settingsScrollPane.getScene().getRoot();
            Node mainContent = root.getChildren().getFirst();

            AnchorPane.setTopAnchor(popup, 0.0);
            AnchorPane.setBottomAnchor(popup, 0.0);
            AnchorPane.setLeftAnchor(popup, 0.0);
            AnchorPane.setRightAnchor(popup, 0.0);

            controller.setupWarningPopup(title, message);
            root.getChildren().add(popup);
            PauseTransition delay = new PauseTransition(Duration.seconds(3));
            delay.setOnFinished(event -> root.getChildren().remove(popup));
            delay.play();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showSuccessPopup(String title, String message){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/tin/oop2_capstone/views/notify-popup-view.fxml"));
            StackPane popup = loader.load();
            NotifyPopupController controller = loader.getController();
            AnchorPane root = (javafx.scene.layout.AnchorPane) settingsScrollPane.getScene().getRoot();
            Node mainContent = root.getChildren().getFirst();

            AnchorPane.setTopAnchor(popup, 0.0);
            AnchorPane.setBottomAnchor(popup, 0.0);
            AnchorPane.setLeftAnchor(popup, 0.0);
            AnchorPane.setRightAnchor(popup, 0.0);

            controller.setupSuccessPopup(title, message);
            root.getChildren().add(popup);
            PauseTransition delay = new PauseTransition(Duration.seconds(2));
            delay.setOnFinished(event -> root.getChildren().remove(popup));
            delay.play();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadUserPreferences() {
        UserPreferences preferences = settingsRepository.load();
        if (preferences != null) {
            exercisePrompts.setSelected(preferences.isEnableExercisePrompts());

            //Change to verify before displaying. Also, reminder that DB has no ExerciseIntensity column in UserPreference
            if (settingsRepository.isExerciseIntensityValid(preferences.getExerciseIntensity())) {
                exerciseIntensity.setValue(preferences.getExerciseIntensity());
            } else {
                exerciseIntensity.setValue(5); //default to mid
            }

            int freqHours = Math.clamp(preferences.getPromptFrequencyMins() / 60, 1, 24);
            promptFrequency.setValue(freqHours);

            //corrects capitalization dark -> Dark ; daRk -> Dark
            if (preferences.getTheme() != null) {
                String capitalized = preferences.getTheme().substring(0, 1).toUpperCase() + preferences.getTheme().substring(1).toLowerCase();
                theme.setValue(capitalized);
            }

            exerciseReminders.setSelected(preferences.isExerciseReminders());
            mealReminders.setSelected(preferences.isMealReminders());
            achievementNotifications.setSelected(preferences.isAchievementNotifications());

            targetWeightTextField.setText(preferences.getTargetWeightKG() > 0 ? String.valueOf(preferences.getTargetWeightKG()) : "");

            //Snap calorieInG to nearest 100 in list
            int calIn = (int) Math.round(preferences.getDailyCalorieIn() / 100.0) * 100;
            calIn = Math.clamp(calIn, 500, 5000);
            caloriesGoalInComboBox.setValue(calIn);

            //Snap calorieOutG to nearest 100 in list
            int calOut = (int) Math.round(preferences.getDailyCalorieOut() / 100.0) * 100;
            calOut = Math.max(100, Math.min(3000, calOut));
            caloriesGoalBurnedComboBox.setValue(calOut);

            weeklyActivityGoalComboBox.setValue(
                    preferences.getWeeklyActivityReps() >= 1 && preferences.getWeeklyActivityReps() <= 7
                            ? preferences.getWeeklyActivityReps()
                            : 3
            );
        }

    }
     /** just commented out for other testing purposes on other features */
    @FXML
    public void onSaveButtonClicked(ActionEvent event) {
        double targetWeight = 0;
        if (targetWeightTextField.getText() == null || targetWeightTextField.getText().isEmpty()) {
            showWarningPopup("Missing Fields", "Enter 0 for target weight to set weight target goal to \"Maintain\"");
            return;
        }
        String weightText = targetWeightTextField.getText().trim();

        if (exerciseIntensity.getValue() == null) {
            showWarningPopup("Missing Fields", "Please select an exercise intensity.");
            return;
        }
        if (theme.getValue() == null) {
            showWarningPopup("Missing Fields", "Please select a theme.");
            return;
        }
        if (caloriesGoalInComboBox.getValue() == null) {
            showWarningPopup("Missing Fields", "Please select a daily calories in goal.");
            return;
        }
        if (caloriesGoalBurnedComboBox.getValue() == null) {
            showWarningPopup("Missing Fields", "Please select a daily calories burned goal.");
            return;
        }
        if (weeklyActivityGoalComboBox.getValue() == null) {
           showWarningPopup("Missing Fields", "Please select a weekly activity goal.");
            return;
        }

        //Goal Mismatches
        try {
            targetWeight = Double.parseDouble(weightText);

            double heightCm = SessionManager.getInstance().getCurrentUser().getHeightCm();
            double heightMeters = heightCm / 100.0;
            double minSafeWeight = 18.5 * (heightMeters * heightMeters);

            final double validatedWeight = targetWeight;

            if(targetWeight == 0){
                showConfirmPopup(() -> {
                    SessionManager.getInstance().getCurrentUserPrefs().setGoalType("Maintain");
                    savePreferences(validatedWeight);
                });
                return;
            }

            if (targetWeight < minSafeWeight) {
                String formattedMinWeight = String.format("%.1f", minSafeWeight);
                showWarningPopup("Unsafe Target!", "Based on your height, your minimum safe weight is " + formattedMinWeight + " kg.");
                return;
            }

            double currentWeight = SessionManager.getInstance().getCurrentUser().getWeightKg();
            String currentGoal = SessionManager.getInstance().getCurrentUserPrefs().getGoalType();


            //if goal is Lose but target weight is HIGHER than current. My solution is to treat it as a typo by the user since the user explicitly selected lose weight in registration.
            //BUT long time user wanting to switch goals might need to be evalutade so ill mark this TODO:
            if (targetWeight > currentWeight) {
                showConfirmPopup(() -> {
                    SessionManager.getInstance().getCurrentUserPrefs().setGoalType("Gain");
                    savePreferences(validatedWeight);
                });
                return;
            }

            //if goal is gain/build muscle but target weight is LOWER than current → suggest switching to Lose
            if (targetWeight < currentWeight) {
                showConfirmPopup(() -> {
                    SessionManager.getInstance().getCurrentUserPrefs().setGoalType("Lose");
                    savePreferences(validatedWeight);
                });
//
                return;
            }

            //if goal is maintain but target differs significantly from current then suggest switching goals
            if (currentGoal.equals("Maintain")) {
                if (targetWeight < currentWeight - 2) {
                    showConfirmPopup(() -> {
                        SessionManager.getInstance().getCurrentUserPrefs().setGoalType("Lose");
                        savePreferences(validatedWeight);
                    });
//
                    return;
                } else if (targetWeight > currentWeight + 2) {
                    showConfirmPopup(() -> {
                        SessionManager.getInstance().getCurrentUserPrefs().setGoalType("Gain");
                        savePreferences(validatedWeight);
                    });
//
                    return;
                }
            }
        } catch (NumberFormatException e) {
            showWarningPopup("Invalid Input", "Target weight must be a valid number.");
            return;
        }

        //prompt for confirmation
        final double finalWeight  = targetWeight;
        confirmPrompt(() -> savePreferences(finalWeight));
    }

    //bridge to wait for confirm
    private void confirmPrompt(Runnable onConfirmAction) {
        showConfirmPopup(onConfirmAction);
    }

    // Extracted method to perform the actual database/repository operations
    private void savePreferences(double targetWeight) {
        UserPreferences current = SessionManager.getInstance().getCurrentUserPrefs();
        UserPreferences updated = new UserPreferences();

        updated.setUserPrefID(current != null ? current.getUserPrefID() : 0);
        updated.setGoalType(current != null ? current.getGoalType() : "Maintain");

        updated.setEnableExercisePrompts(exercisePrompts.isSelected());
        updated.setExerciseIntensity(exerciseIntensity.getValue());
        updated.setPromptFrequencyMins((int) promptFrequency.getValue() * 60);
        updated.setTheme(theme.getValue());
        updated.setExerciseReminders(exerciseReminders.isSelected());
        updated.setMealReminders(mealReminders.isSelected());
        updated.setAchievementNotifications(achievementNotifications.isSelected());
        updated.setTargetWeightKG(targetWeight);
        updated.setDailyCalorieIn(caloriesGoalInComboBox.getValue());
        updated.setDailyCalorieOut(caloriesGoalBurnedComboBox.getValue());
        updated.setWeeklyActivityReps(weeklyActivityGoalComboBox.getValue());

        int userId = SessionManager.getInstance().getCurrentUser().getUid();

        if (settingsRepository.save(updated, userId)) {
            MainController mc = MainController.getInstance();
            if (mc != null) {
                mc.applyThemeStylesheet();
                mc.applyThemeClasses();
            }
            showSuccessPopup("Success", "Settings saved successfully.");
        } else {
           showWarningPopup("Error", "Something went wrong. Try again.");
        }
    }

}
