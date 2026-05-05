package org.tin.oop2_capstone.database.repositories;

import org.tin.oop2_capstone.database.RetrieveData;

public class MealRepository {
    /**
     * Validate before inserting into database.
     * sometimes, the API returns a null, or maybe the user will input bad values
     */

    public static volatile MealRepository instance;

    private MealRepository(){
        System.out.println("MealRepository is initialized for the first time.");
    }

    public static MealRepository getInstance(){
        if(instance == null){
            synchronized (MealRepository.class){
                if(instance == null){
                    instance = new MealRepository();
                }
            }
        }
        return instance;
    }
    public static double getTodayCaloriesIn(int userId) {
        return RetrieveData.fetchUserTodayCaloriesIn(userId);
    }
}