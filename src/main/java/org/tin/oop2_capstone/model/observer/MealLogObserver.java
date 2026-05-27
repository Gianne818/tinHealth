package org.tin.oop2_capstone.model.observer;

import org.tin.oop2_capstone.model.entities.Meal;

public interface MealLogObserver {
    void onMealLogChanged(Meal meal);
}
