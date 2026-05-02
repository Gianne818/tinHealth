package org.tin.oop2_capstone.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.PopupControl;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.chart.XYChart.Series;
import  javafx.scene.chart.PieChart.Data;
import javafx.scene.shape.Circle;

import java.io.IOException;
import javafx.scene.shape.Line;
import javafx.scene.layout.Pane;

public class DashboardController {
    @FXML ScrollPane dashboardScrollPane;
    @FXML private LineChart<?, ?> weeklyChart;
    @FXML private CategoryAxis xAxis;

    @FXML PieChart macroDistPieChart;
    @FXML Circle macroDistInnerHoleCircle;

    ObservableList<PieChart.Data> macroDistData;

    public void initialize() {
        dashboardScrollPane.getStyleClass().add("light");
        macroDistData = FXCollections.observableArrayList();

        // todo: use real data for the charts
        macroDistData.add(new Data("Protein", 50.0));
        macroDistData.add(new Data("Carbs", 30.0));
        macroDistData.add(new Data("Fats", 20.0));
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

        plotArea.setOnMouseMoved( e->{
            double xInAxis = xAxis.sceneToLocal(e.getSceneX(), e.getSceneY()).getX();
            String day = xAxis.getValueForDisplay(xInAxis);

            if(day!=null){
                XYChart.Data<String, Number> inData = findData(in,day);
                XYChart.Data<String, Number> outData = findData(out, day);

                if (inData!=null && outData!=null){
                    toolTipController.setData(day, inData.getYValue(), outData.getYValue());
                    popup.show(plotArea, e.getScreenX()+15, e.getScreenY()+15);
                }
            }
        });

        plotArea.setOnMouseExited(e->popup.hide());
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
        chart.getData().addAll(calIn, calOut);
        chart.setLegendVisible(false);
        setupGlobalTooltip(chart, calIn, calOut);
    }
}

