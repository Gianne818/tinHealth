package org.tin.oop2_capstone.services;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import org.tin.oop2_capstone.utils.SceneSwitcher;
import org.w3c.dom.Node;

public class ExerciseMonitor {
    /**
     *  Here we do the observing for when user opens an app or make it the active window, app is not excluded, and x mins or hours of time has passed since the last user exercise prompt, then we do the exercise prompt
     *  Perhaps we may need to ask for permissions for this, such as overlay or like... process list access?
     *  Perhaps poll the os every few seconds or smth
     */

    private static ExerciseMonitor instance;
    private Timeline pollingTimeline;

    private boolean isTimerOff = true;

    // Arrays used as preferred. Add any apps here that shouldn't trigger the prompt.
    private final String[] excludedApps = {
            "java",           // When running via IntelliJ, JavaFX apps often just register as "java"
            "idea",  // Don't trigger while you are coding!
            "Zoom",
            "Spotify"
    };

    private ExerciseMonitor() {
        setupMonitor();
    }

    public static ExerciseMonitor getInstance() {
        if (instance == null) {
            instance = new ExerciseMonitor();
        }
        return instance;
    }

    private void setupMonitor() {
        // Polls every 3 seconds. JavaFX Timeline is completely safe for UI updates.
        pollingTimeline = new Timeline(new KeyFrame(Duration.seconds(3), event -> checkAndTrigger()));
        pollingTimeline.setCycleCount(Timeline.INDEFINITE);
    }

    public void startMonitoring() {
        pollingTimeline.play();
    }

    public void stopMonitoring() {
        pollingTimeline.stop();
    }

    public void setTimerOff(boolean timerOff) {
        this.isTimerOff = timerOff;
    }

    private void checkAndTrigger() {
        // Condition 1: Is the exercise prompt timer off?
        if (!isTimerOff) return;

        // Condition 2: Get the app the user is currently focused on
        String activeApp = MacAppMonitor.getActiveApp();
        if (activeApp.isEmpty()) return;

        // Condition 3: Is it an excluded app?
        boolean isExcluded = false;
        for (int i = 0; i < excludedApps.length; i++) {
            if (activeApp.equalsIgnoreCase(excludedApps[i])) {
                isExcluded = true;
                break;
            }
        }

        if (!isExcluded) {
            triggerExercisePrompt(activeApp);
        }
    }

    private void triggerExercisePrompt(String triggerApp) {
        System.out.println("User opened " + triggerApp + ". Triggering Exercise Prompt!");

        // Stop the monitor immediately so it doesn't spam the user with 50 popups
        stopMonitoring();

        // TODO: Call SceneSwitcher to open your ExercisePromptController window here.
        SceneSwitcher.openNewWindow("exercise-prompt-view") // Replace with your actual FXML filename
                .setTitle("Time to Move!")
                .setPrefDimensions(450, 550)
                .setCss("application")// Adjust to your FXML's actual size)
                .setStyleClasses(new String[]{"light", "dashboardScrollPane"})
                .setCentered(true)
                .setResizeable(false)
                .switchScene();
        // Once they finish the exercise or dismiss it, remember to call startMonitoring() again!
    }
}
