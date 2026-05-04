package org.tin.oop2_capstone.database.repositories;

import java.sql.*;
import org.tin.oop2_capstone.database.DatabaseConnection;
import org.tin.oop2_capstone.model.entities.User;

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

    public User getUserByUsername(String username) {
        String query = "SELECT * FROM Users WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setUid(rs.getInt("user_id"));
                user.setFullname(rs.getString("fullname"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password_hash"));
                user.setAge(rs.getInt("age"));
                user.setMale("Male".equals(rs.getString("gender")));
                user.setWeightKg(rs.getDouble("weight_kg"));
                user.setHeightCm(rs.getDouble("height_cm"));
                user.setActivityLevel(rs.getString("activity_level"));
                return user;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


}
