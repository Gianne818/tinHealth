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
import org.tin.oop2_capstone.api.APIResponse;
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

    private SelectedFoodController selectedFoodController = SelectedFoodController.getInstance();


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
        if (rawName == null || rawName.isEmpty()) return rawName;

        String[] words = rawName.trim().toLowerCase().split("\\s+");
        StringBuilder formatted = new StringBuilder();
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
                    String logTime = mealType + " • " + TimeFormatter.formatTo12Hour(m.getLogDateTime().toLocalTime());
                    double quantity = m.getQuantity();
                    String unit = m.getUnit();
                    double totalCalories = m.getNutritionDetails().getCalories() * quantity;

                    final Meal currentMeal = m;

                    logCardController.setData(
                            foodName,
                            logTime,
                            quantity,
                            unit,
                            totalCalories,
                            false,
                            true,
                            () -> {
//                                System.out.println("ID to delete: " + currentMeal.getMealId());
                                boolean deleted = org.tin.oop2_capstone.database.DeleteData.deleteMeal(currentMeal.getMealId());
                                if (deleted) {
                                    // Clear and refresh UI list cleanly
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
//        clearAddEntryForm();
        //
    }

    public void onButtonAddEntryClicked(ActionEvent actionEvent) {

        // errorState handling for foodNameComboBox
        if (foodNameComboBox.getValue().trim().isEmpty()) {
            setState(new ErrorState());
            return;
        }
        // Validate all fields
        if(!isDouble(caloriesTextField.getText()) || timeTextField.getText().isEmpty() || selectedFoods.isEmpty()){
            setState(new ErrorState());
            return;
        }


        if (caloriesTextField.getText().equals("- -") || caloriesTextField.getText().equals("Not found")) {
            setState(new ErrorState());
            return;
        }

        /* starting of the loading state */
         setState(new LoadingState());


        // Create and save meal
//        try {
//            Consumable consumable;
//            if(selectedFoods.size() > 1){
//                String foodComboName = "";
//                for(int i = 0; i<selectedFoods.size(); i++){
//                    if(i!=selectedFoods.size()-1) foodComboName += selectedFoods.get(i).getName() + ", ";
//                    else foodComboName += selectedFoods.get(i).getName();
//                }
//                consumable = new FoodCombo(foodComboName, selectedFoods);
//            } else {
//                consumable = new Food(selectedFoods.getFirst().getName(), selectedFoods.getFirst().getNutrition(), false);
//            }
//
//
//            MealType mealType = MealType.valueOf(mealChoiceBox.getValue().toUpperCase());
//            LocalDateTime logTime = LocalDateTime.now();
//
//            Meal meal = new Meal(mealType, consumable, logTime, 1.0, "serving");
//            int userId = UserRepository.getInstance().getUser().getUid();
//
//            if(mealRepository.addMeal(meal, userId)) refreshFoodLog();
//            else showError("Failed to save into database");
//
//
//            clearAddEntryForm();
//
//        } catch (Exception e) {
//            showError("Error saving food entry");
//            e.printStackTrace();
//        }
//        clearAddEntryForm();

        /* Aaaah can't help it : james.executeVibe() */

        /* this whole code start executeVibe() */

        // Run API call in background thread
        new Thread(() -> {
            try {
                Consumable consumable = fetchedFood;
                MealType mealType = MealType.valueOf(mealChoiceBox.getValue().toUpperCase());
                LocalDateTime logTime = LocalDateTime.now();

                Meal meal = new Meal(mealType, consumable, logTime, 1.0, "serving");
                int userId = UserRepository.getInstance().getUser().getUid();

                // Check if food is pending (API call failed)
                if (consumable.isPending()) {
                    // Food already pending, just save it
                    if(mealRepository.addMeal(meal, userId)){
                        Platform.runLater(() -> {
                            setState(new PendingState());
                            refreshFoodLog();
                            clearAddEntryForm();
                        });
                    } else {
                        Platform.runLater(() -> setState(new ErrorState()));
                    }
                } else {
                    // Food has valid nutrition data, proceed normally
                    if(mealRepository.addMeal(meal, userId)){
                        Platform.runLater(() -> {
                            setState(new SuccessState());
                            refreshFoodLog();
                            clearAddEntryForm();
                        });
                    } else {
                        Platform.runLater(() -> setState(new ErrorState()));
                    }
                }

            } catch (Exception e) {
                Platform.runLater(() -> setState(new ErrorState()));
                e.printStackTrace();
            }
        }).start();
    }

    private void validateAndFetchFood() {
        String rawFoodName = foodNameComboBox.getValue().trim();
        String foodName = formatFoodName(rawFoodName);

        if (!rawFoodName.equals(foodName)) {
            foodNameComboBox.setValue(foodName);
        }

        if (foodName.isEmpty() || foodName.length() <= 1) {
            caloriesTextField.setText("- -");
            caloriesTextField.getStyleClass().clear();
            caloriesTextField.getStyleClass().add("lightText");
            return;
        }

        new Thread(() -> {
            try {
                List<String> foodsList = SearchInterpreter.interpret(foodName);
                List<APIResponse> responses = new ArrayList<>();

                for(String s : foodsList){
                    responses.add(FoodAPI.getFoodData(s.trim().replace(" ", "+")));
                }

                // Check HTTP responses
                if (responses.isEmpty() || responses.getFirst().getJson() == null) {
                    handleAPIError(responses.isEmpty() ? -1 : responses.getFirst().getHttpCode());
                    return;
                }

                Consumable consumable;
                if(responses.size() == 1){
                    APIResponse response = responses.getFirst();

                    // Check HTTP code
                    if (response.getHttpCode() == 200) {
                        // Success
                        consumable = FoodParser.parseFood(response.getJson());
                    } else if (isRetryable(response.getHttpCode())) {
                        // Pending - API error but can retry
                        consumable = createPendingFood(foodName);
                    } else {
                        // Error state
                        handleAPIError(response.getHttpCode());
                        return;
                    }
                } else {
                    // Multiple foods
                    List<Food> foodComboFoods = new ArrayList<>();
                    for(APIResponse resp : responses){
                        if (resp.getHttpCode() == 200) {
                            foodComboFoods.add(FoodParser.parseFood(resp.getJson()));
                        }
                    }

                    if (foodComboFoods.isEmpty()) {
                        handleAPIError(responses.getFirst().getHttpCode());
                        return;
                    }
                    consumable = new FoodCombo(foodName, foodComboFoods);
                }

                // Update UI on JavaFX thread
                Platform.runLater(() -> {
                    if (consumable != null) {
                        fetchedFood = consumable;
                        fetchedCalories = consumable.getNutrition() != null ?
                                consumable.getNutrition().getCalories() : 0;

                        if (consumable.isPending()) {
                            caloriesTextField.setText("Pending sync...");
                            caloriesTextField.setStyle("-fx-text-fill: #F59E0B;"); // Orange
                        } else {
                            caloriesTextField.setText(String.format("%.1f", fetchedCalories));
                            caloriesTextField.setStyle("-fx-text-fill: #11B981;"); // Green
                        }
                        selectedFoodName = formatFoodName(consumable.getName());
                        updateTimeBasedOnMeal();
                    } else {
                        caloriesTextField.setText("Not found");
                        caloriesTextField.setStyle("-fx-text-fill: red;");
                    }
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    caloriesTextField.setText("Error");
                    caloriesTextField.setStyle("-fx-text-fill: red;");
                });
                e.printStackTrace();
            }
        }).start();
    }

    private Consumable createPendingFood(String foodName) {
        // Create food with empty nutrition and isPending = true
        NutritionDetails emptyNutrition = new NutritionDetails(0, 0, 0, 0,0, 0, 0, 0);
        return new Food(foodName, emptyNutrition, true);
    }

    private boolean isRetryable(int httpCode) {
        return httpCode == 429  // Rate limited
                || httpCode == 503  // Service unavailable
                || httpCode == 504  // Gateway timeout
                || httpCode == -1;  // Connection error
    }

    private void handleAPIError(int httpCode) {
        Platform.runLater(() -> {
            String errorMsg;
            if (httpCode == 429) {
                errorMsg = "API rate limited. Retry later.";
            } else if (httpCode == 503 || httpCode == 504) {
                errorMsg = "API unavailable. Pending sync...";
            } else if (httpCode == -1) {
                errorMsg = "No internet connection.";
            } else {
                errorMsg = "API error: " + httpCode;
            }
            caloriesTextField.setText(errorMsg);
            caloriesTextField.setStyle("-fx-text-fill: #F59E0B;"); // Orange for pending
        });
    }

    /* end of executeVibe() */


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
        foodNameEntryHBox.getChildren().clear();
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
        clearAddEntryForm();
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

