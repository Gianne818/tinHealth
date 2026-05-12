package org.tin.oop2_capstone.database.repositories;

import org.tin.oop2_capstone.database.RetrieveData;
import org.tin.oop2_capstone.model.entities.User;

import org.tin.oop2_capstone.database.RetrieveData;

public class UserRepository {

    private static volatile UserRepository instance;
    private User currentUser;


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

    public User login(String username, String password){

        return RetrieveData.fetchUser(username, password);

    }

    public User getUser() {
        return currentUser;
    }

    public int getUserID(){
        return currentUser.getUid();
    }

}
