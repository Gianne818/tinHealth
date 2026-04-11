package org.tin.oop2_capstone;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/tin/oop2_capstone/views/login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        primaryStage.setMinHeight(400);
        primaryStage.setMinWidth(600);

        primaryStage.setResizable(false);


        primaryStage.setTitle("Health Tracker");
        primaryStage.setScene(scene);
        primaryStage.show();

    }
}
