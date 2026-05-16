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
import org.tin.oop2_capstone.database.repositories.MealRepository;
import org.tin.oop2_capstone.model.entities.Meal;
import org.tin.oop2_capstone.model.entities.NutritionDetails;

import java.io.IOException;
import java.util.List;


public class HealthController {
    //PROGRESS BARS START
    @FXML Label caloriesLabel;
    @FXML ProgressBar caloriesProgressBar;
    @FXML Label goalCalLabel;
    @FXML Label caloriesProgressLabel;

    @FXML Label cholesterolLabel;
    @FXML ProgressBar cholesterolProgressBar;
    @FXML Label goalCholLabel;
    @FXML Label cholesterolProgressLabel;

    @FXML Label proteinLabel;
    @FXML ProgressBar proteinProgressBar;
    @FXML Label goalProtLabel;
    @FXML Label proteinProgressLabel;

    @FXML Label sodiumLabel;
    @FXML ProgressBar sodiumProgressBar;
    @FXML Label goalSodLabel;
    @FXML Label sodiumProgressLabel;

    @FXML Label fatLabel;
    @FXML ProgressBar fatProgressBar;
    @FXML Label goalFatLabel;
    @FXML Label fatProgressLabel;

    @FXML Label sugarLabel;
    @FXML ProgressBar sugarProgressBar;
    @FXML Label goalSugarLabel;
    @FXML Label sugarProgressLabel;

    @FXML Label carbsLabel;
    @FXML ProgressBar carbsProgressBar;
    @FXML Label goalCarbsLabel;
    @FXML Label carbsProgressLabel;

    @FXML Label fiberLabel;
    @FXML ProgressBar fiberProgressBar;
    @FXML Label goalFiberLabel;
    @FXML Label fiberProgressLabel;
    //PROGRESS BARS END

    //MICRONUTRIENTS START
    @FXML ProgressBar mnCholesterolProgressBar;
    @FXML Label mnCholesterolLabel;
    @FXML Label mnCholesterolGoal;

    @FXML ProgressBar mnSodiumProgressBar;
    @FXML Label mnSodiumLabel;
    @FXML Label mnSodiumGoal;

    @FXML ProgressBar mnSugarProgressBar;
    @FXML Label mnSugarLabel;
    @FXML Label mnSugarGoal;

    @FXML ProgressBar mnFiberProgressBar;
    @FXML Label mnFiberLabel;
    @FXML Label mnFiberGoal;

    //MICRONUTRIENTS END

    @FXML PieChart macroDistPieChart;
    @FXML Circle macroDistInnerHoleCircle;
    @FXML private LineChart<?, ?> weeklyChart;
    @FXML private CategoryAxis xAxis;
    @FXML private NumberAxis yAxis;

    ObservableList<PieChart.Data> macroDistData;

    @FXML private Label proteinLabelMacro;
    @FXML private Label carbsLabelMacro;
    @FXML private Label fatsLabelMacro;

    @FXML
    private Pane radarChartPane;

    public void initialize() {
        initCaloriesLineChart();

        //Same Logic in ToolTipController START
        List<Meal> userMeals = MealRepository.getInstance().getUserMeals();
        double calories = 0.0,  protein = 0.0, fat = 0.0, cholesterol = 0.0, carbs = 0.0, sodium = 0.0, sugar = 0.0, fiber = 0.0;

        for (Meal m : userMeals) {
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

        //Load Values into ProgressBars
        setLabelsAndProgressBars(calories, cholesterol, protein, sodium, fat, sugar, carbs, fiber);
        drawRadarChart(calories/1000, protein/1000, carbs/1000, fat/1000, fiber/1000, sodium/1000);//TODO Change the "1000" to their respective limits e.g. calories/calorielimit, protein/proteinlimit, ...
        macroDistData = FXCollections.observableArrayList();
        updateMacroDist(protein/1000, carbs/1000, fat/1000); //TODO Change the "1000" to their respective limits e.g. calories/calorielimit, protein/proteinlimit, ...

        //Note: Actual Values to be shown
        //(Values should be between 0.0 and 1.0 representing 0% to 100% with "100%" being the goal. e.g. if protein_intake = 24g and goal_protein_intake = 67g then do protein_intake//goal_protein_intake; but add limiter to "'"1.00"'" value (100%))

        initMacroDist();
    }

    private void setLabelsAndProgressBars(Double calories, Double cholesterol, Double protein, Double sodium, Double fat, Double sugar, Double carbs, Double fiber) {
        Double universalGOAL = 1000.00; //TODO: Change the actual Goal to the REAL GOAL for each macros Through Calculations based on user GOAL e.g. Weight Loss, Weight Gain etc.

        caloriesLabel.setText(calories+"");
        goalCalLabel.setText(universalGOAL+"");
        caloriesProgressLabel.setText(String.format("%.2f%%", (calories / universalGOAL * 100)));
        caloriesProgressBar.setProgress((calories/universalGOAL));

        cholesterolLabel.setText(cholesterol+"");
        goalCholLabel.setText(universalGOAL+"");
        cholesterolProgressLabel.setText(String.format("%.2f%%", (cholesterol / universalGOAL * 100)));
        cholesterolProgressBar.setProgress((cholesterol/universalGOAL));

        proteinLabel.setText(protein+"");
        goalProtLabel.setText(universalGOAL+"");
        proteinProgressLabel.setText(String.format("%.2f%%", (protein / universalGOAL * 100)));
        proteinProgressBar.setProgress((protein/universalGOAL));

        sodiumLabel.setText(sodium+"");
        goalSodLabel.setText(universalGOAL+"");
        sodiumProgressLabel.setText(String.format("%.2f%%", (sodium / universalGOAL * 100)));
        sodiumProgressBar.setProgress((sodium/universalGOAL));

        fatLabel.setText(fat+"");
        goalFatLabel.setText(universalGOAL+"");
        fatProgressLabel.setText(String.format("%.2f%%", (fat / universalGOAL * 100)));
        fatProgressBar.setProgress((fat/universalGOAL));

        sugarLabel.setText(sugar+"");
        goalSugarLabel.setText(universalGOAL+"");
        sugarProgressLabel.setText(String.format("%.2f%%", (sugar / universalGOAL * 100)));
        sugarProgressBar.setProgress((sugar/universalGOAL));

        carbsLabel.setText(carbs+"");
        goalCarbsLabel.setText(universalGOAL+"");
        carbsProgressLabel.setText(String.format("%.2f%%", (carbs / universalGOAL * 100)));
        carbsProgressBar.setProgress((carbs/universalGOAL));

        fiberLabel.setText(fiber+"");
        goalFiberLabel.setText(universalGOAL+"");
        fiberProgressLabel.setText(String.format("%.2f%%", (fiber / universalGOAL * 100)));
        fiberProgressBar.setProgress((fiber/universalGOAL));

        //MicroNutrients
        mnCholesterolProgressBar.setProgress((cholesterol/universalGOAL));
        mnCholesterolLabel.setText(cholesterol+"");
        //mnCholesterolGoal.setText(); //TODO: setGoalText

        mnSodiumProgressBar.setProgress((sodium/universalGOAL));
        mnSodiumLabel.setText(sodium+"");
        //mnSodiumGoal.setText(); //TODO: setGoalText

        mnSugarProgressBar.setProgress((sugar/universalGOAL));
        mnSugarLabel.setText(sugar+"");
        //mnSugarGoal.setText(); //TODO: setGoalText

        mnFiberProgressBar.setProgress((fiber/universalGOAL));
        mnFiberLabel.setText(fiber+"");
        //mnFiberGoal.setText(); //TODO: setGoalText

    }

    //For the line chart of Weekly Nutrient Trends
    private void initCaloriesLineChart() {
        xAxis.setCategories(FXCollections.observableArrayList(
                "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"
        ));
        xAxis.setGapStartAndEnd(false);
        xAxis.setTickMarkVisible(false);

        LineChart<String, Number> chart = (LineChart<String, Number>) weeklyChart;

        //TODO: Change hardcoded lines to actual data.
        //Calorie Line
//        XYChart.Series<String, Number> calIn = new XYChart.Series<>();
//        calIn.getData().addAll(
//                new XYChart.Data<>("Mon", 1850),
//                new XYChart.Data<>("Tue", 2150),
//                new XYChart.Data<>("Wed", 1950),
//                new XYChart.Data<>("Thu", 2050),
//                new XYChart.Data<>("Fri", 1900),
//                new XYChart.Data<>("Sat", 2200),
//                new XYChart.Data<>("Sun", 770)
//        );

        //Protein Line
        //TODO: Change hardcoded lines to actual data.
        XYChart.Series<String, Number> protIn = new XYChart.Series<>();
        protIn.setName("Protein");
        protIn.getData().addAll(
                new XYChart.Data<>("Mon", 120),
                new XYChart.Data<>("Tue", 130),
                new XYChart.Data<>("Wed", 115),
                new XYChart.Data<>("Thu", 125),
                new XYChart.Data<>("Fri", 110),
                new XYChart.Data<>("Sat", 140),
                new XYChart.Data<>("Sun", 45)
        );

        //Carbs Line
        //TODO: Change hardcoded lines to actual data.
        XYChart.Series<String, Number> carbIn = new XYChart.Series<>();
        carbIn.setName("Carbs");
        carbIn.getData().addAll(
                new XYChart.Data<>("Mon", 220),
                new XYChart.Data<>("Tue", 240),
                new XYChart.Data<>("Wed", 210),
                new XYChart.Data<>("Thu", 230),
                new XYChart.Data<>("Fri", 200),
                new XYChart.Data<>("Sat", 250),
                new XYChart.Data<>("Sun", 95)
        );

        //Fats Line
        //TODO: Change hardcoded lines to actual data.
        XYChart.Series<String, Number> fatIn = new XYChart.Series<>();
        fatIn.setName("Fats");
        fatIn.getData().addAll(
                new XYChart.Data<>("Mon", 60),
                new XYChart.Data<>("Tue", 70),
                new XYChart.Data<>("Wed", 65),
                new XYChart.Data<>("Thu", 68),
                new XYChart.Data<>("Fri", 62),
                new XYChart.Data<>("Sat", 75),
                new XYChart.Data<>("Sun", 28)
        );

        // Cholesterol
        //TODO: Change hardcoded lines to actual data.
        XYChart.Series<String, Number> cholesterolIn = new XYChart.Series<>();
        cholesterolIn.setName("Cholesterol");
        cholesterolIn.getData().addAll(
                new XYChart.Data<>("Mon", 60),
                new XYChart.Data<>("Tue", 70),
                new XYChart.Data<>("Wed", 65),
                new XYChart.Data<>("Thu", 68),
                new XYChart.Data<>("Fri", 62),
                new XYChart.Data<>("Sat", 75),
                new XYChart.Data<>("Sun", 28)
        );

        // just to see how gubot it will all look

        XYChart.Series<String, Number> sodiumIn = new XYChart.Series<>();
        sodiumIn.setName("Sodium");
        sodiumIn.getData().addAll(
                new XYChart.Data<>("Mon", 87),
                new XYChart.Data<>("Tue", 34),
                new XYChart.Data<>("Wed", 51),
                new XYChart.Data<>("Thu", 75),
                new XYChart.Data<>("Fri", 97),
                new XYChart.Data<>("Sat", 32),
                new XYChart.Data<>("Sun", 88)
        );


        XYChart.Series<String, Number> sugarIn = new XYChart.Series<>();
        sugarIn.setName("Sugar");
        sugarIn.getData().addAll(
                new XYChart.Data<>("Mon", 14),
                new XYChart.Data<>("Tue", 85),
                new XYChart.Data<>("Wed", 45),
                new XYChart.Data<>("Thu", 28),
                new XYChart.Data<>("Fri", 32),
                new XYChart.Data<>("Sat", 90),
                new XYChart.Data<>("Sun", 87)
        );


        XYChart.Series<String, Number> fiberIn = new XYChart.Series<>();
        fiberIn.setName("Fiber");
        fiberIn.getData().addAll(
                new XYChart.Data<>("Mon", 76),
                new XYChart.Data<>("Tue", 90),
                new XYChart.Data<>("Wed", 38),
                new XYChart.Data<>("Thu", 64),
                new XYChart.Data<>("Fri", 82),
                new XYChart.Data<>("Sat", 74),
                new XYChart.Data<>("Sun", 38)
        );


        NumberAxis yAxis = (NumberAxis) chart.getYAxis();
        yAxis.setAutoRanging(false);
        yAxis.setTickUnit(50);
        yAxis.setLowerBound(0);
        yAxis.setUpperBound(300);
        yAxis.setMinorTickCount(10);

        chart.getData().addAll(protIn, carbIn, fatIn, cholesterolIn, sugarIn, sodiumIn, fiberIn);
        //setupGlobalTooltip(chart, calIn, calOut); //TODO: Setup calIn and calOut
        chart.setLegendVisible(true);

    }

    private void setupGlobalTooltip(LineChart<String, Number> chart, XYChart.Series<String, Number> in) { //TODO: Setup
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

                XYChart.Data<String, Number> inData = findData(in, day);

                if (inData != null) {
                    // Pass 0 for the outData since this chart only tracks one series
                    toolTipController.setData(day, inData.getYValue(), 0);
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
        double[] dataValues = {calories, protein, carbs, fat, fiber, sodium};
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

        proteinLabelMacro.setText(String.format("%.0fg", protein));
        carbsLabelMacro.setText(String.format("%.0fg", carbs));
        fatsLabelMacro.setText(String.format("%.0fg", fats));
    }

}