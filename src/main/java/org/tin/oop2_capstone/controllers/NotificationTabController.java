package org.tin.oop2_capstone.controllers;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.SVGPath;

import java.util.ArrayList;
import java.util.List;

public class NotificationTabController {
    // todo: everything. this is not functional, more for display lng. we need to make it into a listview instead of scrollpane, then add like.. notifications-view as a separate view per card, instead of this lengthy function
    @FXML private VBox notificationsContainer;
    @FXML private Label subtitleLabel;
    @FXML private Button markAllReadButton;

    private record Notification(int id, String type, String title, String message, String time, boolean read) {}

    private List<Notification> notifications = new ArrayList<>(List.of(
            new Notification(1, "achievement", "7-Day Streak!",        "Congratulations! You've maintained your daily activity streak for a full week.", "2 hours ago",  false),
            new Notification(2, "reminder",    "Time for Exercise",     "It's time for your scheduled workout. Don't break your streak!",                  "3 hours ago",  false),
            new Notification(3, "goal",        "Calorie Goal Reached",  "You've reached your daily calorie goal of 2000 calories.",                        "5 hours ago",  true),
            new Notification(4, "activity",    "New Activity Logged",   "Running - 30 minutes logged. Great job!",                                         "1 day ago",    true),
            new Notification(5, "achievement", "Weekly Goal Completed", "You completed 5 workouts this week. Keep up the excellent work!",                 "2 days ago",   true),
            new Notification(6, "reminder",    "Log Your Meals",        "Don't forget to log your dinner to track your daily nutrition.",                  "3 days ago",   true)
    ));

    @FXML
    public void initialize() {
        renderNotifications();
    }


    @FXML SVGPath notificationsIcon;
    @FXML
    private void onMarkAllRead() {
        notifications.replaceAll(n -> new Notification(n.id(), n.type(), n.title(), n.message(), n.time(), true));
        renderNotifications();
    }

    private void renderNotifications() {
        notificationsContainer.getChildren().clear();

        long unread = notifications.stream().filter(n -> !n.read()).count();

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

        for (Notification n : new ArrayList<>(notifications)) {
            notificationsContainer.getChildren().add(buildCard(n));
        }
    }

    private HBox buildCard(Notification n) {
        // Icon circle with image
        String iconPath = switch (n.type()) {
            case "achievement" -> "/org/tin/oop2_capstone/assets/icons/trophy.png";
            case "reminder"    -> "/org/tin/oop2_capstone/assets/icons/bell.png";
            case "goal"        -> "/org/tin/oop2_capstone/assets/icons/target.png";
            case "activity"    -> "/org/tin/oop2_capstone/assets/icons/activity.png";
            default            -> "/org/tin/oop2_capstone/assets/icons/bell.png";
        };

        ImageView iconImage = new ImageView(
                new javafx.scene.image.Image(getClass().getResourceAsStream(iconPath))
        );
        iconImage.setFitWidth(24);
        iconImage.setFitHeight(24);
        iconImage.setPreserveRatio(true);

        StackPane iconCircle = new StackPane(iconImage);
        iconCircle.setPrefSize(44, 44);
        iconCircle.setMinSize(44, 44);
        iconCircle.setMaxSize(44, 44);
        iconCircle.setStyle("-fx-background-color: " + iconBgColor(n.type()) + "; -fx-background-radius: 10px;");

        // Text block
        VBox textBlock = new VBox(4);
        HBox.setHgrow(textBlock, Priority.ALWAYS);

        // Title row
        HBox titleRow = new HBox(6);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        Label titleLabel = new Label(n.title());
        titleLabel.getStyleClass().add("boldText");
        titleLabel.setStyle("-fx-font-size: 14px;");

        titleRow.getChildren().add(titleLabel);

        // Unread dot
        if (!n.read()) {
            Circle unreadDot = new Circle(4);
            unreadDot.setFill(Color.web("#3B82F6"));
            titleRow.getChildren().add(unreadDot);
        }

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        titleRow.getChildren().add(spacer);

        // Delete button
        Button deleteBtn = new Button("✕");
        deleteBtn.getStyleClass().add("notif-delete-button");
        deleteBtn.setOnAction(e -> {
            notifications.removeIf(notif -> notif.id() == n.id());
            renderNotifications();
        });
        titleRow.getChildren().add(deleteBtn);

        Label messageLabel = new Label(n.message());
        messageLabel.getStyleClass().add("lightText");
        messageLabel.setWrapText(true);
        messageLabel.setStyle("-fx-font-size: 13px;");

        Label timeLabel = new Label(n.time());
        timeLabel.getStyleClass().add("lightText");
        timeLabel.setStyle("-fx-font-size: 11px;");

        textBlock.getChildren().addAll(titleRow, messageLabel, timeLabel);

        // Card
        HBox card = new HBox(14);
        card.setAlignment(Pos.CENTER_LEFT);
        card.getStyleClass().add("notif-card");
        card.setPadding(new Insets(14));
        card.setOpacity(n.read() ? 0.65 : 1.0);
        card.getChildren().addAll(iconCircle, textBlock);

        // Mark as read on click
        if (!n.read()) {
            card.setOnMouseClicked(e -> {
                notifications.replaceAll(notif ->
                        notif.id() == n.id()
                                ? new Notification(notif.id(), notif.type(), notif.title(), notif.message(), notif.time(), true)
                                : notif
                );
                renderNotifications();
            });
            card.setStyle(card.getStyle() + "; -fx-cursor: hand;");
        }

        return card;
    }

    private String iconColor(String type) {
        return switch (type) {
            case "achievement" -> "#F59E0B";
            case "goal"        -> "#10B981";
            case "activity"    -> "#EF4444";
            default            -> "#3B82F6"; // reminder
        };
    }

    private String iconBgColor(String type) {
        return switch (type) {
            case "achievement" -> "#FEF3C7";
            case "goal"        -> "#D1FAE5";
            case "activity"    -> "#FEE2E2";
            default            -> "#DBEAFE";
        };
    }
}