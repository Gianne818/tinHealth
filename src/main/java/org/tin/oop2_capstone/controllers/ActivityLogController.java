package org.tin.oop2_capstone.controllers;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import org.tin.oop2_capstone.model.entities.Activity;
import org.tin.oop2_capstone.model.entities.ActivityLog;

import java.time.LocalTime;
import java.util.List;

public class ActivityLogController {
    @FXML Button buttonAddActivity;
    @FXML ScrollPane activityLogScrollPane;
    @FXML ListView <GridPane> activityLogListView;

    // todo: get actual activityLog via logRepository
    private ActivityLog activityLog;

    public void initialize(){

    }

    private void setActivityLog(){
        // sample values;
        activityLog.addActivity(new Activity("Strength Training", LocalTime.now(), "minutes", 30, 200));
        activityLog.addActivity(new Activity("Jogging", LocalTime.now(), "km", 1.3, 245));
        activityLog.addActivity(new Activity("Yoga", LocalTime.now(), "minutes", 50, 180));

        for(Activity a : activityLog.getActivities()){
//            activityLogListView.
        }
    }



    public void onButtonAddActivityClicked(ActionEvent actionEvent) {
    }
}
