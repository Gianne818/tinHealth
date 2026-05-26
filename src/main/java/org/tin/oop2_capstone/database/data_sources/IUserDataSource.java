package org.tin.oop2_capstone.database.data_sources;

import org.tin.oop2_capstone.model.entities.User;

public interface IUserDataSource {
    User fetchUser(String username, String password);
}
