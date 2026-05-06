package org.tin.oop2_capstone.services;

import org.tin.oop2_capstone.model.entities.Food;
import org.tin.oop2_capstone.api.FoodAPI;
import org.tin.oop2_capstone.database.RetrieveData;
import org.tin.oop2_capstone.database.UpdateData;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.sql.*;
import java.util.List;

public class PermissionService {

    //INTERNET CHECK
    public boolean hasInternet() {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress("8.8.8.8", 53), 1500);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    //SYNC FUNC
    public void syncPendingFoods() {
        //if walay net kay d siya mo continue
        if (!hasInternet()) {
            System.out.println("No internet. Skipping sync.");
            return;
        }

        // kuwaon niya ang pending foods
        List<Food> pendingFoods = RetrieveData.getPendingFoodsFromDB();

        // if wala then stop
        if (pendingFoods.isEmpty()) {
            System.out.println("No pending foods to sync.");
            return;
        }

        //main logic
        System.out.println("Syncing " + pendingFoods.size() + " pending foods...");
        for (Food food : pendingFoods) {
            try {

                String json = FoodAPI.getFoodData(food.getName());
                if (json == null) continue;

                Food updatedFood = FoodParser.parseFood(json);
                if (updatedFood == null) continue;
                //adds the pending to foodNutrition
                UpdateData.updateFoodNutrition(food.getName(), updatedFood.getNutrition());
                UpdateData.markAsSynced(food.getName());
                System.out.println("Synced: " + food.getName());

            } catch (Exception e) {
                System.out.println("Failed to sync: " + food.getName());
                e.printStackTrace();
            }
        }
    }

    /**
     * os permission checks
     * This thing checks for if permissions have been given for the exercise prompt popup.
     * need ba ug permission if mag check siya if naay internet ang user or wla?
     *
     * if needed, it will be important for SyncMonitor to work
     * Since this exception is thrown, the food object that was queried should be stored in a queue,
     * then that queue will be in the user session.ser file, like a queue of objects over there,
     * and then once magka internet, it will take those queued objects, requery it in the api so
     * that we can get the nutritional values
     *
     * just ask permission to overlay, and check current app focused if it changes
     *
     * "
     * Here we do the observing for when user opens an app or make it the active window, app is not excluded,
     * and x mins or hours of time has passed since the last user exercise prompt,
     * then we do the exercise prompt
     *
     * Perhaps we may need to ask for permissions for this, such as overlay or like... process list access?
     *
     * Perhaps poll the os every few seconds or smth
     * " * -From exercisMonitor
     */



}