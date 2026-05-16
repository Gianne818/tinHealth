package org.tin.oop2_capstone.database.repositories;

import org.tin.oop2_capstone.model.entities.ActivityLog;
import org.tin.oop2_capstone.model.entities.MealLog;


public class LogRepository {
    /**
     * Gets food and activity log for current user using RetrieveData
     */

    public static volatile LogRepository instance;

    public ActivityLog activityLog;
    public MealLog mealLog;


    private LogRepository(){
        System.out.println("LogRepository is initialized for the first time.");
    }

    public static LogRepository getInstance(){
        if(instance == null){
            synchronized (LogRepository.class){
                if(instance == null){
                    instance = new LogRepository();
                }
            }
        }
        return instance;
    }

    public ActivityLog getActivityLog(){
        return activityLog;
    }

    public MealLog getMealLog(){
        return mealLog;
    }

    public void setActivityLog(ActivityLog activityLog){
        this.activityLog = activityLog;
    }

    public void setMealLog(MealLog mealLog){
        this.mealLog = mealLog;
    }


}
