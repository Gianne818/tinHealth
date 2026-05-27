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
    private double remainingMinutes;
    private int remainingSeconds;

    /** Called by MainController so we can remove ourselves from the overlay. */
    private static Runnable onDismiss;

    private ExerciseDifficultyService difficultyService;

    private record Exercise(String name, double duration, String unit) {}

    private UserPrefRepository userPrefRepository;
    public ExercisePromptController(){
        userPrefRepository = DependencyService.getUserPrefRepository();
    }

    private static final List<Exercise> EXERCISES = List.of(
            new Exercise("Push-Ups",      0.25, "minutes"),
            new Exercise("Squats",        0.333333, "minutes"),
            new Exercise("Jumping Jacks", 0.333333, "minutes"),
            new Exercise("Jog in Place",  0.5, "minutes"),
            new Exercise("Plank Hold",    0.166667, "minutes"),
            new Exercise("Lunges",        0.333333, "minutes"),
            new Exercise("High Knees",    0.333333, "minutes"),
            new Exercise("Burpees",        0.25, "minutes")
    );

    @FXML
    public void initialize() {
        difficultyService = new ExerciseDifficultyService();
        Exercise ex = EXERCISES.get(new Random().nextInt(EXERCISES.size()));
        exerciseNameLabel.setText(ex.name());
        minCountLabel.setText(String.valueOf(resolveMins(ex.duration())));
        remainingMinutes = resolveMins(ex.duration());
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
        closeWindow(true);
    }

    public void setDifficultyService(ExerciseDifficultyService service) {
        this.difficultyService = service;
    }

    private double resolveMins(double baseMins) {
        int userId = SessionManager.getInstance().getCurrentUser().getUid();
        return difficultyService.adjustMins(baseMins, userPrefRepository.getExerciseIntensity(userId));
    }

    @FXML
    private void onSkipButtonClick(MouseEvent event){
        closeWindow(false);
    }

    private void dismiss() {
        if (promptTimer != null) promptTimer.stop();

        if (backdropPane != null) {
            FadeTransition fadeOut = new FadeTransition(Duration.millis(180), backdropPane);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setOnFinished(e -> {
                if (onDismiss != null) onDismiss.run();
            });
            fadeOut.play();
        } else if (onDismiss != null) {
            onDismiss.run();
        }
    }

    private void closeWindow(boolean isCompleted) {
        if (isCompleted) {
            //todo: save on db the activity
        }

        ExerciseMonitor.getInstance().resume();
        if (promptTimer != null) promptTimer.stop();

        if (completeButton != null && completeButton.getScene() != null && completeButton.getScene().getWindow() instanceof Stage stage) {
            if (completeButton.getScene().getRoot() == backdropPane) {
                dismiss();
                return;
            }
            else {
                stage.hide();
            }
        }
        dismiss();
    }

    /* fixed timer */
    private void startExercisePromptTimer() {
        remainingSeconds = (int) Math.round(remainingMinutes * 60);
        updateTimerDisplay();

        if (promptTimer != null) {
            promptTimer.stop();
        }

        promptTimer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            if (remainingSeconds > 0) {
                remainingSeconds--;
                updateTimerDisplay();

                if(remainingSeconds == 0){
                    promptTimer.stop();
                    closeWindow(true);
                }
            }

        }));
        promptTimer.setCycleCount(Timeline.INDEFINITE);
        promptTimer.play();
    }

    /* fixed updateTimer */
    private void updateTimerDisplay() {
        int minutes = remainingSeconds / 60;
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