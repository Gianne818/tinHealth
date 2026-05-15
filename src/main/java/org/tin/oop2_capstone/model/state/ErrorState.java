package org.tin.oop2_capstone.model.state;

import org.tin.oop2_capstone.controllers.FoodLogController;

public class ErrorState implements State {
    /**
     * any other reason that warrants this
     */
    @Override
    public void handle(FoodLogController context) {
        // Display error message to user
        // Hide loading indicator
        // Enable food log input for retry
        context.showErrorMessage();
        context.hideLoadingIndicator();
        context.enableFoodLogInput();
    }
}
