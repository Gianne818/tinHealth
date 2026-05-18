package org.tin.oop2_capstone.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import org.tin.oop2_capstone.model.NotificationService;
import org.tin.oop2_capstone.model.NotificationService.Notification;
import org.tin.oop2_capstone.model.entities.NutritionDetails;
import org.tin.oop2_capstone.model.entities.User;
import org.tin.oop2_capstone.model.entities.UserPreferences;
import org.tin.oop2_capstone.database.RetrieveData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NotificationTabController {

    @FXML private VBox notificationsContainer;
    @FXML private Label subtitleLabel;
    @FXML private Button markAllReadButton;
    @FXML SVGPath notificationsIcon;

    private record TrackedNotification(Notification notif, boolean read) {}

    private List<TrackedNotification> notifications = new ArrayList<>();
    private User user;
    private UserPreferences prefs;

    // Call this after login to inject user data and load notifications
    public void loadNotifications(User user, UserPreferences prefs) {
        this.user = user;
        this.prefs = prefs;

        NutritionDetails todayNutrition = fetchTodayNutrition(user.getUid());
        double caloriesOut = RetrieveData.fetchUserTodayCaloriesOut(user.getUid());
        int weeklyWorkouts = RetrieveData.fetchUserWeeklyWorkout(user.getUid());

        List<Notification> generated = NotificationService.generateNotifications(
                user, prefs, todayNutrition, caloriesOut, weeklyWorkouts
        );

        notifications.clear();
        for (Notification n : generated) {
            notifications.add(new TrackedNotification(n, false));
        }

        renderNotifications();
    }

    @FXML
    public void initialize() {
        // Render empty state until loadNotifications() is called
        renderNotifications();
    }

    @FXML
    private void onMarkAllRead() {
        notifications.replaceAll(t -> new TrackedNotification(t.notif(), true));
        renderNotifications();
    }

    private void renderNotifications() {
        notificationsContainer.getChildren().clear();

        long unread = notifications.stream().filter(t -> !t.read()).count();
        subtitleLabel.setText(unread > 0
                ? "You have " + unread + " unread notification" + (unread > 1 ? "s" : "")
                : "All caught up!");

        markAllReadButton.setVisible(unread > 0);
        markAllReadButton.setManaged(unread > 0);

        if (notifications.isEmpty()) {
            Label empty = new Label("No notifications — you're all caught up!");
            empty.getStyleClass().add("lightText");
            notificationsContainer.getChildren().add(empty);
            return;
        }

        for (int i = 0; i < notifications.size(); i++) {
            final int index = i;
            TrackedNotification tracked = notifications.get(i);
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(
                        "/org/tin/oop2_capstone/views/notification-card-view.fxml"));
                javafx.scene.layout.GridPane card = loader.load();
                NotificationCardController ctrl = loader.getController();

                ctrl.setTitle(tracked.notif().title());
                ctrl.setMessage(tracked.notif().message());
                ctrl.setPassedTime("Just now");
                ctrl.setType(tracked.notif().type());
                // ctrl.setIcon(iconPath(tracked.notif().type()));
                ctrl.setRead(tracked.read());


                ctrl.setOnDeleteAction(() -> {
                    notifications.remove(index);
                    renderNotifications();
                });

                // Mark as read on click
                card.setOnMouseClicked(e -> {
                    notifications.set(index, new TrackedNotification(tracked.notif(), true));
                    renderNotifications();
                });

                notificationsContainer.getChildren().add(card);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String iconPath(String type) {
        return switch (type) {
            case "achievement" -> "/org/tin/oop2_capstone/assets/icons/trophy.png";
            case "reminder"    -> "/org/tin/oop2_capstone/assets/icons/bell.png";
            case "goal"        -> "/org/tin/oop2_capstone/assets/icons/target.png";
            case "activity"    -> "/org/tin/oop2_capstone/assets/icons/activity.png";
            default            -> "/org/tin/oop2_capstone/assets/icons/bell.png";
        };
    }

    // Fetch today's nutrition totals for the user
    private NutritionDetails fetchTodayNutrition(int userId) {
        List<org.tin.oop2_capstone.model.entities.Meal> meals =
                RetrieveData.fetchUserMealsToday(userId);

        double cal = 0, protein = 0, fat = 0, carbs = 0,
                cholesterol = 0, sodium = 0, sugar = 0, fiber = 0;

        for (var meal : meals) {
            NutritionDetails nd = meal.getNutritionDetails(); // ✅ direct from Meal
            if (nd == null) continue;
            double s = meal.getQuantity(); // serving size/quantity
            cal         += nd.getCalories()     * s;
            protein     += nd.getProtein()      * s;
            fat         += nd.getFat()          * s;
            carbs       += nd.getCarbs()        * s;
            cholesterol += nd.getCholesterol()  * s;
            sodium      += nd.getSodium()       * s;
            sugar       += nd.getSugar()        * s;
            fiber       += nd.getFiber()        * s;
        }

        return new NutritionDetails(cal, protein, fat, carbs, cholesterol, sodium, sugar, fiber);
    }
}