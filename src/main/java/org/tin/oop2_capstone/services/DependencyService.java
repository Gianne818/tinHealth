package org.tin.oop2_capstone.services;

import org.tin.oop2_capstone.database.repositories.ActivityRepository;
import org.tin.oop2_capstone.database.repositories.MealRepository;
import org.tin.oop2_capstone.database.repositories.UserPrefRepository;
import org.tin.oop2_capstone.database.repositories.UserRepository;

public class DependencyService {
    private static UserRepository userRepository;
    private static MealRepository mealRepository;
    private static ActivityRepository activityRepository;
    private static UserPrefRepository userPrefRepository;

    public static void register(UserRepository userRepo, MealRepository mealRepo,
                                ActivityRepository activityRepo, UserPrefRepository userPrefRepo) {
        userRepository = userRepo;
        mealRepository = mealRepo;
        activityRepository = activityRepo;
        userPrefRepository = userPrefRepo;
    }

    public static UserRepository getUserRepository() { return userRepository; }
    public static MealRepository getMealRepository() { return mealRepository; }
    public static ActivityRepository getActivityRepository() { return activityRepository; }
    public static UserPrefRepository getUserPrefRepository() { return userPrefRepository; }

}
