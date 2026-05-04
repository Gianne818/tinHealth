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

        // CALORIES TODAY (handles food + combos)
        String caloriesTodayQuery = """
        SELECT COALESCE(SUM(
            CASE 
                -- NORMAL FOOD
                WHEN c.type = 'food' THEN nd.calories * m.serving_size

                -- FOOD COMBO (sum of items)
                WHEN c.type = 'foodcombo' THEN (
                    SELECT COALESCE(SUM(nd2.calories * ci.quantity), 0)
                    FROM ComboItems ci
                    JOIN Consumables c2 ON ci.consumable_id = c2.consumable_id
                    LEFT JOIN NutritionalDetails nd2 ON c2.nutri_id = nd2.nutri_id
                    WHERE ci.combo_id = c.consumable_id
                ) * m.serving_size

                ELSE 0
            END
        ), 0) AS total_calories
        FROM Meals m
        JOIN Consumables c ON m.consumable_id = c.consumable_id
        LEFT JOIN NutritionalDetails nd ON c.nutri_id = nd.nutri_id
        WHERE m.user_id = ?
        AND DATE(m.log_timestamp) = CURDATE()
        """;

        String weeklyWorkoutQuery = """
        SELECT COUNT(*) AS workout_count
        FROM Activities
        WHERE user_id = ?
        AND YEARWEEK(log_timestamp, 1) = YEARWEEK(CURDATE(), 1)
        """;

        String totalActivitiesQuery = """
        SELECT COUNT(*) AS total_count
        FROM Activities
        WHERE user_id = ?
        """;

        // STREAK (dates only)
        String streakQuery = """
        SELECT DISTINCT DATE(log_timestamp) AS activity_date
        FROM Activities
        WHERE user_id = ?
        ORDER BY activity_date DESC
        """;

        try (Connection conn = DatabaseConnection.getConnection()) {

            // 1. CALORIES TODAY
            try (PreparedStatement stmt = conn.prepareStatement(caloriesTodayQuery)) {
                stmt.setInt(1, userId);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    int calories = (int) rs.getDouble("total_calories");
                    calories_today.setText(String.valueOf(calories));
                }
            }

            // 2. WEEKLY WORKOUTS
            try (PreparedStatement stmt = conn.prepareStatement(weeklyWorkoutQuery)) {
                stmt.setInt(1, userId);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    int count = rs.getInt("workout_count");
                    this_week_workout_count.setText(count + " Workouts");
                }
            }

            // 3. TOTAL ACTIVITIES
            try (PreparedStatement stmt = conn.prepareStatement(totalActivitiesQuery)) {
                stmt.setInt(1, userId);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    int total = rs.getInt("total_count");
                    total_activities_count.setText(String.valueOf(total));
                }
            }

            // 4. Streak (BY DAYS)
            try (PreparedStatement stmt = conn.prepareStatement("""
                    SELECT DISTINCT DATE(log_timestamp) AS activity_date
                    FROM Activities
                    WHERE user_id = ?
                    ORDER BY activity_date DESC
                """)) {

                stmt.setInt(1, userId);
                ResultSet rs = stmt.executeQuery();

                java.util.Set<java.time.LocalDate> uniqueDates = new java.util.LinkedHashSet<>();

                while (rs.next()) {
                    uniqueDates.add(rs.getDate("activity_date").toLocalDate());
                }

                int streak = 0;

                if (!uniqueDates.isEmpty()) {
                    java.time.LocalDate today = java.time.LocalDate.now();
                    java.time.LocalDate expected = today;

                    // REQUIRE activity today
                    if (uniqueDates.contains(today)) {

                        for (java.time.LocalDate date : uniqueDates) {
                            if (date.equals(expected)) {
                                streak++;
                                expected = expected.minusDays(1);
                            } else if (date.isBefore(expected)) {
                                // gap found → stop streak
                                break;
                            }
                        }
                    }
                }

                String text = streak + " Days";
                curr_streak_1.setText(text);
                curr_streak_2.setText(text);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}