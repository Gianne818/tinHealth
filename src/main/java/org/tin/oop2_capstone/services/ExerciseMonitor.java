package org.tin.oop2_capstone.services;

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

    public ExerciseMonitor(){
        this.activeAppService = new ActiveAppService(this);
        this.thread = new Thread(activeAppService);

        // todo: get excluded apps from user preferences
        // todo: remove timerOff(its for testing only), and
        excludedApps = new String[]{"idea", "java"};
        timerOff = true;

    }

    public void start(){
        thread.start();
    }

    @Override
    public void onAppChanged(String appName) {
        System.out.println("New app: " + appName);

        //todo: popup exercise prompt
        SceneSwitcher.use("exercise-prompt-view").setCentered(true).setPrefDimensions(450, 550)
                .setCss("application").setStyleClasses(new String[]{"light", "dashboardScrollPane"}).setResizeable(false).switchScene();

    }
}
