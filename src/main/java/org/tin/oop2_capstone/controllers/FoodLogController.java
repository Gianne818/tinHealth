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
import javafx.scene.layout.HBox;
import org.tin.oop2_capstone.api.FoodAPI;
import org.tin.oop2_capstone.database.InsertData;
import org.tin.oop2_capstone.database.repositories.MealRepository;
import org.tin.oop2_capstone.database.repositories.UserRepository;
import org.tin.oop2_capstone.model.entities.*;
import org.tin.oop2_capstone.model.state.*;
import org.tin.oop2_capstone.services.SearchInterpreter;
import org.tin.oop2_capstone.services.SessionManager;
import org.tin.oop2_capstone.utils.TimeFormatter;

import org.tin.oop2_capstone.services.FoodParser;
import org.tin.oop2_capstone.utils.TimeFormatter;

import javax.swing.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.tin.oop2_capstone.database.RetrieveData.fetchUserMeals;

import java.util.Map;
import java.util.function.Predicate;
//CHECK
public class FoodLogController {
    @FXML ListView <GridPane> foodLogListView;
    @FXML Button buttonAddFood;
    @FXML GridPane gridPaneAddEntry;
    @FXML ScrollPane foodLogScrollPane;

    @FXML Button addEntryButton;

    private List<Meal> meals;
    private ObservableList<GridPane> foodGridPanes;

    @FXML private ComboBox<String> foodNameComboBox;
    @FXML private TextField caloriesTextField;
    @FXML private TextField timeTextField;
    @FXML ChoiceBox<String> mealChoiceBox;
    @FXML private HBox foodNameEntryHBox;
    @FXML private ProgressIndicator apiWaitingProgressIndicator;

    private FilteredList<String> filteredList;
    private Map<String, Food> searchRes;

    private String selectedFoodName;
    private double fetchedCalories;

    private List<Food> selectedFoods;


    private MealRepository mealRepository = MealRepository.getInstance();

    private State currentState;
    private Consumable fetchedFood;


    public void initialize(){
        foodLogScrollPane.getStyleClass().add("light");
        meals = FXCollections.observableArrayList();
        foodGridPanes = FXCollections.observableArrayList();
        setFoodLog();

        searchRes = new HashMap<>();
        selectedFoods = new ArrayList<>();

        mealChoiceBox.getItems().addAll(MealType.BREAKFAST.toString(), MealType.LUNCH.toString(), MealType.DINNER.toString(), MealType.SNACK.toString());

        currentState = new IdleState();
        mealChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                updateTimeBasedOnMeal();
            }
        });
        initfoodNameComboBox();
    }

    private void addSelectedFood(Food food){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/tin/oop2_capstone/views/selected-food-view.fxml"));
            HBox selectedFood = fxmlLoader.load();
            SelectedFoodController selectedFoodController = fxmlLoader.getController();
            selectedFoodController.setFoodNameLabel(food.getName());
            foodNameEntryHBox.getChildren().add(selectedFood);

            selectedFoodController.setOnDeleteAction(() -> {
                foodNameEntryHBox.getChildren().remove(selectedFood);
                selectedFoods.remove(food);
                calculateCalories();
            });
            foodNameComboBox.getItems().clear();
            foodNameComboBox.getEditor().clear();

        } catch (IOException e){
            e.printStackTrace();
        }
    }

    // we use this var to see if user stopped typing. If so, wakeup thread to query api
    int currentKeyStroke = 0;
    String userTypedName;
    private void initfoodNameComboBox() {
        foodNameComboBox.getEditor().setOnKeyReleased(event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.ENTER) {
                String text = foodNameComboBox.getEditor().getText().trim();
                if (!text.isEmpty() && !(currentState instanceof LoadingState)) {
                    userTypedName = text;
                    searchRes.clear();
                    foodNameComboBox.getItems().clear();
                    fetchFoodFromApi(text);
                    event.consume();
                }
            }
        });

        foodNameComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && searchRes.containsKey(newVal)) {
                Food consumable = searchRes.get(newVal);
                addSelectedFood(consumable);
                selectedFoods.add(consumable);
                consumable.setName(formatFoodName(userTypedName));
                calculateCalories();
            }
        });
    }


    private void fetchFoodFromApi(String food){
        if(food.length() <= 1) return;

        new Thread(() -> {
            try{
                currentState = new LoadingState();
                currentState.handle(this);
                String json = FoodAPI.getFoodData(food.trim().replace(" ", "+"));
                if (json == null || json.isBlank()) {
                    Platform.runLater(() -> {
                        caloriesTextField.setText("Error: Food not found.");
                        caloriesTextField.getStyleClass().add("redLabel");
                    });
                    currentState = new ErrorState();
                    currentState.handle(this);
                    return;
                }

                List<Food> fetchedFoods = FoodParser.parseFoods(json);
                if(fetchedFoods != null && !fetchedFoods.isEmpty()){

                    Platform.runLater(() -> {
                        searchRes.clear();
                        ObservableList<String>  dropDown = FXCollections.observableArrayList();
                        for(Food f : fetchedFoods) {
                            searchRes.put(f.getName(), f);
                            dropDown.add(f.getName());
                        }

                        foodNameComboBox.setItems(dropDown);

                        if(!foodNameComboBox.isShowing()){
                            foodNameComboBox.show();
                        }
                    });
                }

                currentState = new SuccessState();
                currentState.handle(this);

            } catch (Exception e){
                e.printStackTrace();
            }
        }).start();
    }

    private void calculateCalories(){
        double curCal = 0;
        for(Food f : selectedFoods){
            curCal+=f.getNutrition().getCalories();
        }

        caloriesTextField.setText(String.format("%.2f", curCal));
        caloriesTextField.getStyleClass().remove("redLabel");
        caloriesTextField.getStyleClass().add("greenLabel");
    }

    private boolean isDouble(String text){
        if(text == null || text.isEmpty()) return false;
        try{
             Double.parseDouble(text);
             return true;
        } catch (NumberFormatException e){
            return false;
        }
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
        // Bypass repository cache by pulling live rows using the session User ID
        int currentUserId = SessionManager.getInstance().getCurrentUser().getUid();
        meals = fetchUserMeals(currentUserId);

        if (meals != null) {
            for (Meal m : meals) {
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/tin/oop2_capstone/views/log-card.fxml"));
                    GridPane root = fxmlLoader.load();
                    root.getStylesheets().add(getClass().getResource("/org/tin/oop2_capstone/styles/application.css").toExternalForm());
                    root.getStyleClass().addAll("light", "foodLogScrollPane");

                    LogCardController logCardController = fxmlLoader.getController();

                    // gathering data
                    String foodName = m.getConsumable().getName();
                    String mealType = m.getMealType().name().charAt(0) + m.getMealType().name().substring(1).toLowerCase();
//                    String logTime = mealType + " • " + TimeFormatter.formatTo12Hour(m.getLogDateTime().toLocalTime());
                    String logTime = mealType + " • " + m.getTime();
                    double totalCalories = m.getNutritionDetails().getCalories(); // Nuked the * quantity

                    final Meal currentMeal = m;

                    logCardController.setData(
                            foodName,
                            logTime,
                            0.0,
                            "",
                            totalCalories,
                            false,
                            true,
                            () -> {
                                boolean deleted = org.tin.oop2_capstone.database.DeleteData.deleteMeal(currentMeal.getMealId());
                                if (deleted) {
                                    foodGridPanes.clear();
                                    setFoodLog();
                                }
                            }
                    );

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
        if(!isDouble(caloriesTextField.getText()) || timeTextField.getText().isEmpty() || selectedFoods.isEmpty()){
            return;
        }

        try {
            Consumable consumable;
            if(selectedFoods.size() > 1){
                String foodComboName = "";
                for(int i = 0; i<selectedFoods.size(); i++){
                    if(i!=selectedFoods.size()-1) foodComboName += selectedFoods.get(i).getName() + ", ";
                    else foodComboName += selectedFoods.get(i).getName();
                }
                consumable = new FoodCombo(foodComboName, selectedFoods);
            } else {
                consumable = new Food(selectedFoods.getFirst().getName(), selectedFoods.getFirst().getNutrition(), false);
            }

            MealType mealType = MealType.valueOf(mealChoiceBox.getValue().toUpperCase());

            // Just grab today's date and the raw string from the text field!
            Meal meal = new Meal(mealType, consumable, LocalDate.now(), timeTextField.getText().trim());

            int userId = UserRepository.getInstance().getUser().getUid();

            if(mealRepository.addMeal(meal, userId)) refreshFoodLog();
            else showError("Failed to save into database");

            clearAddEntryForm();

        } catch (Exception e) {
            showError("Error saving food entry");
            e.printStackTrace();
        }
    }

    private void refreshFoodLog() {
        foodGridPanes.clear();
        setFoodLog();
    }

    private void clearAddEntryForm() {
        caloriesTextField.setText("- -");
        mealChoiceBox.setValue(null);
        timeTextField.clear();
        selectedFoodName = null;
        fetchedCalories = 0;
        fetchedFood = null;
        gridPaneAddEntry.setVisible(false);
        gridPaneAddEntry.setManaged(false);
        addEntryisVisible = false;
        selectedFoods.clear();
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
        apiWaitingProgressIndicator.setManaged(true);
        apiWaitingProgressIndicator.setVisible(true);
    }

    public void hideLoadingIndicator() {
        apiWaitingProgressIndicator.setVisible(false);
        apiWaitingProgressIndicator.setManaged(false);
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

