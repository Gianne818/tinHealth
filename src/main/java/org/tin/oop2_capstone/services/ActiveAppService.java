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
    private boolean isPaused = false;
    private ExerciseObserver exerciseObserver;
    private String lastAppUsed = "";

    public ActiveAppService(ExerciseObserver exerciseObserver){
        this.exerciseObserver = exerciseObserver;
    }

    public synchronized boolean isRunning() {
        return isRunning;
    }

    public synchronized  void pauseMonitoring(){
        this.isPaused = true;
    }

    public synchronized  void resumeMonitoring(){
        this.isPaused = false;
        notifyAll();
    }


    public synchronized void stopMonitoring(){
        this.isRunning = false;
        notifyAll();
    }

    public synchronized void startMonitoring(){
        this.isRunning = true;
    }

    @Override
    public void run() {
        String currentApp = "";

        while(isRunning){
            synchronized (this) {
                while (isPaused) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
            if(!isRunning) break;

            if (OSName.contains("mac")) {
                currentApp = macActiveApp();
                if (!currentApp.equals("java") && !currentApp.equals(lastAppUsed)) {
                    exerciseObserver.onAppChanged(currentApp);
                    lastAppUsed = currentApp;
                }
            }
            // TODO: Check for windows and linux


            synchronized (this) {
                try {
                    wait(3000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

        }
    }

    private String macActiveApp(){
        String[] command = {"osascript", "-e", "tell application \"System Events\" to get name of first application process whose frontmost is true"};
        Process process = null;
        try{
            ProcessBuilder processBuilder = new ProcessBuilder(command);
           process = processBuilder.start();

            try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()))){
                String activeApp = bufferedReader.readLine();
                System.out.println("Current active app: " + activeApp);
                return activeApp;
            }
        } catch (IOException e){
            e.printStackTrace();
        } finally {
            if(process!=null) process.destroyForcibly();
        }

        return "";
    }

    // TODO: implement the windowsActiveApp() and linuxActiveApp()


}
