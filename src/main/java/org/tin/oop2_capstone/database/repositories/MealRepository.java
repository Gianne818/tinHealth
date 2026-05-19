    package org.tin.oop2_capstone.database.repositories;
    
    import org.tin.oop2_capstone.database.InsertData;
    import org.tin.oop2_capstone.database.RetrieveData;
    import org.tin.oop2_capstone.model.entities.Food;
    import org.tin.oop2_capstone.model.entities.FoodCombo;
    import org.tin.oop2_capstone.model.entities.Meal;
    
    import java.time.LocalDate;
    import java.util.ArrayList;
    import java.util.List;
    
    public class MealRepository {
        /**
         * Validate before inserting into database.
         * sometimes, the API returns a null, or maybe the user will input bad values
         */
    
        public static volatile MealRepository instance;
    
        private List<Meal> userMeals;
    
        private MealRepository(){
            System.out.println("MealRepository is initialized for the first time.");
        }
    
        public static MealRepository getInstance(){
            if(instance == null){
                synchronized (MealRepository.class){
                    if(instance == null){
                        instance = new MealRepository();
                    }
                }
            }
            return instance;
        }
    
        public void fetchInitialMealData(int userId) {
            this.userMeals = RetrieveData.fetchUserMeals(userId);
        }
    
        public List<Meal> getUserMeals() {
            return userMeals;
        }
    
        public static double getTodayCaloriesIn(int userId) {
            return RetrieveData.fetchUserTodayCaloriesIn(userId);
        }
    
        public boolean addMeal(Meal meal, int userId) {
            if(InsertData.insertMeal(userId, meal)){
                if (userMeals != null) {
                    userMeals.add(0, meal); // Add to beginning of list
                } else {
                    userMeals = new ArrayList<>();
                    userMeals.add(meal);
                }
                return true;
            }
            return false;
        }
    }