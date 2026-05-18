package org.tin.oop2_capstone.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import org.tin.oop2_capstone.database.repositories.SettingsRepository;
import org.tin.oop2_capstone.model.entities.User;
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
        promptFrequency.setMax(24);
        promptFrequency.setValue(4);
        promptFrequency.setMajorTickUnit(6);
        promptFrequency.setMinorTickCount(5);
        promptFrequency.setSnapToTicks(true);
        promptFrequency.setShowTickLabels(true);
        promptFrequency.setShowTickMarks(true);

        InputManager.acceptOnlyDouble(targetWeightTextField);
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
        if (targetWeightTextField.getText() == null) {
            WarningPopupController.showPopup("Missing Fields", "Please enter a target weight.");
            return;
        }
        String weightText = targetWeightTextField.getText().trim();

        //Validate
        if (targetWeightTextField.getText().isEmpty()) {
            WarningPopupController.showPopup("Missing Fields", "Please enter a target weight.");
            return;
        }
        try {
            targetWeight = Double.parseDouble(weightText);

            double heightCm = SessionManager.getInstance().getCurrentUser().getHeightCm();
            double heightMeters = heightCm / 100.0;

            //weight(kg)*height(meters)*height(meters) = bmi. calculate safeweight based on height that results in 18.5 safe bmi
            double minSafeWeight = 18.5 * (heightMeters * heightMeters);

            //ensure calculated bmi is above 18.5
            if (targetWeight < minSafeWeight) {
                String formattedMinWeight = String.format("%.1f", minSafeWeight);
                WarningPopupController.showPopup("Unsafe Target!", "Based on your height, your minimum safe weight is " + formattedMinWeight + " kg.");
                return;
            }

            //check if goal is lose weight then dont target higher weight
            double currentWeight = SessionManager.getInstance().getCurrentUser().getWeightKg();
            if (targetWeight > currentWeight && SessionManager.getInstance().getCurrentUserPrefs().getGoalType().equals("Lose")) {
                WarningPopupController.showPopup("Goal Mismatch","Your target weight cannot be higher than your current weight if your goal is to lose weight!");
                return;
            }
        } catch (NumberFormatException e) {
            WarningPopupController.showPopup("Invalid Input", "Target weight must be a valid number.");
            return;
        }
        if (exerciseIntensity.getValue() == null) {
            WarningPopupController.showPopup("Missing Fields", "Please select an exercise intensity.");
            return;
        }
        if (theme.getValue() == null) {
            WarningPopupController.showPopup("Missing Fields", "Please select a theme.");
            return;
        }
        if (caloriesGoalInComboBox.getValue() == null) {
            WarningPopupController.showPopup("Missing Fields", "Please select a daily calories in goal.");
            return;
        }
        if (caloriesGoalBurnedComboBox.getValue() == null) {
            WarningPopupController.showPopup("Missing Fields", "Please select a daily calories burned goal.");
            return;
        }
        if (weeklyActivityGoalComboBox.getValue() == null) {
            WarningPopupController.showPopup("Missing Fields", "Please select a weekly activity goal.");
            return;
        }

        //prompt for confirmation
        final double validatedWeight = targetWeight;
        confirmPrompt(() -> savePreferences(validatedWeight));
    }

    // Accepts a Runnable containing the saving actions
    private void confirmPrompt(Runnable onConfirmAction) {
        ConfirmPopupController.showPopup("Confirm changes?", () -> {
            //when confirmed, runs savePreferences()
            onConfirmAction.run();
        });
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
            WarningPopupController.showPopup("Success", "Settings saved successfully.");
        } else {
            WarningPopupController.showPopup("Error", "Something went wrong. Try again.");
        }
    }

}
