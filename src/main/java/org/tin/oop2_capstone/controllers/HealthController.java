package org.tin.oop2_capstone.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.PopupControl;
import javafx.scene.layout.VBox;
import org.tin.oop2_capstone.database.RetrieveData;
import org.tin.oop2_capstone.database.repositories.ActivityRepository;
import org.tin.oop2_capstone.database.repositories.MealRepository;
import org.tin.oop2_capstone.database.repositories.UserPrefRepository;
import org.tin.oop2_capstone.database.repositories.UserRepository;
import org.tin.oop2_capstone.model.entities.Meal;
import org.tin.oop2_capstone.model.entities.NutritionDetails;
import org.tin.oop2_capstone.model.entities.User;
import org.tin.oop2_capstone.model.entities.UserPreferences;
import org.tin.oop2_capstone.services.DependencyService;
import org.tin.oop2_capstone.services.SessionManager;

import java.io.IOException;
import java.util.List;


public class HealthController {
    //PROGRESS BARS START
    @FXML private  Label hcaloriesLabel;
    @FXML private ProgressBar caloriesProgressBar;
    @FXML private Label goalCalLabel;
    @FXML private Label caloriesProgressLabel;

    @FXML private Label hcholesterolLabel;
    @FXML private ProgressBar cholesterolProgressBar;
    @FXML private Label goalCholLabel;
    @FXML private Label cholesterolProgressLabel;

    @FXML private Label hproteinLabel;
    @FXML private ProgressBar proteinProgressBar;
    @FXML private Label goalProtLabel;
    @FXML private Label proteinProgressLabel;

    @FXML private Label hsodiumLabel;
    @FXML private ProgressBar sodiumProgressBar;
    @FXML private Label goalSodLabel;
    @FXML private Label sodiumProgressLabel;

    @FXML private Label hfatLabel;
    @FXML private ProgressBar fatProgressBar;
    @FXML private Label goalFatLabel;
    @FXML private Label fatProgressLabel;

    @FXML private Label hsugarLabel;
    @FXML private ProgressBar sugarProgressBar;
    @FXML private Label goalSugarLabel;
    @FXML private Label sugarProgressLabel;

    @FXML private Label hcarbsLabel;
    @FXML private ProgressBar carbsProgressBar;
    @FXML private Label goalCarbsLabel;
    @FXML private Label carbsProgressLabel;

    @FXML private Label hfiberLabel;
    @FXML private ProgressBar fiberProgressBar;
    @FXML private Label goalFiberLabel;
    @FXML private Label fiberProgressLabel;
    //PROGRESS BARS END

    //MICRONUTRIENTS START
    @FXML private ProgressBar mnCholesterolProgressBar;
    @FXML private Label mnCholesterolLabel;
    @FXML private Label mnCholesterolGoal;

    @FXML private ProgressBar mnSodiumProgressBar;
    @FXML private Label mnSodiumLabel;
    @FXML private Label mnSodiumGoal;

    @FXML private ProgressBar mnSugarProgressBar;
    @FXML private Label mnSugarLabel;
    @FXML private Label mnSugarGoal;

    @FXML private ProgressBar mnFiberProgressBar;
    @FXML private Label mnFiberLabel;
    @FXML private Label mnFiberGoal;

    //MICRONUTRIENTS END

    @FXML private PieChart macroDistPieChart;
    @FXML private Circle macroDistInnerHoleCircle;
    @FXML private LineChart<?, ?> weeklyChart;
    @FXML private CategoryAxis xAxis;
    @FXML private NumberAxis yAxis;

    private ObservableList<PieChart.Data> macroDistData;

    @FXML private Label proteinLabelMacro;
    @FXML private Label carbsLabelMacro;
    @FXML private Label fatsLabelMacro;

    @FXML
    private Pane radarChartPane;

    //Monday's index is 0
    double[] dailyCals = new double[7];
    double[] dailyProt = new double[7];
    double[] dailyCarbs = new double[7];
    double[] dailyFats = new double[7];
    double[] dailyChol = new double[7];
    double[] dailySod = new double[7];
    double[] dailySug = new double[7];
    double[] dailyFib = new double[7];

    private UserRepository userRepository;
    private MealRepository mealRepository;
    private ActivityRepository activityRepository;
    private UserPrefRepository userPrefRepository;

    public HealthController() {
        this.userRepository = DependencyService.getUserRepository();
        this.mealRepository = DependencyService.getMealRepository();
        this.activityRepository = DependencyService.getActivityRepository();
        this.userPrefRepository = DependencyService.getUserPrefRepository();
    }


    public void initialize() {
        initCaloriesLineChart();

        //Same Logic in ToolTipController START
        List<Meal> userMealsToday = mealRepository.getUserMealsToday();
        double calories = 0.0,  protein = 0.0, fat = 0.0, cholesterol = 0.0, carbs = 0.0, sodium = 0.0, sugar = 0.0, fiber = 0.0;

        for (Meal m : userMealsToday) {
            NutritionDetails nd = m.getNutritionDetails();
            if (nd != null) {
                calories += nd.getCalories();
                protein += nd.getProtein();
                fat += nd.getFat();
                cholesterol += nd.getCholesterol();
                sodium += nd.getSodium();
                sugar += nd.getSugar();
                fiber += nd.getFiber();
                carbs += nd.getCarbs();
            }
        }
        //Same Logic in ToolTipController END
        User user = SessionManager.getInstance().getCurrentUser();
        UserPreferences userpreferences = SessionManager.getInstance().getCurrentUserPrefs();
        NutritionDetails goals = userpreferences.getTargetMacros(user);

        //Load Values into ProgressBars
        setLabelsAndProgressBars(calories, cholesterol, protein, sodium, fat, sugar, carbs, fiber, goals);
        drawRadarChart(calories/goals.getCalories(), protein/goals.getProtein(), carbs/goals.getCarbs(), fat/goals.getFat(), fiber/goals.getFiber(), sodium/goals.getSodium());

        macroDistData = FXCollections.observableArrayList();
        updateMacroDist(protein, carbs, fat);

        initMacroDist();
    }

    private void setLabelsAndProgressBars(Double calories, Double cholesterol, Double protein, Double sodium, Double fat, Double sugar, Double carbs, Double fiber, NutritionDetails goals) {

        hcaloriesLabel.setText(calories+"");
        goalCalLabel.setText(goals.getCalories()+"");
        caloriesProgressLabel.setText(String.format("%.2f%%", (calories / goals.getCalories() * 100)));
        caloriesProgressBar.setProgress((calories/goals.getCalories()));
        if(caloriesProgressBar.getProgress() > 1) {
            caloriesProgressBar.getStyleClass().add("limitbroken");
        }

        hcholesterolLabel.setText(cholesterol+"");
        goalCholLabel.setText(goals.getCholesterol()+"");
        cholesterolProgressLabel.setText(String.format("%.2f%%", (cholesterol / goals.getCholesterol() * 100)));
        cholesterolProgressBar.setProgress((cholesterol/goals.getCholesterol()));
        if(cholesterolProgressBar.getProgress() > 1) {
            cholesterolProgressBar.getStyleClass().add("limitbroken");
        }

        hproteinLabel.setText(protein+"");
        goalProtLabel.setText(goals.getProtein()+"");
        proteinProgressLabel.setText(String.format("%.2f%%", (protein / goals.getProtein() * 100)));
        proteinProgressBar.setProgress((protein/goals.getProtein()));
        if(proteinProgressBar.getProgress() > 1) {
            proteinProgressBar.getStyleClass().add("limitbroken");
        }
        proteinLabelMacro.setText(protein+"g");

        hsodiumLabel.setText(sodium+"");
        goalSodLabel.setText(goals.getSodium()+"");
        sodiumProgressLabel.setText(String.format("%.2f%%", (sodium / goals.getSodium() * 100)));
        sodiumProgressBar.setProgress((sodium/goals.getSodium()));
        if(sodiumProgressBar.getProgress() > 1) {
            sodiumProgressBar.getStyleClass().add("limitbroken");
        }

        hfatLabel.setText(fat+"");
        goalFatLabel.setText(goals.getFat()+"");
        fatProgressLabel.setText(String.format("%.2f%%", (fat / goals.getFat() * 100)));
        fatProgressBar.setProgress((fat/goals.getFat()));
        if(fatProgressBar.getProgress() > 1) {
            fatProgressBar.getStyleClass().add("limitbroken");
        }
        fatsLabelMacro.setText(fat+"g");

        hsugarLabel.setText(sugar+"");
        goalSugarLabel.setText(goals.getSugar()+"");
        sugarProgressLabel.setText(String.format("%.2f%%", (sugar / goals.getSugar() * 100)));
        sugarProgressBar.setProgress((sugar/goals.getSugar()));
        if(sugarProgressBar.getProgress() > 1) {
            sugarProgressBar.getStyleClass().add("limitbroken");
        }

        hcarbsLabel.setText(carbs+"");
        goalCarbsLabel.setText(goals.getCarbs()+"");
        carbsProgressLabel.setText(String.format("%.2f%%", (carbs / goals.getCarbs() * 100)));
        carbsProgressBar.setProgress((carbs/goals.getCarbs()));
        if(carbsProgressBar.getProgress() > 1) {
            carbsProgressBar.getStyleClass().add("limitbroken");
        }
        carbsLabelMacro.setText(carbs+"g");

        hfiberLabel.setText(fiber+"");
        goalFiberLabel.setText(goals.getFiber()+"");
        fiberProgressLabel.setText(String.format("%.2f%%", (fiber / goals.getFiber() * 100)));
        fiberProgressBar.setProgress((fiber/goals.getFiber()));
        if(fiberProgressBar.getProgress() > 1) {
            fiberProgressBar.getStyleClass().add("limitbroken");
        }

        //MicroNutrients
        mnCholesterolProgressBar.setProgress((cholesterol/goals.getCholesterol()));
        if(mnCholesterolProgressBar.getProgress() > 1) {
            mnCholesterolProgressBar.getStyleClass().add("limitbroken");
        }
        mnCholesterolLabel.setText(cholesterol+"");
        mnCholesterolGoal.setText(goals.getCholesterol()+"");

        mnSodiumProgressBar.setProgress((sodium/goals.getSodium()));
        if(mnSodiumProgressBar.getProgress() > 1) {
            mnSodiumProgressBar.getStyleClass().add("limitbroken");
        }
        mnSodiumLabel.setText(sodium+"");
        mnSodiumGoal.setText(goals.getSodium()+"");

        mnSugarProgressBar.setProgress((sugar/goals.getSugar()));
        if(mnSugarProgressBar.getProgress() > 1) {
            mnSugarProgressBar.getStyleClass().add("limitbroken");
        }
        mnSugarLabel.setText(sugar+"");
        mnSugarGoal.setText(goals.getSugar()+"");

        mnFiberProgressBar.setProgress((fiber/goals.getFiber()));
        if(mnFiberProgressBar.getProgress() > 1) {
            mnFiberProgressBar.getStyleClass().add("limitbroken");
        }
        mnFiberLabel.setText(fiber+"");
        mnFiberGoal.setText(goals.getFiber()+"");

    }

    //For the line chart of Weekly Nutrient Trends
    private void initCaloriesLineChart() {
        xAxis.setCategories(FXCollections.observableArrayList("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"));
        xAxis.setGapStartAndEnd(false);
        xAxis.setTickMarkVisible(false);

        LineChart<String, Number> chart = (LineChart<String, Number>) weeklyChart;
        chart.getData().clear();

        // Clear arrays completely before re-populating from DB
        dailyCals = new double[7];
        dailyProt = new double[7];
        dailyCarbs = new double[7];
        dailyFats = new double[7];
        dailyChol = new double[7];
        dailySod = new double[7];
        dailySug = new double[7];
        dailyFib = new double[7];

        int userId = SessionManager.getInstance().getCurrentUser().getUid();

        List<Meal> weeklyMeals = mealRepository.getWeeklyUserMeals();

        if (weeklyMeals != null) {
            for (Meal m : weeklyMeals) {
                int dayIndex = m.getLogDate().getDayOfWeek().getValue() - 1;
                NutritionDetails nd = m.getConsumable().getNutrition();

                dailyCals[dayIndex]  += nd.getCalories()    ;
                dailyProt[dayIndex]  += nd.getProtein()     ;
                dailyCarbs[dayIndex] += nd.getCarbs()       ;
                dailyFats[dayIndex]  += nd.getFat()         ;
                dailyChol[dayIndex]  += nd.getCholesterol() ;
                dailySod[dayIndex]   += nd.getSodium()      ;
                dailySug[dayIndex]   += nd.getSugar()       ;
                dailyFib[dayIndex]   += nd.getFiber()       ;
            }
        }

        //Only plot up to today
        int todayIndex = java.time.LocalDate.now().getDayOfWeek().getValue() - 1;

        XYChart.Series<String, Number> calIn = new XYChart.Series<>();
        calIn.setName("Calories");

        XYChart.Series<String, Number> protIn = new XYChart.Series<>();
        protIn.setName("Protein");

        XYChart.Series<String, Number> carbIn = new XYChart.Series<>();
        carbIn.setName("Carbs");

        XYChart.Series<String, Number> fatIn = new XYChart.Series<>();
        fatIn.setName("Fats");

        XYChart.Series<String, Number> cholesterolIn = new XYChart.Series<>();
        cholesterolIn.setName("Cholesterol");

        XYChart.Series<String, Number> sodiumIn = new XYChart.Series<>();
        sodiumIn.setName("Sodium");

        XYChart.Series<String, Number> sugarIn = new XYChart.Series<>();
        sugarIn.setName("Sugar");

        XYChart.Series<String, Number> fiberIn = new XYChart.Series<>();
        fiberIn.setName("Fiber");

        //Nutrient populate-r
        String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        for (int i = 0; i < 7; i++) {
            calIn.getData().add(new XYChart.Data<>(days[i], dailyCals[i]));
            protIn.getData().add(new XYChart.Data<>(days[i], dailyProt[i]));
            carbIn.getData().add(new XYChart.Data<>(days[i], dailyCarbs[i]));
            fatIn.getData().add(new XYChart.Data<>(days[i], dailyFats[i]));
            cholesterolIn.getData().add(new XYChart.Data<>(days[i], dailyChol[i]));
            sodiumIn.getData().add(new XYChart.Data<>(days[i], dailySod[i]));
            sugarIn.getData().add(new XYChart.Data<>(days[i], dailySug[i]));
            fiberIn.getData().add(new XYChart.Data<>(days[i], dailyFib[i]));
        }

        NumberAxis yAxis = (NumberAxis) chart.getYAxis();
        yAxis.setAutoRanging(true);

        chart.getData().addAll(calIn, protIn, carbIn, fatIn, cholesterolIn, sugarIn, sodiumIn, fiberIn);

        chart.setLegendVisible(true);

        setupGlobalTooltip(chart);
    }

    private void setupGlobalTooltip(LineChart<String, Number> chart) {
        PopupControl popup = new PopupControl();
        ToolTipController toolTipController;

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/tin/oop2_capstone/views/tool-tip-view.fxml"));
            VBox root = fxmlLoader.load();
            popup.getScene().getStylesheets().add(getClass().getResource("/org/tin/oop2_capstone/styles/application.css").toExternalForm());
            root.getStyleClass().add("light");
            popup.getScene().setRoot(root);
            toolTipController = fxmlLoader.getController();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
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
                verticalLine.setStartY(chartContent.sceneToLocal(plotArea.localToScene(0, 0)).getY());
                verticalLine.setEndX(chartPoint.getX());
                verticalLine.setEndY(chartContent.sceneToLocal(plotArea.localToScene(0, plotArea.getBoundsInLocal().getHeight())).getY());

                // Find which day index (0=Mon ... 6=Sun) is being hovered
                int idx = java.util.Arrays.asList(days).indexOf(day);
                if (idx >= 0) {
                    toolTipController.setData(
                            day,
                            dailyCals[idx],
                            dailyProt[idx],
                            dailyCarbs[idx],
                            dailyFats[idx],
                            dailyChol[idx],
                            dailySod[idx],
                            dailySug[idx],
                            dailyFib[idx]
                    );
                    popup.show(plotArea, e.getScreenX() + 15, e.getScreenY() + 15);
                }
            } else {
                verticalLine.setVisible(false);
                popup.hide();
            }
        });

        plotArea.setOnMouseExited(e -> {
            popup.hide();
            verticalLine.setVisible(false);
        });
    }

    private XYChart.Data<String, Number> findData(XYChart.Series<String, Number> series, String category) {
        for (XYChart.Data<String, Number> d : series.getData()) {
            if (d.getXValue().equals(category)) {
                return d;
            }
        }
        return null;
    }

    //Draws the Radar Chart for Nutrient Balance Overview
    private void drawRadarChart(double calories, double protein, double carbs, double fat, double fiber, double sodium) {
        // Clear out the old radar web and polygons before drawing the fresh ones
        radarChartPane.getChildren().clear();

        double[] dataValues = {calories, protein, carbs, fat, fiber, sodium};
        //Avoid OverDrawing
        for(int i=0; i<dataValues.length; i++){
            if(dataValues[i] > 1){
                dataValues[i] = 1;
            }
        }
        String[] categories = {"Calories", "Protein", "Carbs", "Fat", "Fiber", "Sodium"};

        // 2. Chart dimensions
        double centerX = 150; // Half of prefWidth
        double centerY = 125; // Half of prefHeight
        double maxRadius = 90; // Size of the chart

        // 3. Draw the background "Web" (4 concentric hexagons)
        for (int i = 1; i <= 4; i++) {
            double currentRadius = maxRadius * (i / 4.0);
            Polygon backgroundHex = createHexagon(centerX, centerY, currentRadius, new double[]{1, 1, 1, 1, 1, 1});
            backgroundHex.setFill(Color.TRANSPARENT);
            backgroundHex.setStroke(Color.web("#E2E8F0")); // Light grey web lines
            radarChartPane.getChildren().add(backgroundHex);
        }

        // 4. Draw the axes (lines from center to edges) and Labels
        for (int i = 0; i < 6; i++) {
            // Calculate angle for 6 points (Starts at Top, moves clockwise)
            double angle = Math.PI / 2 + (i * 2 * Math.PI / 6);

            // End point for the axis line
            double x = centerX - maxRadius * Math.cos(angle);
            double y = centerY - maxRadius * Math.sin(angle);

            Line axisLine = new Line(centerX, centerY, x, y);
            axisLine.setStroke(Color.web("#E2E8F0"));
            radarChartPane.getChildren().add(axisLine);

            // Add the category text labels slightly outside the max radius
            Text label = new Text(categories[i]);
            label.setFill(Color.web("#64748B")); // Matches your lightText style

            // Adjust label position so it doesn't overlap the lines
            double textOffset = label.getLayoutBounds().getWidth() / 2;
            label.setX(centerX - (maxRadius + 20) * Math.cos(angle) - textOffset);
            label.setY(centerY - (maxRadius + 15) * Math.sin(angle) + 4);

            radarChartPane.getChildren().add(label);
        }

        // 5. Draw the actual Data Polygon (The Blue Shape)
        Polygon dataPolygon = createHexagon(centerX, centerY, maxRadius, dataValues);
        dataPolygon.setFill(Color.web("#61A5FA", 0.5)); // Semi-transparent blue fill
        dataPolygon.setStroke(Color.web("#2663EB"));    // Solid blue border
        dataPolygon.setStrokeWidth(2.0);

        radarChartPane.getChildren().add(dataPolygon);
    }

    //Helper for DrawRadarChart
    private Polygon createHexagon(double cx, double cy, double maxR, double[] values) {
        Polygon polygon = new Polygon();
        for (int i = 0; i < 6; i++) {
            double angle = Math.PI / 2 + (i * 2 * Math.PI / 6);
            double pointRadius = maxR * values[i]; // Scale radius by data percentage

            double x = cx - pointRadius * Math.cos(angle);
            double y = cy - pointRadius * Math.sin(angle);

            polygon.getPoints().addAll(x, y);
        }
        return polygon;
    }

    //For the Macro Distribution
    private void initMacroDist() {
        macroDistPieChart.setData(macroDistData);
        macroDistPieChart.setLegendVisible(false);
        macroDistInnerHoleCircle.radiusProperty().bind(macroDistPieChart.widthProperty().divide(3.5));
    }

    private void updateMacroDist(double protein, double carbs, double fats) {
        macroDistData.clear();
        macroDistData.add(new PieChart.Data("Protein", protein));
        macroDistData.add(new PieChart.Data("Carbs", carbs));
        macroDistData.add(new PieChart.Data("Fats", fats));

        proteinLabelMacro.setText(String.format("%.2fg", protein));
        carbsLabelMacro.setText(String.format("%.2fg", carbs));
        fatsLabelMacro.setText(String.format("%.2fg", fats));
    }

}