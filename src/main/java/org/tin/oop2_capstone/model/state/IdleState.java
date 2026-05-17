package org.tin.oop2_capstone.model.state;

import org.tin.oop2_capstone.controllers.FoodLogController;

public class IdleState implements State {
    /**
     * The default state. Perhaps hide like.. the loading spinning thing, hide the error text, then  enable adding on food log
     */
    @Override
    public void handle(FoodLogController context) {
        // Hide loading spinner
        // Hide error text
        // Enable adding food log functionality
        context.hideLoadingIndicator();
        context.hideErrorMessage();
    }
}
