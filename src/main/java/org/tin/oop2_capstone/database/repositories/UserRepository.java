package org.tin.oop2_capstone.database.repositories;

import org.tin.oop2_capstone.database.RetrieveData;
import org.tin.oop2_capstone.model.entities.User;

public class UserRepository {

    public static volatile UserRepository instance;

    private UserRepository(){

    }

    public UserRepository getInstance(){
        if(instance == null){
            synchronized (UserRepository.class){
                if(instance == null){
                    instance = new UserRepository();
                }
            }
        }
        return instance;
    }

    public static User getUser(String username, String password) {
        return RetrieveData.fetchUser(username, password);
    }

    public static int getPromptFrequency(int userId) {
        return RetrieveData.fetchUserPromptFrequency(userId);
    }
}
