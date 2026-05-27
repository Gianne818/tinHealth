package org.tin.oop2_capstone.services;

import javafx.application.Platform;
import org.tin.oop2_capstone.controllers.MainController;
import org.tin.oop2_capstone.model.observer.ExerciseObserver;
import org.tin.oop2_capstone.utils.SceneSwitcher;

public class ExerciseMonitor implements ExerciseObserver {
    /**
     *  Here we do the observing for when user opens an app or make it the active window, app is not excluded, and x mins or hours of time has passed since the last user exercise prompt, then we do the exercise prompt
     *  Perhaps we may need to ask for permissions for this, such as overlay or like... process list access?
     *  Perhaps poll the os every few seconds or smth
     */

    private ActiveAppService activeAppService;
    private Thread thread;
    private String[] excludedApps;
    private boolean timerOff;

    private static ExerciseMonitor instance;


    private ExerciseMonitor(){
        instance = this;
        this.activeAppService = new ActiveAppService(this);
        this.thread = new Thread(activeAppService);

        // todo: get excluded apps from user preferences
        // todo: remove timerOff(its for testing only), and
        excludedApps = new String[]{"idea", "java"};
        timerOff = true;

    }

    public static ExerciseMonitor getInstance(){
        if(instance == null){
            return new ExerciseMonitor();
        }
        return instance;
    }

    public ActiveAppService getActiveAppService() {
        return activeAppService;
    }

    public void start(){
        if(thread != null && thread.isAlive()){
            return;
        }
        thread = new Thread(activeAppService);
        thread.setDaemon(true);
        activeAppService.startMonitoring();
        thread.start();
    }

    public void resume(){
        activeAppService.resumeMonitoring();
    }
    @Override
    public void onAppChanged(String appName) {
        System.out.println("New app: " + appName);

        //todo: popup exercise prompt

//        if(activeAppService.getLastAppUsed().equals("java")) return;
        for(String s : excludedApps){
            if(s.equals(appName)){
                return;
            }
        }
        activeAppService.pauseMonitoring();
        Platform.runLater(() -> {
            SceneSwitcher.openNewWindow("exercise-prompt-view").setCentered(true).setPrefDimensions(450, 550)
                    .setCss("application").setStyleClasses(new String[]{"light", "dashboardScrollPane"}).setResizeable(false).switchScene();
        });
    }
}
