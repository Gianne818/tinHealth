package org.tin.oop2_capstone.services;

import org.tin.oop2_capstone.api.APIResponse;
import org.tin.oop2_capstone.model.entities.Food;
import org.tin.oop2_capstone.api.FoodAPI;
import org.tin.oop2_capstone.database.RetrieveData;
import org.tin.oop2_capstone.database.UpdateData;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

public class SyncService {

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
                APIResponse response = FoodAPI.getFoodData(food.getName());
                if (response.getHttpCode() == 200 && response.getJson() != null) {
                    Food updatedFood = FoodParser.parseFood(response.getJson());
                    if (updatedFood != null) {
                        UpdateData.updateFoodNutrition(food.getName(), updatedFood.getNutrition());
                        UpdateData.markAsSynced(food.getName());
                        System.out.println("Synced: " + food.getName());
                    }
                    /* this is just for printing */
                } else if (response.getHttpCode() == 429 || response.getHttpCode() == 503 || response.getHttpCode() == 504) {
                    // Retryable - stay pending
                    System.out.println("Retryable error for " + food.getName() + ": " + response.getHttpCode());
                } else {
                    // Non-retryable error
                    System.out.println("Failed to sync " + food.getName() + ": HTTP " + response.getHttpCode());
                }

            } catch (Exception e) {
                System.out.println("Failed to sync: " + food.getName());
                e.printStackTrace();
            }
        }
    }
}