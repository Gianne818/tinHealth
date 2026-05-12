package org.tin.oop2_capstone.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import org.tin.oop2_capstone.api.FoodAPI;
import org.tin.oop2_capstone.database.repositories.MealRepository;
import org.tin.oop2_capstone.model.entities.Food;
import org.tin.oop2_capstone.model.entities.Meal;
import org.tin.oop2_capstone.model.entities.MealType;
import org.tin.oop2_capstone.model.entities.NutritionDetails;
import org.tin.oop2_capstone.services.FoodParser;
import org.tin.oop2_capstone.utils.TimeFormatter;

import javax.swing.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
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

    public void initialize(){
        foodLogScrollPane.getStyleClass().add("light");
        meals = FXCollections.observableArrayList();
        foodGridPanes = FXCollections.observableArrayList();
        setFoodLog();

        mealChoiceBox.getItems().addAll(MealType.BREAKFAST.toString(), MealType.LUNCH.toString(), MealType.DINNER.toString(), MealType.SNACK.toString());

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
        String foodName = foodNameTextField.getText().trim();

        // Validate: not empty and length > 1 character
        if (foodName.isEmpty() || foodName.length() <= 1) {
            caloriesTextField.setText("- -");
            caloriesTextField.setStyle("-fx-text-fill: red;");
            return;
        }

        // Run API call in background thread to avoid freezing UI
        new Thread(() -> {
            try {
                String json = FoodAPI.getFoodData(foodName.replace(" ", "+"));

                // Add null check here
                if (json == null) {
                    javafx.application.Platform.runLater(() -> {
                        caloriesTextField.setText("API Error");
                        caloriesTextField.setStyle("-fx-text-fill: red;");
                    });
                    return;
                }

                Food food = FoodParser.parseFood(json);

                // Update UI on JavaFX thread
                javafx.application.Platform.runLater(() -> {
                    if (food != null && food.getNutrition() != null) {
                        fetchedCalories = food.getNutrition().getCalories();
                        caloriesTextField.setText(String.format("%.1f", fetchedCalories));
                        caloriesTextField.setStyle("-fx-text-fill: #11B981;");
                        selectedFoodName = food.getName();

                        // Auto-set time based on meal type
                        updateTimeBasedOnMeal();
                    } else {
                        caloriesTextField.setText("Not found");
                        caloriesTextField.setStyle("-fx-text-fill: red;");
                    }
                });
            } catch (Exception e) {
                javafx.application.Platform.runLater(() -> {
                    caloriesTextField.setText("Error");
                    caloriesTextField.setStyle("-fx-text-fill: red;");
                });
                e.printStackTrace();
            }
        }).start();
    }

    private void updateTimeBasedOnMeal() {
        String selectedMeal = mealChoiceBox.getValue();
        LocalTime currentTime = LocalTime.now();

        if (selectedMeal != null && !selectedMeal.isEmpty()) {
            switch (selectedMeal.toUpperCase()) {
                case "BREAKFAST":
                    timeTextField.setText("08:00 AM");
                    break;
                case "LUNCH":
                    timeTextField.setText("12:00 PM");
                    break;
                case "DINNER":
                    timeTextField.setText("07:00 PM");
                    break;
                default: // SNACK
                    timeTextField.setText(TimeFormatter.formatTo12Hour(currentTime));
                    break;
            }
        } else {
            // Set default time without meal selected
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
                    System.out.println("Error loading food log card.");
                    e.printStackTrace();
                }
            }
        }

        foodLogListView.setItems(foodGridPanes);
    }

    private boolean addEntryisVisible = false;
    public void onButtonAddFoodClicked(ActionEvent actionEvent) {
        gridPaneAddEntry.setVisible(!addEntryisVisible);
        gridPaneAddEntry.setManaged(!addEntryisVisible);
        addEntryisVisible = !addEntryisVisible;
    }

    public void onButtonEntryFoodClicked(ActionEvent actionEvent){

    }

    public void onButtonAddEntryClicked(ActionEvent actionEvent) {
        // TODO: Convert the textfield inputs into strings and add them into the database(?)
        // TODO: refresh the listview if it queries from the database to load new added activity(?)
        // Validate all fields
        if (foodNameTextField.getText().trim().isEmpty() ||
                foodNameTextField.getText().trim().length() <= 1) {
            showError("Please enter a valid food name");
            return;
        }

        if (caloriesTextField.getText().equals("- -") ||
                caloriesTextField.getText().equals("Not found")) {
            showError("Please enter a valid food name first");
            return;
        }

        // Create and save meal
        try {
            NutritionDetails nutrition = new NutritionDetails(fetchedCalories, 0, 0, 0, 0, 0, 0, 0);
            Food food = new Food(selectedFoodName, nutrition, false);

            MealType mealType = MealType.valueOf(mealChoiceBox.getValue().toUpperCase());
            LocalDateTime logTime = parseTimeFromTextField();

            Meal meal = new Meal(mealType, food, logTime, 1.0, "serving");

            // Save to database (implement in MealRepository)
            mealRepository.addMeal(meal);

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

        gridPaneAddEntry.setVisible(false);
        gridPaneAddEntry.setManaged(false);
        addEntryisVisible = false;
    }

    private void showError(String message) {
        System.out.println("Error: " + message);
    }

    public void onButtonCancelClicked(ActionEvent actionEvent) {
        gridPaneAddEntry.setVisible(!addEntryisVisible);
        gridPaneAddEntry.setManaged(!addEntryisVisible);
        addEntryisVisible = !addEntryisVisible;
    }

}