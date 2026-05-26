package org.tin.oop2_capstone.controllers;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.tin.oop2_capstone.database.repositories.UserPrefRepository;
import org.tin.oop2_capstone.model.entities.User;
import org.tin.oop2_capstone.services.DependencyService;
import org.tin.oop2_capstone.services.ExerciseDifficultyService;
import org.tin.oop2_capstone.services.ExerciseMonitor;
import org.tin.oop2_capstone.services.SessionManager;

import java.util.List;
import java.util.Random;

public class ExercisePromptController {

    @FXML private StackPane backdropPane;
    @FXML private Label exerciseNameLabel;
    @FXML private Label minCountLabel;
    @FXML private Label unitLabel;
    @FXML private Button completeButton;
    @FXML private Label skipButton;

    private Timeline promptTimer;
    private int remainingSeconds;

    /** Called by MainController so we can remove ourselves from the overlay. */
    private static Runnable onDismiss;

    private ExerciseDifficultyService difficultyService;

    private record Exercise(String name, int duration, String unit) {}

    private UserPrefRepository userPrefRepository;
    public ExercisePromptController(){
        userPrefRepository = DependencyService.getUserPrefRepository();
    }

    private static final List<Exercise> EXERCISES = List.of(
            new Exercise("Push-Ups",      10, "minutes"),
            new Exercise("Squats",        15, "minutes"),
            new Exercise("Jumping Jacks", 20, "minutes"),
            new Exercise("Jog in Place",  60, "minutes"),
            new Exercise("Plank Hold",    30, "minutes"),
            new Exercise("Lunges",        10, "minutes"),
            new Exercise("High Knees",    30, "minutes"),
            new Exercise("Burpees",        5, "minutes")
    );

    @FXML
    public void initialize() {
        Exercise ex = EXERCISES.get(new Random().nextInt(EXERCISES.size()));
        exerciseNameLabel.setText(ex.name());
        minCountLabel.setText(String.valueOf(resolveMins(ex.duration())));
        unitLabel.setText("(" + ex.unit() + ")");

        // Fade the whole backdrop in on open
        FadeTransition fadeIn = new FadeTransition(Duration.millis(200), backdropPane);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();

        startExercisePromptTimer();


    }

    public static void setOnDismiss(Runnable callback) {
        onDismiss = callback;
    }

    @FXML
    private void onCompleteButtonClick(ActionEvent event) {
        // todo saving current activity to the database and activity log
        closeWindow(true);
    }

    public void setDifficultyService(ExerciseDifficultyService service) {
        this.difficultyService = service;
    }

    private double resolveMins(double baseMins) {
        if (difficultyService == null) return baseMins;
        return difficultyService.adjustMins(baseMins);
    }

    @FXML
    private void onSkipButtonClick(MouseEvent event){
        closeWindow(false);
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

    private void closeWindow(boolean isCompleted){
        if(isCompleted){
            //todo: save on db the activity
        }
        ExerciseMonitor.getInstance().resume();
        Stage stage = (Stage) completeButton.getScene().getWindow();

        stage.close();
    }

    private void startExercisePromptTimer() {
        int userId = SessionManager.getInstance().getCurrentUser().getUid();
        int promptFreqMinutes = userPrefRepository.getPromptFrequency(userId);

        remainingSeconds = promptFreqMinutes * 60;
        updateTimerDisplay();

        if (promptTimer != null) {
            promptTimer.stop();
        }

        promptTimer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            if (remainingSeconds > 0) {
                remainingSeconds--;
                updateTimerDisplay();

                // Check if time reaches 00:00
                if (remainingSeconds == 0) {
                    // Reset timer
                    remainingSeconds = promptFreqMinutes * 60;
                    updateTimerDisplay();
                }
            }
        }));
        promptTimer.setCycleCount(Timeline.INDEFINITE);
        promptTimer.play();
    }

    private void updateTimerDisplay() {
        int minutes = (remainingSeconds % 3600) / 60;
        int seconds = remainingSeconds % 60;

        if (minutes > 0) {
            minCountLabel.setText(String.format("%d:%02d", minutes, seconds));
            unitLabel.setText("min");
        } else {
            minCountLabel.setText(String.valueOf(seconds));
            unitLabel.setText("sec");
        }
    }


}