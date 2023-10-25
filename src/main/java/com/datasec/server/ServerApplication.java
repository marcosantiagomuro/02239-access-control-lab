package com.datasec.server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServerApplication implements Remote {
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

    }

}
