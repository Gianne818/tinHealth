package org.tin.oop2_capstone.database;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL = "database;
    private static final String USER = System.getenv("db_user") != null ? System.getenv("db_user") : "root";
    private static final String PASSWORD = System.getenv("db_pass") != null ? System.getenv("db_pass") : "";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}