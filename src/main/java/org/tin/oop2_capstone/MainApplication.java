package org.tin.oop2_capstone;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.tin.oop2_capstone.controllers.LoginController;
import org.tin.oop2_capstone.database.data_sources.ActivityDataSource;
import org.tin.oop2_capstone.database.data_sources.MealDataSource;
import org.tin.oop2_capstone.database.data_sources.UserDataSource;
import org.tin.oop2_capstone.database.data_sources.UserPrefDataSource;
import org.tin.oop2_capstone.database.repositories.ActivityRepository;
import org.tin.oop2_capstone.database.repositories.MealRepository;
import org.tin.oop2_capstone.database.repositories.UserPrefRepository;
import org.tin.oop2_capstone.database.repositories.UserRepository;
import org.tin.oop2_capstone.services.DependencyService;

import java.io.IOException;
import java.io.InputStream;

public class MainApplication extends Application {
    private UserRepository userRepository;
    private MealRepository mealRepository;
    private ActivityRepository activityRepository;
    private UserPrefRepository userPrefRepository;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() {
        this.userPrefRepository = new UserPrefRepository(new UserPrefDataSource());
        this.activityRepository = new ActivityRepository(new ActivityDataSource());
        this.mealRepository = new MealRepository(new MealDataSource());
        this.userRepository = new UserRepository(new UserDataSource());

        DependencyService.register(userRepository, mealRepository, activityRepository, userPrefRepository);
    }

    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/tin/oop2_capstone/views/login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        stage.setHeight(400);
        stage.setWidth(650);

        String style = getClass().getResource("/org/tin/oop2_capstone/styles/application.css").toExternalForm();
        String lightMode = getClass().getResource("/org/tin/oop2_capstone/styles/lightmode.css").toExternalForm();
        scene.getStylesheets().add(style);
        scene.getStylesheets().add(lightMode);
        scene.getRoot().getStyleClass().add("light");
        stage.setResizable(false);

        stage.setTitle("Health Tracker");
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    /* 1. Activity logging and deleting doesn't work
        2. implement Health Controller and Dashbboard Controller
     */


}
