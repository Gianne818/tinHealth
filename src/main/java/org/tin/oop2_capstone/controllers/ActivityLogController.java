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
import org.tin.oop2_capstone.database.repositories.ActivityRepository;
import org.tin.oop2_capstone.database.repositories.MealRepository;
import org.tin.oop2_capstone.database.repositories.UserPrefRepository;
import org.tin.oop2_capstone.database.repositories.UserRepository;
import org.tin.oop2_capstone.model.entities.Activity;
import org.tin.oop2_capstone.model.entities.ActivityLog;
import org.tin.oop2_capstone.model.entities.ActivityType;
import org.tin.oop2_capstone.model.entities.UserPreferences;
import org.tin.oop2_capstone.services.DependencyService;
import org.tin.oop2_capstone.services.SessionManager;
import org.tin.oop2_capstone.utils.InputManager;
import org.tin.oop2_capstone.utils.TimeFormatter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.function.Predicate;

import static org.tin.oop2_capstone.database.DeleteData.deleteActivity;

public class ActivityLogController {
    @FXML private Label caloriesBurnedLabel;
    @FXML private Label totalDurationLabel;
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


    private ActivityRepository activityRepository;

    public ActivityLogController() {
        this.activityRepository = DependencyService.getActivityRepository();
    }

    public void initialize(){
        activities = FXCollections.observableArrayList();
        activityGridPanes = FXCollections.observableArrayList();
        activityTypeList = FXCollections.observableArrayList();
        activityTypeNames = FXCollections.observableArrayList();

        setActivityLog();
        setActivityTypes();
        setCaloriesBurnedToday();
        setTotalDurationToday();
        initActivityTypeComboBox();

        filteredList = new FilteredList<>(activityTypeNames);
        activityTypeComboBox.setItems(filteredList);

        setupDynamicCalorieCalculation();
        InputManager.acceptOnlyDouble(textfieldDuration);
        InputManager.acceptOnlyDouble(textfieldCaloriesBurned);
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

    private void setActivityLog() {
        // Good practice: Clear it here automatically
        activityGridPanes.clear();
        int currentUserId = SessionManager.getInstance().getCurrentUser().getUid();
        activities = activityRepository.getUserActivities();

        for (Activity a : activities) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/tin/oop2_capstone/views/log-card.fxml"));
                GridPane root = fxmlLoader.load();
                root.getStylesheets().add(getClass().getResource("/org/tin/oop2_capstone/styles/application.css").toExternalForm());
                root.getStyleClass().addAll("getThemeClass()", "activityLogScrollPane");

                LogCardController logCardController = fxmlLoader.getController();

                // Create a final reference for the lambda context
                final Activity currentActivity = a;

                logCardController.setData(
                        currentActivity.getActivityType().getName(),
                        TimeFormatter.formatTo12Hour(currentActivity.getLogDateTime().toLocalTime()),
                        currentActivity.getQuantity(),
                        currentActivity.getUnit(),
                        currentActivity.getCalories(),
                        false,
                        true,
                        () -> {
                            showDeletePopup(currentActivity.getActivityType().getName(), () -> {
                                boolean deleted = deleteActivity(currentActivity.getActivityId());
                                if (deleted) {
                                    activityGridPanes.clear();
                                    setActivityLog();
                                }
                            });
                        }
                );
                activityGridPanes.add(root);
            } catch (IOException e) {
                System.out.println("OH NNOI");
                e.printStackTrace();
            }
        }

        activityLogListView.setItems(activityGridPanes);
    }

    private void showDeletePopup(String itemName, Runnable onConfirm) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/tin/oop2_capstone/views/confirm-popup-view.fxml"));
            javafx.scene.layout.StackPane popup = loader.load(); // FIX: Changed to StackPane
            ConfirmPopupController controller = loader.getController();

            javafx.scene.layout.AnchorPane root = (javafx.scene.layout.AnchorPane) activityLogScrollPane.getScene().getRoot();
            javafx.scene.Node mainContent = root.getChildren().get(0);

            javafx.scene.layout.AnchorPane.setTopAnchor(popup, 0.0);
            javafx.scene.layout.AnchorPane.setBottomAnchor(popup, 0.0);
            javafx.scene.layout.AnchorPane.setLeftAnchor(popup, 0.0);
            javafx.scene.layout.AnchorPane.setRightAnchor(popup, 0.0);

            controller.setupDeleteMode(itemName, onConfirm, mainContent);
            root.getChildren().add(popup);
        } catch (IOException e) { e.printStackTrace(); }
    }

    private boolean addEntryisVisible = false;
    public void onButtonAddActivityClicked(ActionEvent actionEvent) {
        gridPaneAddEntry.setVisible(!addEntryisVisible);
        gridPaneAddEntry.setManaged(!addEntryisVisible);
        addEntryisVisible = !addEntryisVisible;
    }

    // TODO: Convert the textfield inputs into strings and add them into the database(?)
    // TODO: refresh the listview if it queries from the database to load new added activity(?)
    public void onButtonAddEntryClicked(ActionEvent actionEvent) {
        try {
            String activityName = activityTypeComboBox.getEditor().getText();

            if (activityName == null || activityName.isEmpty()) return;

            // 1. Find the ActivityType directly inside the Controller's local list
            ActivityType selectedType = activityTypeList.stream()
                    .filter(a -> a.getName().equalsIgnoreCase(activityName))
                    .findFirst()
                    .orElse(null);

            if (selectedType == null) {
                System.out.println("Activity type not found.");
                return;
            }

            double duration = Double.parseDouble(textfieldDuration.getText());
            double calories = Double.parseDouble(textfieldCaloriesBurned.getText());
            int currentUserId = SessionManager.getInstance().getCurrentUser().getUid();

            // 2. Build the Activity object right here
            Activity newActivity = new Activity();
            newActivity.setActivityType(selectedType);
            newActivity.setQuantity(duration);
            newActivity.setCalories(calories);
            newActivity.setLogDateTime(LocalDateTime.now());

            // 3. Pass the userId and the created activity object to the repository
            boolean isAdded = activityRepository.addActivity(newActivity, currentUserId);

            if (isAdded) {
                textfieldDuration.clear();
                textfieldCaloriesBurned.clear();
                activityTypeComboBox.getSelectionModel().clearSelection();
                activityTypeComboBox.getEditor().clear();

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
                    int currentUserId = SessionManager.getInstance().getCurrentUser().getUid();

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

    public void setCaloriesBurnedToday(){
        Double caloriesBurnedToday = activityRepository.getTodayCaloriesOut(SessionManager.getInstance().getCurrentUser().getUid());
        String showCaloriesBurnedToday = String.format("%.1f", caloriesBurnedToday);
        caloriesBurnedLabel.setText(showCaloriesBurnedToday);
    }

    public void setTotalDurationToday(){
        Double totalDurationToday = activityRepository.getTodayActivitiesDuration(SessionManager.getInstance().getCurrentUser().getUid());
        String showTotalDurationToday = String.format("%.0f", totalDurationToday);
        totalDurationLabel.setText(showTotalDurationToday);
    }

    private String getThemeClass() {
        UserPreferences prefs = SessionManager.getInstance().getCurrentUserPrefs();
        if (prefs != null && "Dark".equalsIgnoreCase(prefs.getTheme())) {
            return "dark";
        }
        return "light";
    }

}
