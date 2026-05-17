package org.tin.oop2_capstone.model.state;

import org.tin.oop2_capstone.controllers.FoodLogController;

public class SuccessState implements State {
    /**
     * When we now have a food data, then we populate the UI list with food objects.
     */
    @Override
    public void handle(FoodLogController context) {
        // Populate the UI list with food objects
        // Hide loading indicator
        // Enable food log input
        context.populateFoodList();
        context.hideLoadingIndicator();
    }
}