package org.tin.oop2_capstone.services;

import org.tin.oop2_capstone.model.observer.ExerciseObserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


/**
 * gets current active app, with methods being different for windows, mac, and linux
 */
public class ActiveAppService implements Runnable {
    public static final String OSName = System.getProperty("os.name").toLowerCase();

    private boolean isRunning = true;
    private ExerciseObserver exerciseObserver;

    public ActiveAppService(ExerciseObserver exerciseObserver){
        this.exerciseObserver = exerciseObserver;
    }

    public synchronized boolean isRunning() {
        return isRunning;
    }

    public synchronized void stopMonitoring(){
        this.isRunning = false;
    }

    @Override
    public void run() {
        String currentApp = "";
        String lastAppUsed = "";
        while(isRunning){
            if(OSName.contains("mac")){
                currentApp = macActiveApp();
                if(!currentApp.equals(lastAppUsed)){
                    exerciseObserver.onAppChanged(currentApp);
                    lastAppUsed = currentApp;
                }
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                lastAppUsed = currentApp;
            }
            // TODO: Check for windows and linux

        }



    }

    private String macActiveApp(){
        String[] command = {"osascript", "-e", "tell application \"System Events\" to get name of first application process whose frontmost is true"};
        try{
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            Process process = processBuilder.start();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String activeApp = bufferedReader.readLine();
            System.out.println("Current active app: " + activeApp);
            return activeApp;

        } catch (IOException e){
            e.printStackTrace();
        }

        return "";
    }

    // TODO: implement the windowsActiveApp() and linuxActiveApp()


}
