package org.tin.oop2_capstone.utils;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.stage.Stage;
import java.io.IOException;

/**
 *This is a builder pattern
 */

public class SceneSwitcher {
    private final Stage stage;
    private final String fxml;
    private double minWidth = -1;
    private double minHeight = -1;
    private double prefWidth = -1;
    private double prefHeight = -1;
    private double maxWidth = -1;
    private double maxHeight = -1;
    private boolean isCentered;
    private boolean isMaximized;
    private boolean isResizeable;
    private String title;
    private String css;

    private SceneSwitcher(Node node, String fxml){
        this.stage = (Stage) node.getScene().getWindow();
        this.fxml = fxml;
    }

    public static SceneSwitcher use(Node node, String fxml){
        return new SceneSwitcher(node, fxml);
    }

    public SceneSwitcher setMinDimensions(double minWidth, double minHeight){
        this.minWidth = minWidth;
        this.minHeight = minHeight;
        return this;
    }

    public SceneSwitcher setPrefDimensions(double prefWidth, double prefHeight){
        this.prefWidth = prefWidth;
        this.prefHeight = prefHeight;
        return this;
    }

    public SceneSwitcher setMaxDimensions(double maxWidth, double maxHeight){
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
        return this;
    }

    public SceneSwitcher setCentered(boolean centered) {
        isCentered = centered;
        return this;
    }

    public SceneSwitcher setMaximized(boolean maximized) {
        isMaximized = maximized;
        return this;
    }

    public SceneSwitcher setResizeable(boolean resizeable) {
        isResizeable = resizeable;
        return this;
    }

    public SceneSwitcher setTitle(String title) {
        this.title = title;
        return this;
    }

    public SceneSwitcher setCss(String css){
        this.css = css;
        return this;
    }

    public void switchScene(){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/tin/oop2_capstone/views/" + fxml + ".fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            stage.setScene(scene);

            if(minHeight!=-1 && minWidth!=-1){
                stage.setMinWidth(minWidth);
                stage.setMinHeight(minHeight);
            }

            if(maxHeight!=-1 && maxWidth!=-1){
                stage.setMaxHeight(maxHeight);
                stage.setMaxWidth(maxWidth);
            }

            if(!isMaximized && prefHeight!=-1 && prefWidth!=-1){
                stage.setWidth(prefWidth);
                stage.setHeight(prefHeight);
            }

            if(title!=null){
                stage.setTitle(title);
            }


            if(css !=null){
                String style = getClass().getResource("/org/tin/oop2_capstone/styles/" + css + ".css").toExternalForm();
                scene.getStylesheets().add(style);
            }



            stage.show();
            if(isMaximized){
                Platform.runLater(() ->{
                    stage.setMaximized(isMaximized);
                });

            } else if(isCentered) {
                stage.centerOnScreen();
            }

            stage.setResizable(isResizeable);


        } catch(IOException e){
            e.printStackTrace();
        }


    }
}
