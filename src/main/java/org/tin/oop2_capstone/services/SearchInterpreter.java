package org.tin.oop2_capstone.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchInterpreter {
    /*
    todo: here we have the logic for multiple foods with tokens like "and", "with", "," and others.
    todo: worry about the units. If user inputs like 2 hotdogs, remove s, then query for two hotdogs
    todo: handle as well like... 2 cups of coffee. If this proves to be too difficult we may just let user input units and items separately.
    we might wanna settle on the third option. Ill try to find an intuitive way for it.
    **/

    /*
    top priority:
    Do Interpreter.java so that we can finally test for database insertion of food logs.

        1. Before calling the food api, we pass the user input here first, to determine whether it is a Food or a FoodCombo class.
        2. To determine, check with keywords these tokens: "with", "and", "&", ",".
        2. If it is a food, then we just call the API.
        3. if it is a FoodCombo, then we call the API for all the Foods inside the FoodCombo's Food[] foods.

    For example:
        1. User inputs Oreo with Milk
        2. Interpreter detects two Foods and returns a list or array of food.
        3. Controller will call the api for all those food.

        --- The rest will be implemented later when we have finished interpreter ---
        4. Controller will insert to the database via FoodRepository
        5. Insertion on Food Repository shall behave differently for Food and FoodCombo.
        6. See attached Images for database example of handling FoodCombo

     */
    private String input;
    private boolean isFoodCombo;
    // isFood is redundant since we can just say it is Food when FoodCombo is false

    public SearchInterpreter(){
        input = "";
        isFoodCombo = false;
    }

    public List<String> interpret(String input) {
        this.input = input;

        String[] tokens = {"with", "and", "&", ","};
        List<String> foods = new ArrayList<>();
        input = input.trim().toLowerCase();

        for(String token : tokens){
            if(input.contains(token)){
                String[] splitted = input.split(token);
                // i swear si intellij ni nag suggest nga ing.anion, not vibe-coded
                foods.addAll(Arrays.asList(splitted));
            }
        }
        this.isFoodCombo = foods.size() != 1;
        return foods;
    }

    public String getInput() {
        return input;
    }

    public boolean isFoodCombo() {
        return isFoodCombo;
    }
}
