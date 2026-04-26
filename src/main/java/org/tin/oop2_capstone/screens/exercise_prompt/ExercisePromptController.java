package org.tin.oop2_capstone.screens.exercise_prompt;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.util.List;
import java.util.Random;

public class ExercisePromptController {

    @FXML private StackPane backdropPane;
    @FXML private Label exerciseNameLabel;
    @FXML private Label repCountLabel;
    @FXML private Label unitLabel;
    @FXML private Button completeButton;
    @FXML private Button skipButton;

    /** Called by MainController so we can remove ourselves from the overlay. */
    private Runnable onDismiss;

    // ── Exercise data ─────────────────────────────────────────────────
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

    // ── Init ──────────────────────────────────────────────────────────
    @FXML
    public void initialize() {
        Exercise ex = EXERCISES.get(new Random().nextInt(EXERCISES.size()));
        exerciseNameLabel.setText(ex.name());
        repCountLabel.setText(String.valueOf(ex.baseReps()));
        unitLabel.setText("(" + ex.unit() + ")");

        // Fade the whole backdrop in on open
        FadeTransition fadeIn = new FadeTransition(Duration.millis(200), backdropPane);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
    }

    public void setOnDismiss(Runnable callback) {
        this.onDismiss = callback;
    }

    // ── Handlers ──────────────────────────────────────────────────────
    @FXML
    private void onMarkComplete() {
        completeButton.setText("Great job!");
        completeButton.setDisable(true);
        completeButton.getStyleClass().removeAll("ep-complete-button");
        completeButton.getStyleClass().add("ep-complete-button-done");
        skipButton.setVisible(false);

        PauseTransition pause = new PauseTransition(Duration.millis(1500));
        pause.setOnFinished(e -> dismiss());
        pause.play();
    }

    @FXML
    private void onClose() {
        dismiss();
    }

    /**
     * Clicking the dark backdrop also closes the prompt,
     * but clicks on the white card itself must NOT propagate here.
     */
    @FXML
    private void onBackdropClicked() {
        dismiss();
    }

    /** Consume click so it doesn't bubble up to the backdrop handler. */
    @FXML
    private void onModalClicked(MouseEvent event) {
        event.consume();
    }

    // ── Dismiss with fade-out ─────────────────────────────────────────
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