package org.tin.oop2_capstone.database.repositories;

import org.tin.oop2_capstone.database.RetrieveData;
import org.tin.oop2_capstone.model.entities.User;
import org.tin.oop2_capstone.model.entities.UserPreferences;
import org.tin.oop2_capstone.services.SessionManager;

public class UserRepository {

    public static volatile UserRepository instance;
    public User currentUser;

    private UserRepository(){
        System.out.println("UserRepository is initialized for the first time.");
    }

    public static UserRepository getInstance(){
        if(instance == null){
            synchronized (UserRepository.class){
                if(instance == null){
                    instance = new UserRepository();
                }
            }
        }
        return instance;
    }

    public void login(String username, String password) {
        currentUser = RetrieveData.fetchUser(username, password);
        if(currentUser != null){
            SessionManager.getInstance().setCurrentUser(currentUser);
            UserPreferences prefs = RetrieveData.fetchUserPreferences(currentUser.getUid());
            SessionManager.getInstance().setCurrentUserPrefs(prefs);
        }
    }

    public User getUser(){
        return currentUser;
    }

}
