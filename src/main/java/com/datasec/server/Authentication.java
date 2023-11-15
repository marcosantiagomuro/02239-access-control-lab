package com.datasec.server;

import com.datasec.database.DatabaseConfig;
import com.datasec.database.User;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.password4j.Password;
import java.sql.SQLException;
import java.util.Optional;

import static com.datasec.database.User.USERID;

public class Authentication {
    private static final String pepper = "bFhVcnFiWndYV3hUZk1PeQ==";

    /**
     * This method authenticates a username and password.
     * 
     * @param userName the unique username
     * @param password cleartext password
     * @return true if accepted, false if not
     */
    public static boolean authenticateUser(String userName, String password) {

        ConnectionSource connectionSource = DatabaseConfig.getConnectionSource();

        try {
            Dao<User, Integer> userDao = DaoManager.createDao(connectionSource, User.class);

            User user = userDao.queryForFirst(userDao.queryBuilder()
                    .where()
                    .eq(USERID, userName)
                    .prepare());
            if (!Optional.ofNullable(user).isPresent()) {
                return false;
            }
            return Password.check(password, user.getPassword()).addPepper(pepper).withScrypt();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConfig.closeConnection();
        }
        return false;
    }
}
