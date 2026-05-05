package org.tin.oop2_capstone.database.repositories;

import org.tin.oop2_capstone.database.RetrieveData;
import org.tin.oop2_capstone.model.entities.User;

public class UserRepository {

    public static User getUser(String username, String password) {
        return RetrieveData.fetchUser(username, password);
    }

    public static int getPromptFrequency(int userId) {
        return RetrieveData.fetchUserPromptFrequency(userId);
    }
}
