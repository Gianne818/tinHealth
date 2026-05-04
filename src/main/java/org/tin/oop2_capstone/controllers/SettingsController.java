package org.tin.oop2_capstone.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.tin.oop2_capstone.database.repositories.SettingsRepository;
import org.tin.oop2_capstone.model.entities.UserPreferences;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.IntStream;

public class SettingsController implements Initializable {
    @FXML public ScrollPane settingsScrollPane;
    @FXML public ToggleButton exercisePrompts;
    @FXML public ChoiceBox<Integer> exerciseIntensity;
    @FXML public Slider promptFrequency;
    @FXML public ChoiceBox<String> theme;
    @FXML public ChoiceBox<String> language;
    @FXML public ToggleButton exerciseReminders;
    @FXML public ToggleButton mealReminders;
    @FXML public ToggleButton achievementNotifications;
    @FXML public ChoiceBox<Integer> dailyCalorieGoal;
    @FXML public ChoiceBox<Integer> weeklyActivityGoal;
    @FXML public Button saveSettings;

    private SettingsRepository settingsRepository;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        settingsRepository = new SettingsRepository();
        initializeControls();
//        loadUserPreferences();
    }

    private void initializeControls() {
        exerciseIntensity.getItems().addAll(IntStream.rangeClosed(1, 10).boxed().toList());
        theme.getItems().addAll("Light", "Dark");
        language.getItems().addAll("English", "Spanish", "French");
        dailyCalorieGoal.getItems().addAll(IntStream.iterate(1500, n -> n <= 3500, n -> n + 100).boxed().toList());
        weeklyActivityGoal.getItems().addAll(IntStream.rangeClosed(0, 7).boxed().toList());

        promptFrequency.setMin(1);
        promptFrequency.setMax(24);
        promptFrequency.setValue(4);
    }

    private void loadUserPreferences() {
        UserPreferences preferences = settingsRepository.load();
        if (preferences != null) {
            exercisePrompts.setSelected(preferences.isEnableExercisePrompts());
            exerciseIntensity.setValue(preferences.getExerciseIntensity());
            promptFrequency.setValue(preferences.getPromptFrequencyHours());
            theme.setValue(preferences.getTheme().substring(0, 1).toUpperCase() + preferences.getTheme().substring(1));
            dailyCalorieGoal.setValue((int) preferences.getDailyCalorieGoal());
            weeklyActivityGoal.setValue(preferences.getWeeklyActivityReps());
        }

    }
     /** just commented out for other testing purposes on other features */
    @FXML
    public void onSaveButtonClicked(ActionEvent event) {
//        try {
//            boolean enablePrompts = exercisePromptsToggle.isSelected();
//            int exerciseIntensityValue = exerciseIntensity.getValue();
//            int promptFrequencyValue = (int) promptFrequency.getValue();
//            String themeValue = theme.getValue().toLowerCase();
//            double dailyCalorieGoalValue = dailyCalorieGoal.getValue();
//            int weeklyActivityRepsValue = weeklyActivityGoal.getValue();
//
//            UserPreferences preferences = new UserPreferences(
//                enablePrompts, exerciseIntensityValue, promptFrequencyValue, themeValue, dailyCalorieGoalValue, weeklyActivityRepsValue
//            );
//
//            if (settingsRepository.save(preferences)) {
//                statusLabel.setText("Settings saved successfully!");
//                statusLabel.setStyle("-fx-text-fill: green;");
//            } else {
//                statusLabel.setText("Invalid settings. Please check your inputs.");
//                statusLabel.setStyle("-fx-text-fill: red;");
//            }
//        } catch (NumberFormatException e) {
//            statusLabel.setText("Please select valid values for all fields.");
//            statusLabel.setStyle("-fx-text-fill: red;");
//        } catch (NullPointerException e) {
//            statusLabel.setText("Please select values for all dropdown menus and sliders.");
//            statusLabel.setStyle("-fx-text-fill: red;");
//        }
    }
}
