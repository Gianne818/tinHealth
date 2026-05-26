package org.tin.oop2_capstone.database.data_sources;

import org.tin.oop2_capstone.database.RetrieveData;
import org.tin.oop2_capstone.model.entities.User;

public class UserDataSource implements IUserDataSource {
    @Override
    public User fetchUser(String username, String password) {
        return RetrieveData.fetchUser(username, password);
    }
}
