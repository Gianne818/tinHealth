package org.tin.oop2_capstone.database.repositories;

import java.sql.*;
import org.tin.oop2_capstone.database.DatabaseConnection;

public class UserRepository {
    /**
     * Handle login or signup validation then call in the database.
     */

    public boolean validateLogin(String username, String password) {
        // If both are empty -> allow
        if ((username == null || username.trim().isEmpty()) &&
                (password == null || password.trim().isEmpty())) {
            return true;
        }

        // If either one is not empty -> verify in database
        String query = "SELECT * FROM Users WHERE username = ? AND password_hash = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


}
