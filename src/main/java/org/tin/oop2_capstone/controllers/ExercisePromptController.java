package org.tin.oop2_capstone.controllers;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import org.tin.oop2_capstone.services.ExerciseDifficultyService;

import java.util.List;
import java.util.Random;

public class ExercisePromptController {

    @FXML private StackPane backdropPane;
    @FXML private Label exerciseNameLabel;
    @FXML private Label repCountLabel;
    @FXML private Label unitLabel;
    @FXML private Button completeButton;
    @FXML private Label skipButton;

    /** Called by MainController so we can remove ourselves from the overlay. */
    private static Runnable onDismiss;

    private ExerciseDifficultyService difficultyService;

    private record Exercise(String name, int baseReps, String unit) {}

    private static final List<Exercise> EXERCISES = List.of(
            new Exercise("Push-Ups",      10, "reps"),
            new Exercise("Squats",        15, "reps"),
            new Exercise("Jumping Jacks", 20, "reps"),
            new Exercise("Jog in Place",  60, "seconds"),
            new Exercise("Plank Hold",    30, "seconds"),
            new Exercise("Lunges",        10, "reps per leg"),
            new Exercise("High Knees",    30, "seconds"),
            new Exercise("Burpees",        5, "reps")
    );

    @FXML
    public void initialize() {
        Exercise ex = EXERCISES.get(new Random().nextInt(EXERCISES.size()));
        exerciseNameLabel.setText(ex.name());
        repCountLabel.setText(String.valueOf(resolveReps(ex.baseReps())));
        unitLabel.setText("(" + ex.unit() + ")");

        // Fade the whole backdrop in on open
        FadeTransition fadeIn = new FadeTransition(Duration.millis(200), backdropPane);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
    }

    public static void setOnDismiss(Runnable callback) {
        onDismiss = callback;
    }

    @FXML
    private void onCompleteButtonClick(ActionEvent event) {
        // todo saving current activity to the database and activity log
        dismiss();
    }

    public void setDifficultyService(ExerciseDifficultyService service) {
        this.difficultyService = service;
    }

    private int resolveReps(int baseReps) {
        if (difficultyService == null) return baseReps;
        return difficultyService.adjustReps(baseReps);
    }

    @FXML
    private void onSkipLabelClick(){
        dismiss();
    }

    private void dismiss() {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(180), backdropPane);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> {
            if (onDismiss != null) onDismiss.run();
        });
        fadeOut.play();
    }


}