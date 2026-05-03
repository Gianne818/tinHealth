package org.tin.oop2_capstone.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.control.PopupControl;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.chart.XYChart.Series;
import  javafx.scene.chart.PieChart.Data;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import java.io.IOException;
import javafx.scene.shape.Line;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;

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

    public void initialize() {
        dashboardScrollPane.getStyleClass().add("light");
        macroDistData = FXCollections.observableArrayList();

        // todo: use real data for all the charts
        updateMacroDist(50.0, 30.0, 20.0);

        initCaloriesLineChart();
        initMacroDist();
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

                if (inData != null && outData != null){
                    toolTipController.setData(day, inData.getYValue(), outData.getYValue());
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

    private void initCaloriesLineChart(){
        xAxis.setCategories(FXCollections.observableArrayList(
                "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"
        ));
        xAxis.setGapStartAndEnd(false);
        xAxis.setTickMarkVisible(false);

        LineChart<String, Number> chart = (LineChart<String, Number>) weeklyChart;

        XYChart.Series<String, Number> calIn = new XYChart.Series<>();
        calIn.getData().addAll(
                new XYChart.Data<>("Mon", 1850),
                new XYChart.Data<>("Tue", 2100),
                new XYChart.Data<>("Wed", 1950),
                new XYChart.Data<>("Thu", 2050),
                new XYChart.Data<>("Fri", 1900),
                new XYChart.Data<>("Sat", 2200),
                new XYChart.Data<>("Sun", 770)
        );

        XYChart.Series<String, Number> calOut = new XYChart.Series<>();
        calOut.getData().addAll(
                new XYChart.Data<>("Mon", 420),
                new XYChart.Data<>("Tue", 380),
                new XYChart.Data<>("Wed", 450),
                new XYChart.Data<>("Thu", 400),
                new XYChart.Data<>("Fri", 520),
                new XYChart.Data<>("Sat", 350),
                new XYChart.Data<>("Sun", 465)
        );

        // Find max value from both series
        double maxIn = calIn.getData().stream().mapToDouble(d -> d.getYValue().doubleValue()).max().orElse(0);
        double maxOut = calOut.getData().stream().mapToDouble(d -> d.getYValue().doubleValue()).max().orElse(0);
        double maxValue = Math.max(maxIn, maxOut);

        // Calculate upper bound rounded up to nearest 550
        double upperBound = Math.ceil((maxValue + maxValue * 0.125) / 550) * 550;

        // Only 5 yAxis Labels
        NumberAxis yAxis = (NumberAxis) chart.getYAxis();
        yAxis.setAutoRanging(false);
        yAxis.setTickUnit(550);
        yAxis.setLowerBound(0);
        yAxis.setUpperBound(upperBound);
        yAxis.setMinorTickCount(0);

        chart.getData().addAll(calIn, calOut);

        chart.setLegendVisible(false);
        setupGlobalTooltip(chart, calIn, calOut);
    }
}