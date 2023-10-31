package com.datasec.server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.SQLException;
import java.util.Optional;

import com.datasec.database.DatabaseConfig;
import com.datasec.database.User;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import static com.datasec.database.User.USERID_COLUMN_NAME;

public class ServerApplication implements Remote {
    private static final Logger logger = LogManager.getLogger(ServerApplication.class);
    Registry registry;

    public void startPrinterServer() {
        try {
            registry = LocateRegistry.createRegistry(4002);
            String name = "printerServerName1";
            registry.rebind(name, new PrinterServer());
            logger.info("Server successfully started.");
        } catch (RemoteException e) {
            // todo to imporove
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws RemoteException {
        ServerApplication serverApplication = new ServerApplication();
        serverApplication.startPrinterServer();
        DatabaseConfig.createDatabase();

        ConnectionSource connectionSource = DatabaseConfig.getConnectionSource();
        try {
            Dao<User, Integer> userDao = DaoManager.createDao(connectionSource, User.class);

            // Perform database operations using userDao
            if (!userDao.isTableExists()) {
                TableUtils.createTable(connectionSource, User.class);
            } // createsTable if it doesn't exist
            User user1 = new User("user1",
                    "$100801$859vkX/UYi8Tuz9zyEyDfQ==$PYMi5I0+2kjT6bbOOWd9gIyA7u6apvcJpcPjU0R6MsV8Xu+CrJhGv5XhiGy64/HjArmjeKJTIEgnLbLUSjS6Jg==");
            User userToCheck = userDao.queryForFirst(userDao.queryBuilder()
                    .where()
                    .eq(USERID_COLUMN_NAME, "user1")
                    .prepare());
            if (!Optional.ofNullable(userToCheck).isPresent()) {
                userDao.create(user1);
            }
            System.out.println(user1.getUser());
            User user2 = new User("user2",
                    "$100801$IEu0Wzu2FqqVpIIQyQ03KQ==$VhmQu8Uiy8ecNVpTz1iQCPj/UoUd8fAOSzp2N2SlFsVYH2xruAC3wGhElRwa6xx1OEMLvYuNOsYFZCIxoBh0YQ==");
            userToCheck = userDao.queryForFirst(userDao.queryBuilder()
                    .where()
                    .eq(USERID_COLUMN_NAME, "user2")
                    .prepare());
            if (!Optional.ofNullable(userToCheck).isPresent()) {
                userDao.create(user2);
            }
            System.out.println(user2.getUser());

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConfig.closeConnection();
        }

    }
}