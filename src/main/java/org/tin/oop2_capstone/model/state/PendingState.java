package org.tin.oop2_capstone.model.state;

import org.tin.oop2_capstone.controllers.FoodLogController;

public class PendingState implements State {
    /**
     * If there is no for the user, or some error with the API, then we do this.
     * Perhaps it will create the food object, but just the name and other non-api data for the food.
     * Maybe add a boolean isPending on to the Food object, and if true, set the values that come from API to zero.
     * Then we proceed with SyncMonitor
     */
    @Override
    public void handle(FoodLogController context) {
        // Create food object with name and non-API data
        // Set isPending flag to true
        // Set API-derived values to zero
        // Proceed with SyncMonitor for later synchronization
        context.createPendingFoodEntry();
        context.hideLoadingIndicator();
        context.showPendingIndicator();
        context.enableFoodLogInput();
    }
}
