package org.tin.oop2_capstone.database.repositories;

import org.tin.oop2_capstone.database.RetrieveData;
import org.tin.oop2_capstone.database.data_sources.UserDataSource;
import org.tin.oop2_capstone.model.entities.User;

public class UserRepository {

    private User currentUser;
    private final UserDataSource userDataSource;

    public UserRepository(UserDataSource userDataSource){
        this.userDataSource = userDataSource;
    }


    public void login(String username, String password) {
        currentUser = userDataSource.fetchUser(username, password);
    }

    public User getUser(){
        return currentUser;
    }

}
