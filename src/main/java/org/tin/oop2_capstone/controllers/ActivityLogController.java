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
import org.tin.oop2_capstone.database.repositories.ActivityRepository;
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
    private List<Activity> activities;
    private ObservableList<GridPane> activityGridPanes;

    private ActivityRepository activityRepository = ActivityRepository.getInstance();

    public void initialize(){
        activities = FXCollections.observableArrayList();
        activityGridPanes = FXCollections.observableArrayList();
        setActivityLog();
    }

    private void setActivityLog(){
        // sample values;
        activities = activityRepository.getUserActivities();

        for(Activity a : activities){
            try{
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/tin/oop2_capstone/views/log-card.fxml"));
                GridPane root = fxmlLoader.load();
                root.getStylesheets().add(getClass().getResource("/org/tin/oop2_capstone/styles/application.css").toExternalForm());
                root.getStyleClass().addAll("light", "activityLogScrollPane");

                LogCardController logCardController = fxmlLoader.getController();
                logCardController.setData(a.getActivityType().getName(), TimeFormatter.formatTo12Hour(a.getLogDateTime().toLocalTime()), a.getQuantity(), a.getUnit(), a.getCalories(), false, true);
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

        //TODO: refresh the listview if it queries from the database to load new added activity(?)
    }

    public void onButtonCancelClicked(ActionEvent actionEvent) {
        gridPaneAddEntry.setVisible(!addEntryisVisible);
        gridPaneAddEntry.setManaged(!addEntryisVisible);
        addEntryisVisible = !addEntryisVisible;
    }

}
