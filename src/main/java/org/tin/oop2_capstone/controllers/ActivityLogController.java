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
import org.tin.oop2_capstone.utils.TimeFormatter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.function.Predicate;

public class ActivityLogController {
    @FXML Button buttonAddEntry;
    @FXML Button buttonCancel;
    @FXML ComboBox<String> activityTypeComboBox;
    @FXML TextField textfieldDuration;
    @FXML TextField textfieldCaloriesBurned;
    @FXML GridPane gridPaneAddEntry;
    @FXML Button buttonAddActivity;
    @FXML ScrollPane activityLogScrollPane;
    @FXML ListView <GridPane> activityLogListView;

    // todo: get actual activityLog via logRepository
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

        setupDynamicCalorieCalculation();
    }

    private void initActivityTypeComboBox(){
        activityTypeComboBox.getEditor().textProperty().addListener((obs, oldVal, newVal) -> {
            // Ignore programmatic changes (like when we call .clear() on button click)
            if (!activityTypeComboBox.getEditor().isFocused()) {
                return;
            }

           String selected = activityTypeComboBox.getSelectionModel().getSelectedItem();

            /* EXCEPT FROM FilteredList.java
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
        try {
            String activityName = activityTypeComboBox.getEditor().getText();

            if (activityName == null || activityName.isEmpty()) return;

            double duration = Double.parseDouble(textfieldDuration.getText());
            double calories = Double.parseDouble(textfieldCaloriesBurned.getText());
            int currentUserId = 1; // Replace with actual Session/Login ID

            boolean isAdded = activityRepository.addActivityRecord(currentUserId, activityName, duration, calories);

            if (isAdded) {
                // Clear UI for the next entry
                textfieldDuration.clear();
                textfieldCaloriesBurned.clear();
                activityTypeComboBox.getSelectionModel().clearSelection();
                activityTypeComboBox.getEditor().clear();

                // Removed the lines that closed gridPaneAddEntry so it stays open...

                // Refresh UI List
                activityGridPanes.clear();
                setActivityLog();
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid numeric input.");
        }
    }

    public void onButtonCancelClicked(ActionEvent actionEvent) {
        gridPaneAddEntry.setVisible(!addEntryisVisible);
        gridPaneAddEntry.setManaged(!addEntryisVisible);
        addEntryisVisible = !addEntryisVisible;

        // Clear the input values
        textfieldDuration.clear();
        textfieldCaloriesBurned.clear();
        activityTypeComboBox.getSelectionModel().clearSelection();
        activityTypeComboBox.getEditor().clear();
    }

    public double calculateCalories(double met, double weightKg, int durationMinutes) {
        return (met * 3.5 * (weightKg / 200.0)) * durationMinutes;
    }

    private void setupDynamicCalorieCalculation() {
        textfieldDuration.focusedProperty().addListener((obs, oldVal, isFocused) -> {
            if (!isFocused) updateCalculatedCalories();
        });

        activityTypeComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            updateCalculatedCalories();
        });
    }

    private void updateCalculatedCalories() {
        String selectedName = activityTypeComboBox.getSelectionModel().getSelectedItem();
        String durationText = textfieldDuration.getText();

        if (selectedName != null && !durationText.isEmpty()) {
            try {
                ActivityType type = activityTypeList.stream()
                        .filter(a -> a.getName().equals(selectedName))
                        .findFirst()
                        .orElse(null);

                if (type != null) {
                    int duration = Integer.parseInt(durationText);
                    int currentUserId = 1; // Replace with actual Session/Login ID

                    double weightKg = activityRepository.getUserCurrentWeight(currentUserId);
                    double calories = calculateCalories(type.getMetValue(), weightKg, duration);

                    textfieldCaloriesBurned.setText(String.format("%.2f", calories));
                    textfieldCaloriesBurned.setStyle("-fx-text-fill: green;");
                }
            } catch (NumberFormatException e) {
                textfieldCaloriesBurned.setText("Invalid");
                textfieldCaloriesBurned.setStyle("-fx-text-fill: red;");
            }
        }
    }


}
