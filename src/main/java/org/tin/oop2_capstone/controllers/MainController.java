package org.tin.oop2_capstone.controllers;

import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.input.MouseEvent;
import javafx.animation.*;
import javafx.util.Duration;

import java.io.IOException;

import org.tin.oop2_capstone.database.repositories.ActivityRepository;
import org.tin.oop2_capstone.database.repositories.MealRepository;
import org.tin.oop2_capstone.database.repositories.UserPrefRepository;
import org.tin.oop2_capstone.database.repositories.UserRepository;
import org.tin.oop2_capstone.model.entities.User;
import org.tin.oop2_capstone.model.entities.UserPreferences;
import org.tin.oop2_capstone.services.ExerciseMonitor;
import org.tin.oop2_capstone.services.SessionManager;

public class MainController {
    @FXML public SplitPane splitPaneMain;
    @FXML public AnchorPane rootAnchorPane;
    @FXML public AnchorPane anchorPaneSideBar;
    @FXML public AnchorPane anchorPaneContent;
    @FXML public ImageView imgViewCollapse;
    @FXML public Label userFullNameLabel;

    @FXML public HBox dashboardNav;
    @FXML public HBox foodLogNav;
    @FXML public HBox activityLogNav;
    @FXML public HBox settingsNav;
    @FXML public GridPane profileNav;
    @FXML public HBox notificationsNav;
    @FXML public HBox healthNav;

    @FXML public Label curr_streak_1;
    @FXML public Label curr_streak_2;
    @FXML public Label calories_today;
    @FXML public Label this_week_workout_count;
    @FXML public Label total_activities_count;

    ObservableList<Pane> navs;

    private boolean isSideBarCollapsed = false;

    @FXML public Button quickWorkoutButton;

    private static MainController instance;

    private Timeline promptTimer;
    private int remainingSeconds;

    @FXML private Label remainingTimeNumberLabel;
    @FXML private Label remainingTimeUnitLabelk;

    private ActivityRepository activityRepository = ActivityRepository.getInstance();

    private ExerciseMonitor exerciseMonitor = ExerciseMonitor.getInstance();

    private int userId;


    public void initialize(){
        System.out.println(activityRepository==null);
        instance = this;
        userId = SessionManager.getInstance().getCurrentUser().getUid();
        rootAnchorPane.getStyleClass().add("light");
        anchorPaneSideBar.getStyleClass().add("light");
        anchorPaneContent.getStyleClass().add("light");
        exerciseMonitor.start();



        navs = FXCollections.observableArrayList();
        navs.addAll(dashboardNav, foodLogNav, activityLogNav, settingsNav, profileNav, notificationsNav, healthNav);

        // Set user full name from session
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser != null && userFullNameLabel != null) {
            userFullNameLabel.setText(currentUser.getFullname());
        }

        navigateToView("dashboard-view", "dashboardScrollPane", dashboardNav);
        loadSideBoardStats();
        startExercisePromptTimer();
    }


    public void toggleSideBar(){
        double defaultPosition = 0.3;
        if(isSideBarCollapsed){
            splitPaneMain.setDividerPosition(0, defaultPosition);
            anchorPaneSideBar.setMinWidth(300);
        } else {
            anchorPaneSideBar.setMinWidth(0);
            anchorPaneSideBar.setMaxWidth(0);
            splitPaneMain.setDividerPosition(0, 0);
        }
        isSideBarCollapsed = !isSideBarCollapsed;
    }

    @FXML public void onNavElementClicked(MouseEvent event){
        Node clickedBox = (Node) event.getSource();
        char id = clickedBox.getId().charAt(0);
        switch(id){
            case 'd':
                navigateToView("dashboard-view", "dashboardScrollPane", dashboardNav);
                break;

            case'f':
                navigateToView("food-log-view", "foodLogScrollPane", foodLogNav);
                break;

            case 'a':
                navigateToView("activity-log-view", "activityLogScrollPane", activityLogNav);
                break;

            case 's':
                navigateToView("settings-view", "settingsScrollPane", settingsNav);
                break;

            case 'p':
                navigateToView("profile-view", "profileScrollPane", profileNav);
                break;

            case 'n':
                navigateToView("notifications-view", "notificationsScrollPane", notificationsNav);
                break;
            case 'h':
                navigateToView("health-view", "healthScrollPane", healthNav);
                break;
        }
    }

    public static MainController getInstance() {
        return instance;
    }

    public void navigateToView(String filename, String styleClass, Node button){
        ScrollPane view = null;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/tin/oop2_capstone/views/" + filename + ".fxml"));
            view = fxmlLoader.load();

            if (filename.equals("notifications-view")) {
                NotificationTabController notifCtrl = fxmlLoader.getController();
                User user = SessionManager.getInstance().getCurrentUser();
                UserPreferences prefs = SessionManager.getInstance().getCurrentUserPrefs();
                if (user != null && prefs != null) {
                    notifCtrl.loadNotifications(user, prefs);
                }
            }

            AnchorPane.setBottomAnchor(view, 0.0);
            AnchorPane.setTopAnchor(view, 0.0);
            AnchorPane.setRightAnchor(view, 0.0);
            AnchorPane.setLeftAnchor(view, 0.0);
            anchorPaneContent.getChildren().setAll(view);

            // Set UserData on the scene (not null now)
            Scene scene = anchorPaneContent.getScene();
            if (scene != null) {
                scene.setUserData(this);
            }

            // todo getUserAppearancePref() to determine if lightmode or darkmode styles, but lightmode for now

            anchorPaneContent.getStyleClass().clear();
            anchorPaneContent.getStyleClass().addAll("light", styleClass);

            anchorPaneSideBar.getStyleClass().clear();
            anchorPaneSideBar.getStyleClass().addAll("light", styleClass);


           for(Node p : navs){
              p.getStyleClass().remove("active");
           }

           button.getStyleClass().add( "active");

        } catch (IOException e) {
            System.out.println("File not found!");
            e.printStackTrace();
        }
    }

    @FXML
    public void onQuickExerciseClicked() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/tin/oop2_capstone/views/exercise-prompt-view.fxml"));
            StackPane overlay = fxmlLoader.load();

            overlay.getStyleClass().add("light");

            AnchorPane.setTopAnchor(overlay, 0.0);
            AnchorPane.setBottomAnchor(overlay, 0.0);
            AnchorPane.setLeftAnchor(overlay, 0.0);
            AnchorPane.setRightAnchor(overlay, 0.0);

            ExercisePromptController.setOnDismiss(() -> {
                rootAnchorPane.getChildren().remove(overlay);
                splitPaneMain.setEffect(null);
            });

            splitPaneMain.setEffect(new GaussianBlur(10));

            rootAnchorPane.getChildren().add(overlay);
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadSideBoardStats() {
        // Calories
        double calories = MealRepository.getTodayCaloriesIn(userId);
        calories_today.setText(String.valueOf((int) calories));

        // Weekly workouts
        int weeklyWorkouts = activityRepository.getWeeklyWorkoutCount();
        this_week_workout_count.setText(weeklyWorkouts + (weeklyWorkouts == 1 ? " Workout" : " Workouts"));

        // Total activities
        int totalActivities = activityRepository.getTotalActivitiesCount();
        total_activities_count.setText(String.valueOf(totalActivities));

        // Streak
        int streak = activityRepository.getCurrentStreak();
        String streakText = streak + (streak == 1 ? " Day" : " Days");
        curr_streak_1.setText(streakText);
        curr_streak_2.setText(streakText);
    }

    private void startExercisePromptTimer() {
        int userId = SessionManager.getInstance().getCurrentUser().getUid();
        int promptFreqMinutes = UserPrefRepository.getInstance().getPromptFrequency(userId);

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
                    showExercisePrompt();
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
        int hours = remainingSeconds / 3600;
        int minutes = (remainingSeconds % 3600) / 60;
        int seconds = remainingSeconds % 60;

        if (hours > 0) {
            remainingTimeNumberLabel.setText(String.format("%d:%02d:%02d", hours, minutes, seconds));
            remainingTimeUnitLabelk.setText("hr");
        } else if (minutes > 0) {
            remainingTimeNumberLabel.setText(String.format("%d:%02d", minutes, seconds));
            remainingTimeUnitLabelk.setText("min");
        } else {
            remainingTimeNumberLabel.setText(String.valueOf(seconds));
            remainingTimeUnitLabelk.setText("sec");
        }
    }

    private void showExercisePrompt() {
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/tin/oop2_capstone/views/exercise-prompt-view.fxml"));
                StackPane overlay = loader.load();
                overlay.getStyleClass().add("light");

                AnchorPane.setTopAnchor(overlay, 0.0);
                AnchorPane.setBottomAnchor(overlay, 0.0);
                AnchorPane.setLeftAnchor(overlay, 0.0);
                AnchorPane.setRightAnchor(overlay, 0.0);

                ExercisePromptController.setOnDismiss(() -> {
                    rootAnchorPane.getChildren().remove(overlay);
                    splitPaneMain.setEffect(null);
                });

                splitPaneMain.setEffect(new GaussianBlur(10));
                rootAnchorPane.getChildren().add(overlay);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}