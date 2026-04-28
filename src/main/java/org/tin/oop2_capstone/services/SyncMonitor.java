package org.tin.oop2_capstone.services;

public class SyncMonitor {
    /**
     * Runs on the background.
     * Every few minutes or maybe when connection is restored, we look at the foods via LogRepository
     * and see if there is any Food object that isPending.
     * Then call NutritionApiAdapter for the process stuff, then sets isPending to false, then update UI via the LogObserver.
     */
}
