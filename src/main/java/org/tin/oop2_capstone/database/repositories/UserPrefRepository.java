package org.tin.oop2_capstone.database.repositories;

import org.tin.oop2_capstone.database.DatabaseConnection;
import org.tin.oop2_capstone.database.RetrieveData;

import java.sql.*;

public class UserPrefRepository {

    public  static int getDailyCalorieInGoal(int userId) {
       return RetrieveData.fetchUserDailyCalorieInGoal(userId);
    }
}