package com.datasec.database;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;

public class DatabaseConfig {

    private final static String DATABASE_URL = "jdbc:sqlite:authLabDatabase.db";

    private static ConnectionSource connectionSource;

    public static void createDatabase() {
        try {
            Connection connection = DriverManager.getConnection(DATABASE_URL);
            if (connection != null) {
                System.out.println("SQLite database created successfully or was already there.");
            }
        } catch (SQLException e) {
            System.err.println("Error creating SQLite database: " + e.getMessage());
        }
    }

    public static ConnectionSource getConnectionSource() {
        if (!Optional.ofNullable(connectionSource).isPresent()) {
            try {
                connectionSource = new JdbcConnectionSource(DATABASE_URL);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return connectionSource;
    }

    public static void closeConnection() {
        if (Optional.ofNullable(connectionSource).isPresent()) {
            try {
                connectionSource.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }


}
