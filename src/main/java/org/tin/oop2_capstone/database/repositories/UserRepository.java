package org.tin.oop2_capstone.database.repositories;

import org.tin.oop2_capstone.database.RetrieveData;
import org.tin.oop2_capstone.model.entities.User;

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
    }

    public User getUser(){
        return currentUser;
    }

}
