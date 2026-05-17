package org.tin.oop2_capstone.model.state;

import org.tin.oop2_capstone.controllers.FoodLogController;

public class LoadingState implements State {
    /**
     * Shows the loading spinning thing, then disable user actions while we wait for the API or some other process.
     * This happens after user clicks Add Entry, then we fetch stuff from the API.
     */
    @Override
    public void handle(FoodLogController context) {
        // Show loading spinner
        // Disable user actions while waiting for API
        context.showLoadingIndicator();
        context.hideErrorMessage();
    }
}
