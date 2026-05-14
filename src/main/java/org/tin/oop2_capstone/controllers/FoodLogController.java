package org.tin.oop2_capstone.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import org.tin.oop2_capstone.database.repositories.MealRepository;
import org.tin.oop2_capstone.model.entities.Meal;
import org.tin.oop2_capstone.model.entities.MealType;
import org.tin.oop2_capstone.model.state.IdleState;
import org.tin.oop2_capstone.model.state.LoadingState;
import org.tin.oop2_capstone.model.state.State;
import org.tin.oop2_capstone.utils.TimeFormatter;

import java.io.IOException;
import java.util.List;

public class FoodLogController {
    @FXML ListView <GridPane> foodLogListView;
    @FXML Button buttonAddFood;
    @FXML GridPane gridPaneAddEntry;
    @FXML ScrollPane foodLogScrollPane;

    @FXML Button addEntryButton;
    @FXML ChoiceBox<String> mealChoiceBox;

    private List<Meal> meals;
    private ObservableList<GridPane> foodGridPanes;

    private MealRepository mealRepository = MealRepository.getInstance();

    private State currentState;

    public void initialize(){
        foodLogScrollPane.getStyleClass().add("light");
        meals = FXCollections.observableArrayList();
        foodGridPanes = FXCollections.observableArrayList();
        setFoodLog();

        mealChoiceBox.getItems().addAll(MealType.BREAKFAST.toString(), MealType.LUNCH.toString(), MealType.DINNER.toString(), MealType.SNACK.toString());

        currentState = new IdleState();
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

    public void onButtonAddEntryClicked(ActionEvent actionEvent) {
        // Transition to loading state when starting API call
        setState(new LoadingState());

        // TODO: Implement API call logic here
        // After API call completes, transition to appropriate state:
        // - SuccessState if API returns data
        // - PendingState if API fails but we can create pending entry
        // - ErrorState if there's an error

        // For now, simulate success after a delay (replace with actual API call)
        // setState(new SuccessState());
    }

    public void onButtonCancelClicked(ActionEvent actionEvent) {
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
