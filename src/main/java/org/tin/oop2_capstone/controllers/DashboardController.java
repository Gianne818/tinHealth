package org.tin.oop2_capstone.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.chart.XYChart.Series;
import  javafx.scene.chart.PieChart.Data;
import javafx.scene.shape.Circle;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javafx.scene.shape.Line;
import org.tin.oop2_capstone.database.repositories.ActivityRepository;
import org.tin.oop2_capstone.database.repositories.MealRepository;
import org.tin.oop2_capstone.database.repositories.UserPrefRepository;
import org.tin.oop2_capstone.database.repositories.UserRepository;
import org.tin.oop2_capstone.model.entities.*;
import org.tin.oop2_capstone.services.DependencyService;
import org.tin.oop2_capstone.services.SessionManager;
import org.tin.oop2_capstone.utils.TimeFormatter;


public class DashboardController {
    @FXML
    private ScrollPane dashboardScrollPane;
    @FXML
    private LineChart<?, ?> weeklyChart;
    @FXML
    private CategoryAxis xAxis;
    @FXML
    private NumberAxis yAxis;

    @FXML
    private PieChart macroDistPieChart;
    @FXML
    private Circle macroDistInnerHoleCircle;

    private ObservableList<PieChart.Data> macroDistData;

    @FXML
    private Label proteinLabelMacro;
    @FXML
    private Label carbsLabelMacro;
    @FXML
    private Label fatsLabelMacro;

    @FXML
    private Label caloriesInLabel;
    @FXML
    private Label caloriesOutLabel;
    @FXML
    private Label netCaloriesLabel;
    @FXML
    private Label activityStreakLabel;

    @FXML
    private VBox recentFoodLogsContainer;
    @FXML
    private VBox recentActivityLogsContainer;

    @FXML
    private Label goalCaloriesLabelCaloriesIn;
    @FXML
    private Label numActLabelCaloriesBurned;
    @FXML
    private Label daysInARowHeader;

    @FXML
    private ListView<GridPane> recentFoodsListView;
    @FXML
    private ListView<GridPane> recentActivityListView;

    private List<Meal> mealsList;
    private List<Activity> activityList;
    private ObservableList<GridPane> mealGridPanes;
    private ObservableList<GridPane> activityGridPanes;

    private NutritionDetails nutritionDetails;
    private int userId;

    private UserRepository userRepository;
    private MealRepository mealRepository;
    private ActivityRepository activityRepository;
    private UserPrefRepository userPrefRepository;

    public DashboardController() {
        this.userRepository = DependencyService.getUserRepository();
        this.mealRepository = DependencyService.getMealRepository();
        this.activityRepository = DependencyService.getActivityRepository();
        this.userPrefRepository = DependencyService.getUserPrefRepository();
    }

    public void initialize() {
        userId = SessionManager.getInstance().getCurrentUser().getUid();
        dashboardScrollPane.getStyleClass().add(getThemeClass());
        macroDistData = FXCollections.observableArrayList();
        nutritionDetails = activityRepository.getWeeklyNutrients(userId);

        mealsList = new ArrayList<>();
        mealGridPanes = FXCollections.observableArrayList();

        activityGridPanes = FXCollections.observableArrayList();

        initRecentActivitiesList();
        initRecentMealsList();
        initDashboardHeader();
        initMacroDist();
        initCaloriesLineChart();
    }

    private void initRecentMealsList(){
        // Clear the old layout panes before fetching updated data
        mealGridPanes.clear();
        mealsList = mealRepository.getUserMeals();

        for(Meal a : mealsList){
            try{
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/tin/oop2_capstone/views/log-card.fxml"));
                GridPane root = fxmlLoader.load();
                root.getStylesheets().add(getClass().getResource("/org/tin/oop2_capstone/styles/application.css").toExternalForm());
                root.getStyleClass().addAll(getThemeClass(), "foodLogScrollPane");
                root.getStyleClass().remove("cardContent");

                LogCardController logCardController = fxmlLoader.getController();
                logCardController.setData(a.getConsumable().getName(), a.getTime(), 0.0, "", a.getNutritionDetails().getCalories(), true, false, null);
                root.setPadding(new Insets(0, 0, 0, 0));
                mealGridPanes.add(root);
            } catch (IOException e) {
                System.out.println("OH NNOI");
                e.printStackTrace();
            }
        }

        recentFoodsListView.setItems(mealGridPanes);
    }

    private void initRecentActivitiesList(){
        // Clear the old layout panes before fetching updated data
        activityGridPanes.clear();
        activityList = activityRepository.getUserActivities();
        for(Activity a : activityList){
            try{
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/tin/oop2_capstone/views/log-card.fxml"));
                GridPane root = fxmlLoader.load();
                root.getStylesheets().add(getClass().getResource("/org/tin/oop2_capstone/styles/application.css").toExternalForm());
                root.getStyleClass().addAll(getThemeClass(), "activityLogScrollPane");
                root.getStyleClass().remove("cardContent");

                LogCardController logCardController = fxmlLoader.getController();
                logCardController.setData(a.getActivityType().getName(), TimeFormatter.formatTo12Hour(a.getLogDateTime().toLocalTime()), a.getQuantity(), a.getUnit(), a.getCalories(), true, false, null);                root.setPadding(new Insets(0, 0, 0, 0));
                activityGridPanes.add(root);
            } catch (IOException e) {
                System.out.println("OH NNOI");
                e.printStackTrace();
            }
        }

        recentActivityListView.setItems(activityGridPanes);
    }

    private void setupGlobalTooltip(LineChart<String, Number> chart, Series<String, Number> in, Series<String, Number> out) {
        PopupControl popup = new PopupControl();
        ToolTipController toolTipController;

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/tin/oop2_capstone/views/tool-tip-view.fxml"));
            VBox root = fxmlLoader.load();
            popup.getScene().getStylesheets().add(getClass().getResource("/org/tin/oop2_capstone/styles/application.css").toExternalForm());
            root.getStyleClass().add(getThemeClass());
            popup.getScene().setRoot(root);
            toolTipController = fxmlLoader.getController();
        } catch (IOException e) {
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

    private XYChart.Data<String, Number> findData(Series<String, Number> series, String category) {
        for (XYChart.Data<String, Number> d : series.getData()) {
            if (d.getXValue().equals(category)) {
                return d;
            }
        }
        return null;
    }

    private void initMacroDist() {
        macroDistPieChart.setData(macroDistData);
        macroDistPieChart.setLegendVisible(false);
        macroDistInnerHoleCircle.radiusProperty().bind(macroDistPieChart.widthProperty().divide(3.5));

        // Prevent crash if user has no meals logged yet
        if (nutritionDetails == null) {
            updateMacroDist(0, 0, 0);
            return;
        }

        double protein = nutritionDetails.getProtein();
        double carbs = nutritionDetails.getCarbs();
        double fats = nutritionDetails.getFat();
        double total = protein + carbs + fats;

        if (total > 0) {
            protein = (protein / total) * 100;
            carbs = (carbs / total) * 100;
            fats = (fats / total) * 100;
        }

        updateMacroDist(protein, carbs, fats);
    }

    private void initCaloriesLineChart() {
        xAxis.setCategories(FXCollections.observableArrayList(
                "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"
        ));
        xAxis.setGapStartAndEnd(false);
        xAxis.setTickMarkVisible(false);

        Map<String, Double[]> weeklyData = activityRepository.getWeeklyCalories(userId);

        LineChart<String, Number> chart = (LineChart<String, Number>) weeklyChart;
        XYChart.Series<String, Number> calIn = new XYChart.Series<>();
        XYChart.Series<String, Number> calOut = new XYChart.Series<>();

        String[] allDays = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        LocalDate today = LocalDate.now();
        int currentDayIndex = today.getDayOfWeek().getValue() - 1;

        for (int i = 0; i <= currentDayIndex; i++) {
            String day = allDays[i];
            Double[] data = weeklyData.get(day);
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

    private void initDashboardHeader() {
        int userId = SessionManager.getInstance().getCurrentUser().getUid();
        double caloriesIn = mealRepository.getTodayCaloriesIn(userId);
        caloriesInLabel.setText(String.valueOf((int) caloriesIn));

        double caloriesOut = activityRepository.getTodayCaloriesOut(userId);
        caloriesOutLabel.setText(String.valueOf(caloriesOut));

        netCaloriesLabel.setText(String.valueOf((int) (caloriesIn - caloriesOut)));

        int streak = activityRepository.getCurrentStreak(userId);
        activityStreakLabel.setText(String.valueOf(streak));

        int dailyCalorieInGoal = userPrefRepository.getDailyCalorieInGoal(userId);
        goalCaloriesLabelCaloriesIn.setText(String.valueOf(dailyCalorieInGoal));

        int todayActivitiesCount = activityRepository.getTodayActivitiesCount(userId);
        numActLabelCaloriesBurned.setText(todayActivitiesCount + (todayActivitiesCount <= 1 ? " Activity" : " Activities"));

        String daysStringDisplay = (streak <= 1 ? "day" : "days in a row");
        daysInARowHeader.setText(daysStringDisplay);
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


    @FXML
    private void goToView(MouseEvent event) {
        MainController main = MainController.getInstance();
        main.onNavElementClicked(event);
    }

    private String getThemeClass() {
        UserPreferences prefs = SessionManager.getInstance().getCurrentUserPrefs();
        if (prefs != null && "Dark".equalsIgnoreCase(prefs.getTheme())) {
            return "dark";
        }
        return "light";
    }
}
