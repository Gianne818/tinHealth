package org.tin.oop2_capstone.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import org.tin.oop2_capstone.database.RetrieveData;
import org.tin.oop2_capstone.database.repositories.ActivityRepository;
import org.tin.oop2_capstone.model.entities.Activity;
import org.tin.oop2_capstone.model.entities.ActivityLog;
import org.tin.oop2_capstone.model.entities.ActivityType;
import org.tin.oop2_capstone.utils.InputManager;
import org.tin.oop2_capstone.utils.TimeFormatter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.function.Predicate;

public class ActivityLogController {
    @FXML private Button buttonAddEntry;
    @FXML private Button buttonCancel;
    @FXML private ComboBox<String> activityTypeComboBox;
    @FXML private TextField textfieldDuration;
    @FXML private TextField textfieldCaloriesBurned;
    @FXML private GridPane gridPaneAddEntry;
    @FXML private Button buttonAddActivity;
    @FXML private ScrollPane activityLogScrollPane;
    @FXML private ListView <GridPane> activityLogListView;

    private ObservableList<ActivityType> activityTypeList;
    private ObservableList<String> activityTypeNames;
    private ActivityLog activityLog;
    private List<Activity> activities;
    private ObservableList<GridPane> activityGridPanes;
    private FilteredList<String> filteredList;

    private ActivityRepository activityRepository = ActivityRepository.getInstance();

    public void initialize(){
        activities = FXCollections.observableArrayList();
        activityGridPanes = FXCollections.observableArrayList();
        activityTypeList = FXCollections.observableArrayList();
        activityTypeNames = FXCollections.observableArrayList();

        setActivityLog();
        setActivityTypes();
        initActivityTypeComboBox();

        filteredList = new FilteredList<>(activityTypeNames);
        activityTypeComboBox.setItems(filteredList);

        InputManager.acceptOnlyDouble(textfieldDuration);
        InputManager.acceptOnlyDouble(textfieldCaloriesBurned);
    }

    private void initActivityTypeComboBox(){
        activityTypeComboBox.getEditor().textProperty().addListener((obs, oldVal, newVal) -> {
           String selected = activityTypeComboBox.getSelectionModel().getSelectedItem();

            /* EXCERPT FROM FilteredList.java
              The predicate that will match the elements that will be in this FilteredList.
              Elements not matching the predicate will be filtered-out.
              Null predicate means "always true" predicate, all elements will be matched.
             */

           if(selected == null || !selected.equals(newVal)){
               filteredList.setPredicate(new Predicate<>() {
                   @Override
                   public boolean test(String s) {
                       if (newVal == null) {
                           return true; // show everything if empty
                       }

                       return s.toLowerCase().contains(newVal.toLowerCase());
                   }
               });
           }

           if(newVal!=null && newVal.equals(selected)){
               if(!filteredList.isEmpty()){
                   activityTypeComboBox.getSelectionModel().select(0);
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            activityTypeComboBox.getEditor().setText(newVal);
                            activityTypeComboBox.getEditor().positionCaret(newVal.length());
                        }
                    });
               }
           }

           if(!activityTypeComboBox.isShowing()){
               activityTypeComboBox.show();
           }

        });
    }



    private  void setActivityTypes(){
       activityTypeList.clear();
       activityTypeNames.clear();
        activityTypeList = activityRepository.getActivityTypes();
        for(ActivityType a : activityTypeList){
            activityTypeNames.add(a.getName());
        }
//        activityTypeComboBox.setItems(activityTypeNames);
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
