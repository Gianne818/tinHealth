package org.tin.oop2_capstone.database.repositories;

import org.tin.oop2_capstone.database.DatabaseConnection;
import org.tin.oop2_capstone.database.RetrieveData;

import java.sql.*;

public class UserPrefRepository {

    public static volatile UserPrefRepository instance;

    private UserPrefRepository() {
        System.out.println("UserPrefRepository is initialized for the first time.");
    }

    public static UserPrefRepository getInstance(){
        if(instance == null){
            synchronized (UserPrefRepository.class){
                if(instance == null){
                    instance = new UserPrefRepository();
                }
            }
        }
        return instance;
    }

    public  static int getDailyCalorieInGoal(int userId) {
       return RetrieveData.fetchUserDailyCalorieInGoal(userId);
    }
}