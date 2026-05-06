package org.tin.oop2_capstone.services;

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
}