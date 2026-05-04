package org.tin.oop2_capstone.database.repositories;

import java.sql.*;
import org.tin.oop2_capstone.database.DatabaseConnection;

public class TestSelectUsers { // temp file rani ha
    public static void main(String[] args) {
        String query = "SELECT * FROM Users";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            System.out.println("=== Users Table ===");
            System.out.println("------------------------------------------------");

            while (rs.next()) {
                System.out.println("user_id: " + rs.getInt("user_id"));
                System.out.println("fullname: " + rs.getString("fullname"));
                System.out.println("username: " + rs.getString("username"));
                System.out.println("email: " + rs.getString("email"));
                System.out.println("age: " + rs.getInt("age"));
                System.out.println("date_of_birth: " + rs.getDate("date_of_birth"));
                System.out.println("gender: " + rs.getString("gender"));
                System.out.println("weight_kg: " + rs.getBigDecimal("weight_kg"));
                System.out.println("height_cm: " + rs.getBigDecimal("height_cm"));
                System.out.println("activity_level: " + rs.getString("activity_level"));
                System.out.println("------------------------------------------------");
            }

        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
