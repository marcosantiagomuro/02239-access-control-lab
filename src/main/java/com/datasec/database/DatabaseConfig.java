package com.datasec.database;

import com.datasec.server.ServerApplication;
import com.datasec.server.SessionManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;


public class DatabaseConfig {

    private static final Logger logger = LogManager.getLogger(ServerApplication.class);

    private final static String DATABASE_URL = "jdbc:sqlite:authLabDatabase.db";

    private static ConnectionSource connectionSource;

    public static void createDatabase() {
        try {
            Connection connection = DriverManager.getConnection(DATABASE_URL);
            if (connection != null) {
                logger.info("SQLite database created successfully or was already there.");
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
