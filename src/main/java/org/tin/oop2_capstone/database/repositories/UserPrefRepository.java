package org.tin.oop2_capstone.database.repositories;

import org.tin.oop2_capstone.database.DatabaseConnection;
import org.tin.oop2_capstone.database.RetrieveData;
import org.tin.oop2_capstone.model.entities.UserPreferences;

import java.sql.*;

public class UserPrefRepository {

    public static volatile UserPrefRepository instance;

    private UserPreferences userPref;



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

    public UserPreferences getUserPref(){
        return userPref;
    }

    public void setUserPref(int uid){
        return RetrieveData.
    }
}