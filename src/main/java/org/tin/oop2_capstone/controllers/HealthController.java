package org.tin.oop2_capstone.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
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
import java.io.IOException;



public class HealthController {
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
        //(Values should be between 0.0 and 1.0 representing 0% to 100%)
        drawRadarChart(0.85, 0.50, 0.75, 0.90, 0.40, 0.60); //TODO: change according to actual progress
        macroDistData = FXCollections.observableArrayList();
        updateMacroDist(35.0, 95.0, 28.0);//TODO: change according to actual progress
        initMacroDist();
    }

    //For the line chart of Weekly Nutrient Trends
    private void initCaloriesLineChart() {
        xAxis.setCategories(FXCollections.observableArrayList(
                "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"
        ));
        xAxis.setGapStartAndEnd(false);
        xAxis.setTickMarkVisible(false);

        LineChart<String, Number> chart = (LineChart<String, Number>) weeklyChart;

        XYChart.Series<String, Number> calIn = new XYChart.Series<>();
        calIn.getData().addAll(
                new XYChart.Data<>("Mon", 1850),
                new XYChart.Data<>("Tue", 2150),
                new XYChart.Data<>("Wed", 1950),
                new XYChart.Data<>("Thu", 2050),
                new XYChart.Data<>("Fri", 1900),
                new XYChart.Data<>("Sat", 2200),
                new XYChart.Data<>("Sun", 770)
        );

        NumberAxis yAxis = (NumberAxis) chart.getYAxis();
        yAxis.setAutoRanging(false);
        yAxis.setTickUnit(550);
        yAxis.setLowerBound(0);
        yAxis.setUpperBound(2500);
        yAxis.setMinorTickCount(0);

        chart.getData().addAll(calIn);

        chart.setLegendVisible(false);

        setupGlobalTooltip(chart, calIn);
    }

    private void setupGlobalTooltip(LineChart<String, Number> chart, XYChart.Series<String, Number> in) {
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