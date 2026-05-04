package org.tin.oop2_capstone.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.tin.oop2_capstone.database.DatabaseConnection;
import org.tin.oop2_capstone.database.repositories.ActivityRepository;
import org.tin.oop2_capstone.database.repositories.MealRepository;
import org.tin.oop2_capstone.model.entities.User;
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
    private MealRepository mealRepository = new MealRepository();
    private ActivityRepository activityRepository = new ActivityRepository();

    ObservableList<Pane> navs;

    private boolean isSideBarCollapsed = false;

    @FXML public Button quickWorkoutButton;


    public void initialize(){
        rootAnchorPane.getStyleClass().add("light");
        anchorPaneSideBar.getStyleClass().add("light");
        anchorPaneContent.getStyleClass().add("light");

        navs = FXCollections.observableArrayList();
        navs.addAll(dashboardNav, foodLogNav, activityLogNav, settingsNav, profileNav, notificationsNav, healthNav);

        // Set user full name from session
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser != null && userFullNameLabel != null) {
            userFullNameLabel.setText(currentUser.getFullname());
        }

        navigateToView("dashboard-view", "dashboardScrollPane", dashboardNav);
        loadDashboardStats();
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
        Pane clickedBox = (Pane) event.getSource();
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

    public void navigateToView(String filename, String styleClass, Pane button){
        ScrollPane view = null;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/tin/oop2_capstone/views/" + filename + ".fxml"));
            view = fxmlLoader.load();

            AnchorPane.setBottomAnchor(view, 0.0);
            AnchorPane.setTopAnchor(view, 0.0);
            AnchorPane.setRightAnchor(view, 0.0);
            AnchorPane.setLeftAnchor(view, 0.0);
            anchorPaneContent.getChildren().setAll(view);

            // todo getUserAppearancePref() to determine if lightmode or darkmode styles, but lightmode for now

            anchorPaneContent.getStyleClass().clear();
            anchorPaneContent.getStyleClass().addAll("light", styleClass);

            anchorPaneSideBar.getStyleClass().clear();
            anchorPaneSideBar.getStyleClass().addAll("light", styleClass);


           for(Pane p : navs){
              p.getStyleClass().remove("active");
           }

           button.getStyleClass().add( "active");

        } catch (IOException e) {
            System.out.println("File not found!");
            e.printStackTrace();
        }
    }

    @FXML
    private void onQuickExerciseClicked() {
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
            e.printStackTrace();
        }
    }

    private void loadDashboardStats() {
        int userId = SessionManager.getInstance().getCurrentUser().getUid();

        // Calories
        double calories = mealRepository.getDailyCalories(userId);
        calories_today.setText(String.valueOf((int) calories));

        // Weekly workouts
        int weeklyWorkouts = activityRepository.getWeeklyWorkoutCount(userId);
        this_week_workout_count.setText(weeklyWorkouts + (weeklyWorkouts == 1 ? " Workout" : " Workouts"));

        // Total activities
        int totalActivities = activityRepository.getTotalActivitiesCount(userId);
        total_activities_count.setText(String.valueOf(totalActivities));

        // Streak
        int streak = activityRepository.getCurrentStreak(userId);
        String streakText = streak + " Days";
        if(streak == 1){
            streakText = streak + " Day";
        }
        curr_streak_1.setText(streakText);
        curr_streak_2.setText(streakText);
    }


}