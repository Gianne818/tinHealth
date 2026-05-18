package org.tin.oop2_capstone.controllers;

import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
    @FXML private  SplitPane splitPaneMain;
    @FXML private  AnchorPane rootAnchorPane;
    @FXML private  AnchorPane anchorPaneSideBar;
    @FXML private  AnchorPane anchorPaneContent;
    @FXML private  ImageView imgViewCollapse;
    @FXML private  Label userFullNameLabel;

    @FXML private  HBox dashboardNav;
    @FXML private  HBox foodLogNav;
    @FXML private  HBox activityLogNav;
    @FXML private  HBox settingsNav;
    @FXML private  GridPane profileNav;
    @FXML private  HBox notificationsNav;
    @FXML private  HBox healthNav;

    @FXML private  Label curr_streak_1;
    @FXML private  Label curr_streak_2;
    @FXML private  Label calories_today;
    @FXML private  Label this_week_workout_count;
    @FXML private  Label total_activities_count;
    @FXML private StackPane hamburgerButton;
    @FXML private Label viewProfileLabel;
    @FXML private StackPane imageStackPane;
    ObservableList<Pane> navs;

    private boolean isSideBarCollapsed = false;

    @FXML private  Button quickWorkoutButton;

    private static MainController instance;

    private Timeline promptTimer;
    private int remainingSeconds;

    @FXML private Label remainingTimeNumberLabel;
    @FXML private Label remainingTimeUnitLabelk;
    @FXML private Label sidebarNavLabel1;
    @FXML private Label sidebarNavLabel2;
    @FXML private Label sidebarNavLabel3;
    @FXML private Label sidebarNavLabel4;
    @FXML private Label sidebarNavLabel5;
    @FXML private VBox sideBarVbox1;
    @FXML private VBox sideBarVbox2;
    @FXML private GridPane streakGridPane;
    @FXML private Separator separator1;
    @FXML private Separator separator2;
    @FXML private Label quickStatsLabel;
    @FXML private GridPane quickStatsGridPane;
    @FXML private Label promptLabel1;

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
        hamburgerButton.setOnMouseClicked( e ->{
            toggleSideBar();
        });
    }

    private void toggleSideBar(){
        double defaultPosition = 0.3;
        if(isSideBarCollapsed){
            splitPaneMain.setDividerPosition(75, defaultPosition);
            anchorPaneSideBar.setMinWidth(300);
            toggleElement(sidebarNavLabel1, true);
            toggleElement(sidebarNavLabel2, true);
            toggleElement(sidebarNavLabel3, true);
            toggleElement(sidebarNavLabel4, true);
            toggleElement(sidebarNavLabel5, true);
            toggleElement(userFullNameLabel, true);
            toggleElement(viewProfileLabel, true);
            toggleElement(streakGridPane, true);
            toggleElement(separator1, true);
            toggleElement(quickStatsLabel, true);
            toggleElement(quickStatsGridPane, true);
            toggleElement(promptLabel1, true);
            remainingTimeUnitLabelk.setVisible(true);
            remainingTimeNumberLabel.setVisible(true);
            quickWorkoutButton.setText("Quick Exercise");
            profileNav.setPadding(new Insets(0, 0, 0, 0));
        }
        else {
            anchorPaneSideBar.setMinWidth(105);
            anchorPaneSideBar.setMaxWidth(105);
            splitPaneMain.setDividerPosition(0, 0);
            toggleElement(sidebarNavLabel1, false);
            toggleElement(sidebarNavLabel2, false);
            toggleElement(sidebarNavLabel3, false);
            toggleElement(sidebarNavLabel4, false);
            toggleElement(sidebarNavLabel5, false);
            toggleElement(userFullNameLabel, false);
            toggleElement(viewProfileLabel, false);
            toggleElement(streakGridPane, false);
            toggleElement(separator1, false);
            toggleElement(quickStatsLabel, false);
            toggleElement(quickStatsGridPane, false);
            toggleElement(promptLabel1, false);
            remainingTimeUnitLabelk.setVisible(false);
            remainingTimeNumberLabel.setVisible(false);
            quickWorkoutButton.setText("\u29BF");
            profileNav.setPadding(new Insets(0, 0, 0, 7));
        }
        isSideBarCollapsed = !isSideBarCollapsed;
    }

    private void toggleElement(Node e, boolean show){
        e.setManaged(show);
        e.setVisible(show);
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