package com.datasec.server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class ServerApplication implements Remote {
    private static final Logger logger = LogManager.getLogger(ServerApplication.class);
    Registry registry;

    public void startPrinterServer() {
        try {
            registry = LocateRegistry.createRegistry(4002);
            String name = "printerServerName1";
            registry.rebind(name, new PrinterServer());
        } catch (RemoteException e) {
            // todo to imporove
            e.printStackTrace();
        }
    }


    public static void main(String[] args) throws RemoteException {
        ServerApplication serverApplication = new ServerApplication();
        serverApplication.startPrinterServer();
        logger.debug("Debug Message Logged !!!");
        logger.info("Info Message Logged !!!");
        //logger.error("Error Message Logged !!!", new NullPointerException("NullError"));
    }

}
