package org.tin.oop2_capstone.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import org.tin.oop2_capstone.api.FoodAPI;
import org.tin.oop2_capstone.database.InsertData;
import org.tin.oop2_capstone.database.repositories.MealRepository;
import org.tin.oop2_capstone.database.repositories.UserRepository;
import org.tin.oop2_capstone.model.entities.*;
import org.tin.oop2_capstone.model.state.IdleState;
import org.tin.oop2_capstone.model.state.LoadingState;
import org.tin.oop2_capstone.model.state.State;
import org.tin.oop2_capstone.services.SearchInterpreter;
import org.tin.oop2_capstone.utils.TimeFormatter;

import org.tin.oop2_capstone.services.FoodParser;
import org.tin.oop2_capstone.utils.TimeFormatter;

import javax.swing.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class FoodLogController {
    @FXML ListView <GridPane> foodLogListView;
    @FXML Button buttonAddFood;
    @FXML GridPane gridPaneAddEntry;
    @FXML ScrollPane foodLogScrollPane;

    @FXML Button addEntryButton;

    private List<Meal> meals;
    private ObservableList<GridPane> foodGridPanes;

    @FXML private TextField foodNameTextField;
    @FXML private TextField caloriesTextField;
    @FXML private TextField timeTextField;
    @FXML ChoiceBox<String> mealChoiceBox;

    private String selectedFoodName;
    private double fetchedCalories;

    private MealRepository mealRepository = MealRepository.getInstance();

    private State currentState;
    private Consumable fetchedFood;

    public void initialize(){
        foodLogScrollPane.getStyleClass().add("light");
        meals = FXCollections.observableArrayList();
        foodGridPanes = FXCollections.observableArrayList();
        setFoodLog();

        mealChoiceBox.getItems().addAll(MealType.BREAKFAST.toString(), MealType.LUNCH.toString(), MealType.DINNER.toString(), MealType.SNACK.toString());

        currentState = new IdleState();
        foodNameTextField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) { // onLostFocus
                validateAndFetchFood();
            }
        });

        mealChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && selectedFoodName != null && !selectedFoodName.isEmpty()) {
                updateTimeBasedOnMeal();
            }
        });
    }

    private void validateAndFetchFood() {
        // TODO: Implement relevant changes to take States into account.
        String rawFoodName = foodNameTextField.getText().trim();
        // Format the name first
        String foodName = formatFoodName(rawFoodName);
        // Update the text field with formatted name (must be on UI thread)
        if (!rawFoodName.equals(foodName)) {
            foodNameTextField.setText(foodName);  // This is already on UI thread since focus lost event runs on UI thread
        }

        // Validate: not empty and length > 1 character
        if (foodName.isEmpty() || foodName.length() <= 1) {
            caloriesTextField.setText("- -");
            caloriesTextField.setStyle(""); // Clears any previously set inline red styling

            // Add the CSS class safely without duplicating it
            if (!caloriesTextField.getStyleClass().contains("lightText")) {
                caloriesTextField.getStyleClass().add("lightText");
            }
            return;
        }

        // Run API call in background thread to avoid freezing UI
        new Thread(() -> {
            try {
                List<String> foodsList = SearchInterpreter.interpret(foodName);
                List<String> jsons = new ArrayList<>();

                for(String s : foodsList){
                    jsons.add(FoodAPI.getFoodData(s.trim().replace(" ", "+")));
                }

                // Add null check here
                if (jsons.isEmpty() || jsons.getFirst() == null) {
                    Platform.runLater(() -> {
                        caloriesTextField.setText("Error: Food not found.");
                        caloriesTextField.setStyle("-fx-text-fill: red;");
                    });
                    return;
                }

                Consumable consumable;
                if(jsons.size() == 1){
                    consumable = FoodParser.parseFood(jsons.getFirst());
                } else {
                    List<Food> foodComboFoods = new ArrayList<>();
                    for(String s : jsons){
                        foodComboFoods.add(FoodParser.parseFood(s));
                    }
                    consumable = new FoodCombo(foodName, foodComboFoods);
                }
                System.out.println("Consumable: " + consumable.getName());
                // Update UI on JavaFX thread
                Platform.runLater(() -> {
                    if (consumable != null && consumable.getNutrition() != null) {
                        fetchedFood = consumable;  // Store the full Food object
                        fetchedCalories = consumable.getNutrition().getCalories();
                        caloriesTextField.setText(String.format("%.1f", fetchedCalories));
                        caloriesTextField.setStyle("-fx-text-fill: #11B981;");
                        selectedFoodName = formatFoodName(consumable.getName());

                        // Auto-set time based on meal type
                        updateTimeBasedOnMeal();
                    } else {
                        caloriesTextField.setText("Not found");
                        caloriesTextField.setStyle("-fx-text-fill: red;");
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    caloriesTextField.setText("Error 3");
                    caloriesTextField.setStyle("-fx-text-fill: red;");
                });
                e.printStackTrace();
            }
        }).start();
    }

    private String formatFoodName(String rawName) {
        // TODO: Only capitalize the first letter of each word. For example: Fried Chicken
        if (rawName == null || rawName.isEmpty()) return rawName;

        String[] words = rawName.trim().toLowerCase().split("\\s+");
        StringBuilder formatted = new StringBuilder();

        for(char c : rawName.toCharArray()){

        }

        for (String word : words) {
            if (word.length() > 0) {
                formatted.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1))
                        .append(" ");
            }
        }

        return formatted.toString().trim();
    }

    private void updateTimeBasedOnMeal() {
        String selectedMealStr = mealChoiceBox.getValue();
        LocalTime currentTime = LocalTime.now();

        if (selectedMealStr != null && !selectedMealStr.isEmpty()) {
            MealType selectedMeal = MealType.valueOf(selectedMealStr.toUpperCase());

            if (selectedMeal.isWithinRange(currentTime)) {
                timeTextField.setText(TimeFormatter.formatTo12Hour(currentTime));
            } else {
                timeTextField.setText(selectedMeal.getDefaultTimeRange());
            }
        } else {
            timeTextField.setText(TimeFormatter.formatTo12Hour(currentTime));
        }
    }

    private void setFoodLog() {
        // 1. Get the data from the repository
        meals = mealRepository.getUserMeals();

        if (meals != null) {
            for(Meal m : meals) {
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/tin/oop2_capstone/views/log-card.fxml"));
                    GridPane root = fxmlLoader.load();
                    root.getStylesheets().add(getClass().getResource("/org/tin/oop2_capstone/styles/application.css").toExternalForm());
                    root.getStyleClass().addAll("light", "foodLogScrollPane");

                    LogCardController logCardController = fxmlLoader.getController();

                    //gathering data
                    String foodName = m.getConsumable().getName();
                    String mealType = m.getMealType().name().charAt(0) + m.getMealType().name().substring(1).toLowerCase();
                    String logTime = mealType + " • " + TimeFormatter.formatTo12Hour(m.getLogDateTime().toLocalTime());
                    double quantity = m.getQuantity();
                    String unit = m.getUnit();
                    double totalCalories = m.getNutritionDetails().getCalories() * quantity;

                    //load data into
                    logCardController.setData(foodName, logTime, quantity, unit, totalCalories, false, true);

                    foodGridPanes.add(root);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        foodLogListView.setItems(foodGridPanes);
    }

    private boolean addEntryisVisible = false;
    public void onButtonAddFoodClicked(ActionEvent actionEvent) { // @FXML Button addEntryButton;
        gridPaneAddEntry.setVisible(!addEntryisVisible);
        gridPaneAddEntry.setManaged(!addEntryisVisible);
        addEntryisVisible = !addEntryisVisible;
        //
    }

    public void onButtonAddEntryClicked(ActionEvent actionEvent) {
        // Transition to loading state when starting API call
        setState(new LoadingState());

        // TODO: Implement API call logic here
        // After API call completes, transition to appropriate state:
        // - SuccessState if API returns data
        // - PendingState if API fails but we can create pending entry
        // - ErrorState if there's an due to internet connection error

        // For now, simulate success after a delay (replace with actual API call)
        // setState(new SuccessState());
        // Validate all fields
        if (foodNameTextField.getText().trim().isEmpty() ||
                foodNameTextField.getText().trim().length() <= 1) {
            return;
        }

        if (caloriesTextField.getText().equals("- -") ||
                caloriesTextField.getText().equals("Not found")) {
            return;
        }

        // Create and save meal
        try {
            Consumable consumable = fetchedFood;

            MealType mealType = MealType.valueOf(mealChoiceBox.getValue().toUpperCase());
            LocalDateTime logTime = parseTimeFromTextField();

            Meal meal = new Meal(mealType, consumable, logTime, 1.0, "serving");

            // Save to database (implement in MealRepository)
            int userId = UserRepository.getInstance().getUser().getUid();

            //TODO: Run through interpreter before insertion, and behave accordingly. And also, DO NOT CALL ANY CRUD FROM NON REPOSITORY CLASSES.

                // Also add to repository's local list for UI updates
                if(mealRepository.addMeal(meal, userId)){
                refreshFoodLog();
            } else {
                showError("Failed to save into database");
            }
            // Clear form
            clearAddEntryForm();

            // Refresh UI
            refreshFoodLog();

            // Clear form
            clearAddEntryForm();

        } catch (Exception e) {
            showError("Error saving food entry");
            e.printStackTrace();
        }
    }

    private LocalDateTime parseTimeFromTextField() {
        String timeStr = timeTextField.getText();
        // Implement time parsing logic
        return LocalDateTime.now();
    }

    private void refreshFoodLog() {
        foodGridPanes.clear();
        setFoodLog();
    }

    private void clearAddEntryForm() {
        foodNameTextField.clear();
        caloriesTextField.setText("- -");
        mealChoiceBox.setValue(null);
        timeTextField.clear();
        selectedFoodName = null;
        fetchedCalories = 0;
        fetchedFood = null;
        gridPaneAddEntry.setVisible(false);
        gridPaneAddEntry.setManaged(false);
        addEntryisVisible = false;
    }

    private void showError(String message) {
        // TODO: Show error on actual UI
        System.out.println("Error: " + message);
    }

    public void onButtonCancelClicked(ActionEvent actionEvent) {
        // TODO: Pressing this, will empty everything...
        gridPaneAddEntry.setVisible(!addEntryisVisible);
        gridPaneAddEntry.setManaged(!addEntryisVisible);
        addEntryisVisible = !addEntryisVisible;
    }

    /** Mga State Functions */

    public void enableFoodLogInput() {
        buttonAddFood.setDisable(false);
        addEntryButton.setDisable(false);
    }

    public void disableFoodLogInput() {
        buttonAddFood.setDisable(true);
        addEntryButton.setDisable(true);
    }

    public void showLoadingIndicator() {
        // TODO: Implement loading indicator visibility
        // e.g., show a progress indicator or spinner
    }

    public void hideLoadingIndicator() {
        // TODO: Implement loading indicator hiding
    }

    public void showErrorMessage() {
        // TODO: Implement error message display
        // e.g., show a label with error text
    }

    public void hideErrorMessage() {
        // TODO: Implement error message hiding
    }

    public void showPendingIndicator() {
        // TODO: Implement pending indicator display
        // e.g., show a message indicating data is pending sync
    }

    public void createPendingFoodEntry() {
        // TODO: Implement pending food entry creation
        // Create food object with isPending = true
        // Set API values to zero
        // Proceed with SyncMonitor
    }

    public void populateFoodList() {
        // Refresh the food list view
        setFoodLog();
    }

    public void setState(State state) {
        this.currentState = state;
        currentState.handle(this);
    }

    /** End of State Functions */
}

