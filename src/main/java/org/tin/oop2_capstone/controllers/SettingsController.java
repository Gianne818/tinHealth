package org.tin.oop2_capstone.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.tin.oop2_capstone.database.repositories.SettingsRepository;
import org.tin.oop2_capstone.model.entities.UserPreferences;
import org.tin.oop2_capstone.services.SessionManager;

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
        weeklyActivityGoalComboBox.getItems().addAll(IntStream.rangeClosed(0, 7).boxed().toList());
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
//        weeklyActivityGoalChoiceBox.setVisibleRowCount(7); //TODO:

        promptFrequency.setMin(1);
        promptFrequency.setMax(24);
        promptFrequency.setValue(4);
        promptFrequency.setMajorTickUnit(6);
        promptFrequency.setMinorTickCount(5);
        promptFrequency.setSnapToTicks(true);
        promptFrequency.setShowTickLabels(true);
        promptFrequency.setShowTickMarks(true);
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
                    preferences.getWeeklyActivityReps() >= 0 && preferences.getWeeklyActivityReps() <= 7
                            ? preferences.getWeeklyActivityReps()
                            : 3
            );
        }

    }
     /** just commented out for other testing purposes on other features */
    @FXML
    public void onSaveButtonClicked(ActionEvent event) {
        double targetWeight = 0;
        String weightText = targetWeightTextField.getText().trim();
        if (!weightText.isEmpty()) {
            try {
                targetWeight = Double.parseDouble(weightText);
                if (targetWeight <= 0 || targetWeight > 500) {
                    showAlert(Alert.AlertType.WARNING, "Invalid Input",
                            "Target weight must be between 0 and 500 kg.");
                    return;
                }
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.WARNING, "Invalid Input",
                        "Target weight must be a valid number.");
                return;
            }
        }

        // ── Validate required dropdowns ─────────────────────────────────────
        if (exerciseIntensity.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Missing Field", "Please select an exercise intensity.");
            return;
        }
        if (theme.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Missing Field", "Please select a theme.");
            return;
        }
        if (caloriesGoalInComboBox.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Missing Field", "Please select a daily calories in goal.");
            return;
        }
        if (caloriesGoalBurnedComboBox.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Missing Field", "Please select a daily calories burned goal.");
            return;
        }
        if (weeklyActivityGoalComboBox.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Missing Field", "Please select a weekly activity goal.");
            return;
        }

        //Update local UserPreferences
        UserPreferences current = SessionManager.getInstance().getCurrentUserPrefs();
        UserPreferences updated = new UserPreferences();

        updated.setUserPrefID(current != null ? current.getUserPrefID() : 0);
        updated.setGoalType(current != null ? current.getGoalType() : "Maintain");

        updated.setEnableExercisePrompts(exercisePrompts.isSelected());
        updated.setExerciseIntensity(exerciseIntensity.getValue());
        updated.setPromptFrequencyMins((int) promptFrequency.getValue() * 60); //Slider convert from hours to minutes for storage
        updated.setTheme(theme.getValue());
        updated.setExerciseReminders(exerciseReminders.isSelected());
        updated.setMealReminders(mealReminders.isSelected());
        updated.setAchievementNotifications(achievementNotifications.isSelected());
        updated.setTargetWeightKG(targetWeight);
        updated.setDailyCalorieIn(caloriesGoalInComboBox.getValue());
        updated.setDailyCalorieOut(caloriesGoalBurnedComboBox.getValue());
        updated.setWeeklyActivityReps(weeklyActivityGoalComboBox.getValue());

        // ── Persist via SettingsRepository (validates + saves to DB + SessionManager) ──
        int userId = SessionManager.getInstance().getCurrentUser().getUid();
        if (settingsRepository.save(updated, userId)) {
            showAlert(Alert.AlertType.INFORMATION, "Saved", "Settings saved successfully.");
        } else {
            showAlert(Alert.AlertType.ERROR, "Save Failed",
                    "Could not save settings. Please check your inputs.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
