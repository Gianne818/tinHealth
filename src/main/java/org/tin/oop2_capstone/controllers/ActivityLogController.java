package org.tin.oop2_capstone.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import org.tin.oop2_capstone.database.RetrieveData;
import org.tin.oop2_capstone.model.entities.Activity;
import org.tin.oop2_capstone.model.entities.ActivityLog;
import org.tin.oop2_capstone.model.entities.ActivityType;
import org.tin.oop2_capstone.utils.TimeFormatter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class ActivityLogController {
    @FXML Button buttonAddEntry;
    @FXML Button buttonCancel;
    @FXML TextField textfieldExercise;
    @FXML TextField textfieldDuration;
    @FXML TextField textfieldCaloriesBurned;
    @FXML GridPane gridPaneAddEntry;
    @FXML Button buttonAddActivity;
    @FXML ScrollPane activityLogScrollPane;
    @FXML ListView <GridPane> activityLogListView;

    // todo: get actual activityLog via logRepository
    private ActivityLog activityLog;
    private ObservableList<Activity> activities;
    private ObservableList<GridPane> activityGridPanes;

    public void initialize(){
        activities = FXCollections.observableArrayList();
        activityGridPanes = FXCollections.observableArrayList();
        setActivityLog();
    }

    private void setActivityLog(){
        // sample values;
        activityLog = new ActivityLog();
        List<ActivityType> activityTypes = RetrieveData.fetchActivityTypes();
        activityLog.addActivity(new Activity(activityTypes.get(1), LocalDateTime.now(), "minutes", 30, 200));
        activityLog.addActivity(new Activity(activityTypes.get(2), LocalDateTime.now(), "minutes", 45, 245));
        activityLog.addActivity(new Activity(activityTypes.get(3), LocalDateTime.now(), "minutes", 50, 180));
        activityLog.addActivity(new Activity(activityTypes.get(4), LocalDateTime.now(), "minutes", 30, 200));
        activityLog.addActivity(new Activity(activityTypes.get(0), LocalDateTime.now(), "minutes", 60, 245));
        activityLog.addActivity(new Activity(activityTypes.get(1), LocalDateTime.now(), "minutes", 30, 1));
        activityLog.addActivity(new Activity(activityTypes.get(2), LocalDateTime.now(), "minutes", 30, 200));
        activityLog.addActivity(new Activity(activityTypes.get(3), LocalDateTime.now(), "minutes", 50, 245));
        activityLog.addActivity(new Activity(activityTypes.get(4), LocalDateTime.now(), "minutes", 60, 270));

        activities.addAll(activityLog.getActivities());

        for(Activity a : activities){
            try{
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/tin/oop2_capstone/views/log-card.fxml"));
                GridPane root = fxmlLoader.load();
                root.getStylesheets().add(getClass().getResource("/org/tin/oop2_capstone/styles/application.css").toExternalForm());
                root.getStyleClass().addAll("light", "activityLogScrollPane");

                LogCardController logCardController = fxmlLoader.getController();
                logCardController.setData(a.getActivityType().getName(), TimeFormatter.formatTo12Hour(a.getLogDateTime().toLocalTime()), a.getQuantity(), a.getUnit(), a.getCalories());
                activityGridPanes.add(root);
            } catch (IOException e){
                System.out.println("OH NNOI");
                e.printStackTrace();
            }
        }

        activityLogListView.setItems(activityGridPanes);
    }

    private boolean addEntryisVisible = false;
    public void onButtonAddActivityClicked(ActionEvent actionEvent) {
        gridPaneAddEntry.setVisible(!addEntryisVisible);
        gridPaneAddEntry.setManaged(!addEntryisVisible);
        addEntryisVisible = !addEntryisVisible;
    }

    public void onButtonAddEntryClicked(ActionEvent actionEvent) {
        //TODO: Convert the textfield inputs into strings and add them into the database(?)

        //remove the prompt box
        gridPaneAddEntry.setVisible(false);
        addEntryisVisible = !addEntryisVisible;

        //TODO: refresh the listview if it queries from the database to load new added activity(?)
    }

    public void onButtonCancelClicked(ActionEvent actionEvent) {
        gridPaneAddEntry.setVisible(false);
        addEntryisVisible = !addEntryisVisible;
    }

}
