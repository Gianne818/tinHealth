package org.tin.oop2_capstone.services;

import org.tin.oop2_capstone.database.repositories.MealRepository;
import org.tin.oop2_capstone.model.entities.Activity;
import org.tin.oop2_capstone.model.entities.Meal;
import org.tin.oop2_capstone.model.entities.MealLog;
import org.tin.oop2_capstone.model.observer.MealLogObserver;
import org.tin.oop2_capstone.model.observer.MealLogObserver;

import java.util.ArrayList;
import java.util.List;

public class MealLogger extends Logger<MealLogObserver> {
    private static MealLogger instance;
    private MealLog mealLog;
    private MealRepository mealRepository = DependencyService.getMealRepository();
    private int userID = SessionManager.getInstance().getCurrentUser().getUid();

    private Meal mealToAdd;


    private MealLogger() {
        this.mealLog = new MealLog();
    }

    public static synchronized MealLogger getInstance() {
        if (instance == null) {
            instance = new MealLogger();
        }
        return instance;
    }

    public boolean addMeal(Meal meal){
        this.mealToAdd = meal;
        return logData();
    }


    @Override
    public boolean isValid() {
        return mealLog != null;
    }

    @Override
    public boolean saveToDB() {
        if(mealRepository.addMeal(mealToAdd, userID)){
            mealToAdd = null;
            return true;
        }
        return false;
    }

    @Override
    public void notifyObservers() {
        for(MealLogObserver mealLogObserver : observers){
            mealLogObserver.onMealLogChanged();
        }

    }

    public boolean deleteMealLog(int mealId){
        if(mealRepository.deleteMeal(mealId)){
            notifyObservers();
            return true;
        }
        return false;
    }

    public MealLog getActivityLog() {
        return mealLog;
    }

    public void setMealLog(MealLog mealLog) {
        this.mealLog = mealLog;
    }

}