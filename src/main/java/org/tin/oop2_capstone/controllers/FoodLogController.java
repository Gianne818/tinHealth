package org.tin.oop2_capstone.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import org.tin.oop2_capstone.database.repositories.MealRepository;
import org.tin.oop2_capstone.model.entities.Meal;
import org.tin.oop2_capstone.utils.TimeFormatter;
import java.io.IOException;
import java.util.List;

public class FoodLogController {
    @FXML ListView <GridPane> foodLogListView;
    @FXML Button buttonAddFood;
    @FXML GridPane gridPaneAddEntry;
    @FXML ScrollPane foodLogScrollPane;

    private List<Meal> meals;
    private ObservableList<GridPane> foodGridPanes;

    private MealRepository mealRepository = MealRepository.getInstance();

    public void initialize(){
        foodLogScrollPane.getStyleClass().add("light");
        meals = FXCollections.observableArrayList();
        foodGridPanes = FXCollections.observableArrayList();
        setFoodLog();
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
                    root.getStyleClass().addAll("light", "activityLogScrollPane");

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
        //TODO: Convert the textfield inputs into strings and add them into the database(?)

        //remove the prompt box
        gridPaneAddEntry.setVisible(false);
        addEntryisVisible = !addEntryisVisible;

        //TODO: refresh the listview if it queries from the database to load new added activity(?)
    }

    public void onButtonCancelClicked(ActionEvent actionEvent) {
        gridPaneAddEntry.setVisible(false);
        addEntryisVisible = !addEntryisVisible;
    }

}
