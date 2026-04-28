package org.tin.oop2_capstone.model.state;

public interface State {
    /**
     * This is useful for the API stuff on the food log.
     * This interface will define the contract. FoodLogController will be the context, like it will have a SearchState within its variables
     * Basically tells what the UI wil do.
     * have a method like maybe handle(FoodLogController)
     * to be implemented by other states
     */
}
