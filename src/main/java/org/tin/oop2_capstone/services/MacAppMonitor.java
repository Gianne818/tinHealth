package org.tin.oop2_capstone.services;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class MacAppMonitor {

    public static String getActiveApp() {
        String[] command = {
                "osascript",
                "-e",
                "tell application \"System Events\" to get name of first application process whose frontmost is true"
        };

        try {
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String activeApp = reader.readLine();
            process.waitFor();
            System.out.println("Active app: " + activeApp);

            return activeApp != null ? activeApp.trim() : "";

        } catch (Exception e) {
            System.err.println("Failed to fetch active Mac app: " + e.getMessage());
            return "";
        }
    }
}