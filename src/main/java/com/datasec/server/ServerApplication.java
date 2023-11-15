package com.datasec.server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.SQLException;
import java.util.Optional;
import com.datasec.database.DatabaseConfig;
import com.datasec.database.PermissionUser;
import com.datasec.database.User;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import static com.datasec.database.User.USERID;

public class ServerApplication implements Remote {
    private static final Logger logger = LogManager.getLogger(ServerApplication.class);
    Registry registry;

    public void startPrinterServer(String autherizationMethod) {
        try {
            registry = LocateRegistry.createRegistry(4002);
            String name = "printerServerName1";
            registry.rebind(name, new PrinterServer(autherizationMethod));
            logger.info("Server successfully started.");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws RemoteException {
        String method = args.length == 1 ? args[0] : null;
        if (method == null) {
            logger.info("Configuring default authorization (ACL)");
            ServerApplication serverApplication = new ServerApplication();
            serverApplication.startPrinterServer("acl");
        } else if (method.toLowerCase().equals("acl")) {
            logger.info("Configuring ACL authorization.");
            ServerApplication serverApplication = new ServerApplication();
            serverApplication.startPrinterServer("acl");
        } else if (method.toLowerCase().equals("rbac")) {
            logger.info("Configuring RBAC authorization.");
            ServerApplication serverApplication = new ServerApplication();
            serverApplication.startPrinterServer("rbac");
        } else {
            System.out.println("Error: pass argument: [acl] or [rbac].");
        }
        DatabaseConfig.createDatabase();
    }
}
