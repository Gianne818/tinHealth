package org.tin.oop2_capstone.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.PopupControl;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.geometry.Pos;
import javafx.scene.chart.PieChart;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class DashboardController {
    @FXML ScrollPane dashboardScrollPane;
    @FXML private LineChart<?, ?> weeklyChart;
    @FXML private CategoryAxis xAxis;

//    For Weekly Macro Distributions
    @FXML PieChart macroChart;
    @FXML VBox macroLegendBox;

    public void initialize() {
        dashboardScrollPane.getStyleClass().add("light");
        weeklyCalorieTrack();
        weeklyMacroDistribution();
    }

    // Start of Weekly Calorie Tracking ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    private void weeklyCalorieTrack() {
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

    // Start of Weekly Macro Distributions ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public void weeklyMacroDistribution() {
        macroChart.getData().clear();
        macroLegendBox.getChildren().clear();

        PieChart.Data carbs = new PieChart.Data("45", 45); // Green
        PieChart.Data protein = new PieChart.Data("30", 30); // Red
        PieChart.Data fats = new PieChart.Data("25", 25); // Purple

        macroChart.getData().addAll(carbs, protein, fats);

        macroChart.setLabelsVisible(true);
        macroChart.setLabelLineLength(25);

        Platform.runLater(() -> {
            // 1. Slices
            if (carbs.getNode() != null) carbs.getNode().setStyle("-fx-pie-color: #11b981;");
            if (protein.getNode() != null) protein.getNode().setStyle("-fx-pie-color: #ef4444;");
            if (fats.getNode() != null) fats.getNode().setStyle("-fx-pie-color: #8b5cf6;");

            // 2. Text Labels (Match by string value)
            for (Node node : macroChart.lookupAll(".chart-pie-label")) {
                if (node instanceof javafx.scene.text.Text) {
                    String text = ((javafx.scene.text.Text) node).getText();
                    if (text.equals("45")) node.setStyle("-fx-fill: #11b981; -fx-font-weight: bold; -fx-font-size: 16px;");
                    if (text.equals("30")) node.setStyle("-fx-fill: #ef4444; -fx-font-weight: bold; -fx-font-size: 16px;");
                    if (text.equals("25")) node.setStyle("-fx-fill: #8b5cf6; -fx-font-weight: bold; -fx-font-size: 16px;");
                }
            }
        });

        // Build Custom Legend
        addLegendItem("Protein", 30, "#ef4444");
        addLegendItem("Carbs", 45, "#11b981");
        addLegendItem("Fats", 25, "#8b5cf6");
    }

    private void addLegendItem(String name, int value, String hexColor) {
        Circle dot = new Circle(6, javafx.scene.paint.Color.web(hexColor));
        Label nameLabel = new Label(name);

        Label valueLabel = new Label(value + "%");
        valueLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        // Pushes the percentage to the right-most side
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox row = new HBox(10, dot, nameLabel, spacer, valueLabel);
        row.setAlignment(Pos.CENTER_LEFT);
        macroLegendBox.getChildren().add(row);
    }

    public void goToHealthAnalytics() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/tin/oop2_capstone/views/health-analytics-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            // String style = getClass().getResource("/org/tin/oop2_capstone/styles/application.css").toExternalForm();
            // scene.getStylesheets().add(style);

            Stage primaryStage = (Stage) dashboardScrollPane.getScene().getWindow();

            primaryStage.setResizable(true);
            primaryStage.setMinWidth(1000);
            primaryStage.setMinHeight(800);
            primaryStage.setTitle("Health Tracker - Analytics");
            primaryStage.setScene(scene);
            primaryStage.setMaximized(true);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}