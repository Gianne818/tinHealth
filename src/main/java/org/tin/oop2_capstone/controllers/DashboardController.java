package org.tin.oop2_capstone.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.control.PopupControl;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.chart.XYChart.Series;
import  javafx.scene.chart.PieChart.Data;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import org.tin.oop2_capstone.database.repositories.ActivityRepository;
import org.tin.oop2_capstone.database.repositories.MealRepository;
import org.tin.oop2_capstone.services.SessionManager;
import org.tin.oop2_capstone.utils.SceneSwitcher;

public class DashboardController {
    @FXML ScrollPane dashboardScrollPane;
    @FXML private LineChart<?, ?> weeklyChart;
    @FXML private CategoryAxis xAxis;
    @FXML private NumberAxis yAxis;

    @FXML PieChart macroDistPieChart;
    @FXML Circle macroDistInnerHoleCircle;

    ObservableList<PieChart.Data> macroDistData;

    @FXML private Label proteinLabelMacro;
    @FXML private Label carbsLabelMacro;
    @FXML private Label fatsLabelMacro;

    private MealRepository mealRepository = new MealRepository();
    private ActivityRepository activityRepository = new ActivityRepository();
    @FXML public Label caloriesInLabel;
    @FXML public Label caloriesOutLabel;
    @FXML public Label netCaloriesLabel;
    @FXML public Label activityStreakLabel;

    @FXML private VBox recentFoodLogsContainer;
    @FXML private VBox recentActivityLogsContainer;

    private MainController mainController;

    public void initialize() {
        dashboardScrollPane.getStyleClass().add("light");
        macroDistData = FXCollections.observableArrayList();

        initDashboardHeader();
        initMacroDist();
        initCaloriesLineChart();
        initRecentLogs();
    }

    private void setupGlobalTooltip(LineChart<String, Number> chart, Series<String, Number> in, Series<String, Number> out) {
        PopupControl popup = new PopupControl();
        ToolTipController toolTipController;

        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/tin/oop2_capstone/views/tool-tip-view.fxml"));
            VBox root = fxmlLoader.load();
            popup.getScene().getStylesheets().add(getClass().getResource("/org/tin/oop2_capstone/styles/application.css").toExternalForm());
            root.getStyleClass().add("light");
            popup.getScene().setRoot(root);
            toolTipController = fxmlLoader.getController();
        } catch(IOException e){
            e.printStackTrace();
            return;
        }

        Node plotArea = chart.lookup(".chart-plot-background");
        Node chartContent = chart.lookup(".chart-content");

        // Create vertical line
        Line verticalLine = new Line();
        verticalLine.getStyleClass().add("vertical-line");
        verticalLine.setManaged(false);
        verticalLine.setVisible(false);
        ((Pane) chartContent).getChildren().add(verticalLine);

        plotArea.setOnMouseMoved(e -> {
            double xInAxis = xAxis.sceneToLocal(e.getSceneX(), e.getSceneY()).getX();
            String day = xAxis.getValueForDisplay(xInAxis);

            if (day != null) {
                // Get X position of the category
                double xPos = xAxis.getDisplayPosition(day);
                Point2D chartPoint = chartContent.sceneToLocal(xAxis.localToScene(xPos, 0));

                // Get Y bounds of plot area in chartContent coordinates
                verticalLine.setVisible(true);
                verticalLine.setStartX(chartPoint.getX());
                verticalLine.setStartY(chartContent.sceneToLocal(plotArea.localToScene(0, 0)).getY()); //top left y
                verticalLine.setEndX(chartPoint.getX());
                verticalLine.setEndY(chartContent.sceneToLocal(plotArea.localToScene(0, plotArea.getBoundsInLocal().getHeight())).getY()); // bottom left y

                XYChart.Data<String, Number> inData = findData(in, day);
                XYChart.Data<String, Number> outData = findData(out, day);

                // If the inValue or outValue is null set it to zero. i.e only Monday data, no tuesday or any other dates.
                // In this case, single dot only
                // Another example if today is monday, it will not show a line-connect to Tuesday as that doesn't make sense.
                if (inData != null) {
                    double inValue = inData.getYValue() != null ? inData.getYValue().doubleValue() : 0.0;
                    double outValue = outData != null && outData.getYValue() != null ? outData.getYValue().doubleValue() : 0.0;
                    toolTipController.setData(day, inValue, outValue);
                    popup.show(plotArea, e.getScreenX() + 15, e.getScreenY() + 15);
                }
            } else {
                verticalLine.setVisible(false);
            }
        });

        plotArea.setOnMouseExited(e -> {
            popup.hide();
            verticalLine.setVisible(false);
        });
    }

    private XYChart.Data<String, Number> findData(Series<String, Number> series, String category){
        for(XYChart.Data<String, Number> d : series.getData()){
            if(d.getXValue().equals(category)){
                return d;
            }
        }
        return null;
    }

    private void initMacroDist() {
        macroDistPieChart.setData(macroDistData);
        macroDistPieChart.setLegendVisible(false);
        macroDistInnerHoleCircle.radiusProperty().bind(macroDistPieChart.widthProperty().divide(3.5));

        int userId = SessionManager.getInstance().getCurrentUser().getUid();
        ActivityRepository.MacroData macros = activityRepository.getWeeklyMacros(userId);

        double protein = macros.protein;
        double carbs = macros.carbs;
        double fats = macros.fats;
        double total = protein + carbs + fats;

        if (total > 0) {
            protein = (protein / total) * 100;
            carbs = (carbs / total) * 100;
            fats = (fats / total) * 100;
        }

        updateMacroDist(protein, carbs, fats);
    }

    private void initCaloriesLineChart(){
        xAxis.setCategories(FXCollections.observableArrayList(
                "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"
        ));
        xAxis.setGapStartAndEnd(false);
        xAxis.setTickMarkVisible(false);

        int userId = SessionManager.getInstance().getCurrentUser().getUid();
        Map<String, Double[]> weeklyData = activityRepository.getWeeklyCalories(userId);

        LineChart<String, Number> chart = (LineChart<String, Number>) weeklyChart;
        XYChart.Series<String, Number> calIn = new XYChart.Series<>();
        XYChart.Series<String, Number> calOut = new XYChart.Series<>();

        String[] allDays = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        LocalDate today = LocalDate.now();
        int currentDayIndex = today.getDayOfWeek().getValue() - 1;

        for (int i = 0; i <= currentDayIndex; i++) {
            String day = allDays[i];
            Double[] data = weeklyData.getOrDefault(day, new Double[]{0.0, 0.0});
            calIn.getData().add(new XYChart.Data<>(day, data[0]));
            calOut.getData().add(new XYChart.Data<>(day, data[1]));
        }

        for (int i = currentDayIndex + 1; i < allDays.length; i++) {
            String day = allDays[i];
            calIn.getData().add(new XYChart.Data<>(day, null));
            calOut.getData().add(new XYChart.Data<>(day, null));
        }

        double maxIn = calIn.getData().stream().filter(d -> d.getYValue() != null).mapToDouble(d -> d.getYValue().doubleValue()).max().orElse(0);
        double maxOut = calOut.getData().stream().filter(d -> d.getYValue() != null).mapToDouble(d -> d.getYValue().doubleValue()).max().orElse(0);
        double maxValue = Math.max(maxIn, maxOut);

        double tickUnit = 550;
        double minUpperBound = tickUnit * 4;
        double calculatedBound = Math.ceil((maxValue + maxValue * 0.125) / tickUnit) * tickUnit;
        double upperBound = Math.max(calculatedBound, minUpperBound);

        NumberAxis yAxis = (NumberAxis) chart.getYAxis();
        yAxis.setAutoRanging(false);
        yAxis.setTickUnit(tickUnit);
        yAxis.setLowerBound(0);
        yAxis.setUpperBound(upperBound);

        chart.getData().clear();
        chart.getData().addAll(calIn, calOut);
        chart.setLegendVisible(false);
        setupGlobalTooltip(chart, calIn, calOut);
    }

    private void initDashboardHeader(){
        int userId = SessionManager.getInstance().getCurrentUser().getUid();
        double caloriesIn = mealRepository.getDailyCaloriesIn(userId);
        caloriesInLabel.setText(String.valueOf((int) caloriesIn));

        double caloriesOut = activityRepository.getDailyCaloriesOut(userId);
        caloriesOutLabel.setText(String.valueOf(caloriesOut));

        netCaloriesLabel.setText(String.valueOf((int) (caloriesIn - caloriesOut)));

        int streak = activityRepository.getCurrentStreak(userId);
        activityStreakLabel.setText(String.valueOf(streak));
    }

    private void updateMacroDist(double protein, double carbs, double fats) {
        macroDistData.clear();
        macroDistData.add(new Data("Protein", protein));
        macroDistData.add(new Data("Carbs", carbs));
        macroDistData.add(new Data("Fats", fats));

        proteinLabelMacro.setText(String.format("%.1f%%", protein));
        carbsLabelMacro.setText(String.format("%.1f%%", carbs));
        fatsLabelMacro.setText(String.format("%.1f%%", fats));
    }

    private void initRecentLogs() {
        initRecentFoodLogs();
        initRecentActivityLogs();
    }

    private void initRecentFoodLogs() {
        int userId = SessionManager.getInstance().getCurrentUser().getUid();
        List<Map<String, Object>> foodLogs = activityRepository.getTodayFoodLogs(userId);

        recentFoodLogsContainer.getChildren().remove(1, recentFoodLogsContainer.getChildren().size());

        for (int i = 0; i < foodLogs.size(); i++) {
            Map<String, Object> log = foodLogs.get(i);

            GridPane grid = new GridPane();
            grid.setHgap(10);

            ColumnConstraints col1 = new ColumnConstraints();
            col1.setHgrow(Priority.ALWAYS);
            ColumnConstraints col2 = new ColumnConstraints();
            col2.setHgrow(Priority.NEVER);
            col2.setPrefWidth(80);
            grid.getColumnConstraints().addAll(col1, col2);

            // Row 0: Meal name | Calories
            Label nameLabel = new Label((String) log.get("name"));
            nameLabel.getStyleClass().add("recent-food-name");
            grid.add(nameLabel, 0, 0);

            Label caloriesLabel = new Label(String.valueOf(log.get("total_calories")));
            caloriesLabel.getStyleClass().add("recent-food-calories");
            grid.add(caloriesLabel, 1, 0);
            GridPane.setHalignment(caloriesLabel, HPos.RIGHT);

            // Row 1: Meal type • time | kcal
            Label timeLabel = new Label(log.get("meal_type") + " • " + log.get("log_time"));
            timeLabel.getStyleClass().add("recent-food-time");
            grid.add(timeLabel, 0, 1);

            Label kcalLabel = new Label("kcal");
            kcalLabel.getStyleClass().add("recent-food-kcal");
            grid.add(kcalLabel, 1, 1);
            GridPane.setHalignment(kcalLabel, HPos.RIGHT);

            recentFoodLogsContainer.getChildren().add(grid);

            if (i < foodLogs.size() - 1) {
                recentFoodLogsContainer.getChildren().add(new Separator());
            }
        }
    }

    private void initRecentActivityLogs() {
        int userId = SessionManager.getInstance().getCurrentUser().getUid();
        List<Map<String, Object>> activityLogs = activityRepository.getTodayActivityLogs(userId);

        recentActivityLogsContainer.getChildren().remove(1, recentActivityLogsContainer.getChildren().size());

        for (int i = 0; i < activityLogs.size(); i++) {
            Map<String, Object> log = activityLogs.get(i);

            GridPane grid = new GridPane();
            grid.setHgap(5);

            ColumnConstraints col1 = new ColumnConstraints();
            col1.setHgrow(Priority.ALWAYS);
            ColumnConstraints col2 = new ColumnConstraints();
            col2.setHgrow(Priority.NEVER);
            col2.setPrefWidth(45);
            ColumnConstraints col3 = new ColumnConstraints();
            col3.setHgrow(Priority.NEVER);
            col3.setPrefWidth(45);
            grid.getColumnConstraints().addAll(col1, col2, col3);

            // Row 0: Name | Minutes | Calories
            Label nameLabel = new Label((String) log.get("name"));
            nameLabel.getStyleClass().add("recent-activity-name");
            grid.add(nameLabel, 0, 0);

            Object quantityObj = log.get("quantity");
            int quantity = quantityObj instanceof Number ? ((Number) quantityObj).intValue() : 0;
            Label quantityLabel = new Label(String.valueOf(quantity));
            quantityLabel.getStyleClass().add("recent-activity-quantity");
            grid.add(quantityLabel, 1, 0);
            GridPane.setHalignment(quantityLabel, HPos.CENTER);

            Label caloriesLabel = new Label(String.valueOf(log.get("calories")));
            caloriesLabel.getStyleClass().add("recent-activity-calories");
            grid.add(caloriesLabel, 2, 0);
            GridPane.setHalignment(caloriesLabel, HPos.RIGHT);

            // Row 1: Time | "min" | "kcal"
            Label timeLabel = new Label((String) log.get("log_time"));
            timeLabel.getStyleClass().add("recent-activity-time");
            grid.add(timeLabel, 0, 1);

            Label unitLabel = new Label("min");
            unitLabel.getStyleClass().add("recent-activity-unit");
            grid.add(unitLabel, 1, 1);
            GridPane.setHalignment(unitLabel, HPos.CENTER);

            Label kcalLabel = new Label("kcal");
            kcalLabel.getStyleClass().add("recent-activity-kcal");
            grid.add(kcalLabel, 2, 1);
            GridPane.setHalignment(kcalLabel, HPos.RIGHT);

            recentActivityLogsContainer.getChildren().add(grid);

            if (i < activityLogs.size() - 1) {
                recentActivityLogsContainer.getChildren().add(new Separator());
            }
        }
    }

    @FXML
    private void goToFoodLog(MouseEvent event) {
        MainController main = MainController.getInstance();
        if (main != null) {
            main.navigateToView("food-log-view", "foodLogScrollPane", main.foodLogNav);
        }
    }

    @FXML
    private void goToActivityLog(MouseEvent event) {
        MainController main = MainController.getInstance();
        if (main != null) {
            main.navigateToView("activity-log-view", "activityLogScrollPane", main.activityLogNav);
        }
    }

    @FXML
    private void goToHealthLog(MouseEvent event) {
        MainController main = MainController.getInstance();
        if (main != null) {
            main.navigateToView("health-view", "healthScrollPane", main.healthNav);
        }
    }
}