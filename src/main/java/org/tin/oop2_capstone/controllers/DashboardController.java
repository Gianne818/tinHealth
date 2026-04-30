package org.tin.oop2_capstone.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.PopupControl;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.util.StringConverter;

public class DashboardController {
    @FXML ScrollPane dashboardScrollPane;
    @FXML private LineChart<?, ?> weeklyChart;
    @FXML private CategoryAxis xAxis;

    public void initialize() {
        dashboardScrollPane.getStyleClass().add("light");

        xAxis.setStartMargin(4);
        xAxis.setEndMargin(4);
        xAxis.setGapStartAndEnd(false);

        // Set categories for X-axis
        xAxis.setCategories(FXCollections.observableArrayList(
                "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"
        ));

        // Remove tick marks from CategoryAxis
        xAxis.setTickMarkVisible(false);
        xAxis.setTickLabelsVisible(true);

        // Style the CategoryAxis
        xAxis.setStyle("-fx-border-color: transparent; -fx-border-width: 0;");

        @SuppressWarnings("unchecked")
        LineChart<String, Number> chart = (LineChart<String, Number>) weeklyChart;

        // Remove ALL grid lines and borders
        chart.setVerticalGridLinesVisible(true);
        chart.setHorizontalGridLinesVisible(true);
        chart.setHorizontalZeroLineVisible(true);

        // Make chart transparent
        chart.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");

        // Show Y-axis but style it to remove the black line
        NumberAxis yAxis = (NumberAxis) chart.getYAxis();
        yAxis.setVisible(true);
        yAxis.setManaged(true);
        yAxis.setTickLabelsVisible(true);
        yAxis.setTickMarkVisible(true);

        // Style Y-axis to remove the black line but keep ticks and labels
        yAxis.setStyle(
                "-fx-border-color: transparent; " +
                        "-fx-border-width: 0; " +
                        "-fx-background-color: transparent;"
        );

        // Remove the axis line specifically
        Platform.runLater(() -> {
            Node axisLine = yAxis.lookup(".axis-line");
            if (axisLine != null) {
                axisLine.setStyle("-fx-stroke: transparent; -fx-stroke-width: 0;");
                axisLine.setVisible(false);
            }

            Node plotBackground = chart.lookup(".chart-plot-background");
            if (plotBackground != null) {
                plotBackground.setStyle("-fx-border-color: transparent; -fx-border-width: 0;");
            }
        });

        // Your data series (using String for X values)
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

        chart.setCreateSymbols(true);
        chart.getData().addAll(calIn, calOut);
        chart.setPadding(new Insets(0, 0, 0, 0));
        chart.setLegendVisible(false);

        chart.applyCss();
        chart.layout();
        setupGlobalTooltip(chart, calIn, calOut);
    }

    private void setupGlobalTooltip(LineChart<String, Number> chart,
                                    XYChart.Series<String, Number> in,
                                    XYChart.Series<String, Number> out) {

        Line vLine = new Line();
        vLine.setStrokeWidth(1);
        vLine.setStyle("-fx-stroke: black;");
        vLine.setManaged(false);

        // Custom popup
        PopupControl popup = new PopupControl();
        popup.setAutoHide(false);

        Label dayLabel = new Label();
        dayLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");

        Label inLabel = new Label();
        inLabel.setStyle("-fx-text-fill: #ef4444;"); // red

        Label outLabel = new Label();
        outLabel.setStyle("-fx-text-fill: #ffa200;"); // orangey

        VBox content = new VBox(4, dayLabel, inLabel, outLabel);
        content.setStyle(
                "-fx-background-color: white;" +
                        "-fx-padding: 10 14 10 14;" +
                        "-fx-background-radius: 8;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 8, 0, 0, 2);"
        );

        popup.getScene().setRoot(content);

        Node plotArea = chart.lookup(".chart-plot-background");
        ((Pane) plotArea.getParent()).getChildren().add(vLine);

        plotArea.setOnMouseMoved(event -> {
            double x = event.getX();
            double width = plotArea.getBoundsInLocal().getWidth();
            double height = plotArea.getBoundsInLocal().getHeight();

            int size = in.getData().size();

            int index = (int) ((x / width) * size);
            index = Math.max(0, Math.min(index, size - 1));

            Node node = in.getData().get(index).getNode();
            if (node != null) {
                // Convert node position → scene → parent (where vLine lives)
                Bounds nodeBounds = node.localToScene(node.getBoundsInLocal());
                Bounds parentBounds = ((Pane) plotArea.getParent()).sceneToLocal(nodeBounds);

                double snappedX = parentBounds.getMinX() + parentBounds.getWidth() / 2;

                vLine.setStartX(snappedX);
                vLine.setEndX(snappedX);
            }
            Bounds plotBounds = plotArea.localToScene(plotArea.getBoundsInLocal());
            Bounds parentPlotBounds = ((Pane) plotArea.getParent()).sceneToLocal(plotBounds);

            vLine.setStartY(parentPlotBounds.getMinY());
            vLine.setEndY(parentPlotBounds.getMaxY());
            vLine.setVisible(true);


            // Tooltip logic (unchanged)
            String day = in.getData().get(index).getXValue();
            Number inVal = in.getData().get(index).getYValue();
            Number outVal = out.getData().get(index).getYValue();

            dayLabel.setText(day);
            inLabel.setText("Calories In:  " + inVal + " kcal");
            outLabel.setText("Calories Out: " + outVal + " kcal");

            popup.show(plotArea,
                    event.getScreenX() + 14,
                    event.getScreenY() + 14);
        });

        plotArea.setOnMouseExited(event -> popup.hide());
    }
}