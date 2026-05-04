package org.tin.oop2_capstone.model.observer;

import org.tin.oop2_capstone.model.entities.Activity;
import org.tin.oop2_capstone.model.entities.Meal;

public interface LogObserver {
    /**
     * called when food or activity or stuff is added.
     * perhaps a function like onFoodLogChanged(Food) or onActivityLogChanged(Activity) or smth idk
     * maybe dashboard controller will implement this to update the stuff on the ui
     */
    void onActivityLogChanged(Activity activity);
    void onMealLogChanged(Meal meal);
}
