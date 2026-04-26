package org.tin.oop2_capstone;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import javax.swing.text.Style;
import java.io.IOException;
import java.io.InputStream;

public class MainApplication extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
//        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/tin/oop2_capstone/views/login-view.fxml"));
//        Scene scene = new Scene(fxmlLoader.load());
//
//        primaryStage.setMinHeight(400);
//        primaryStage.setMinWidth(600);
//
//        String style = getClass().getResource("/org/tin/oop2_capstone/styles/application.css").toExternalForm();
//        scene.getStylesheets().add(style);
//        primaryStage.setResizable(false);
//
//        primaryStage.setTitle("Health Tracker");
//        primaryStage.setScene(scene);
//        primaryStage.show();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/tin/oop2_capstone/views/main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        String style = getClass().getResource("/org/tin/oop2_capstone/styles/application.css").toExternalForm();
        scene.getStylesheets().add(style);

        primaryStage.setResizable(true);

        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(800);

        primaryStage.setTitle("Health Tracker");
        primaryStage.setScene(scene);

        primaryStage.setMaximized(true);
        primaryStage.show();
    }
}