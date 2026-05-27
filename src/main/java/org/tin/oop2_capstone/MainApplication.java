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

    /* koan do james. here is what to do.

    1. Update ActivityLogger such that it would extend Logger<ActivityLogObserver> i think.
        and have it hold the userRepository = DependencyService.getActRepo.... as well as userID so that it can handle saveToDB.
        and perhaps on activityLogger also add like...deleteActivtyLog, then after deletion, notigy the observers.

    2. on ActivtyLogController, make it implement ActivtyLogObnbserver, then override the method wherein you update the UI inside the activLogChjanged.
        also perhaps on initialize(), do like.. ActivityLogger.addObserver(this). then on addEntryButton, call the activtyLogger.logData().
        oh also call diay the activityLogger.deleteACtivtyLog() on delete sa activtyController


    */


}
