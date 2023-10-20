package com.datasec.server;

import org.apache.commons.lang3.StringUtils;
import com.datasec.remoteInterface.IServerApplication;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServerApplication implements IServerApplication {
    Registry registry;

    @Override
    public void startPrinterServer(String printerServerName) {
        try {
            Registry registry = LocateRegistry.createRegistry(4000);
            String name = StringUtils.isEmpty(printerServerName) || StringUtils.isBlank(printerServerName)
                    ? "genericPrinterServerName"
                    : printerServerName;
            registry.rebind(name, new PrinterServer());
        } catch (RemoteException e) {
            // todo to imporove
            e.printStackTrace();
        }
    }

    @Override
    public void stopServerApplication(String printerServerName) {

    }

    @Override
    public void restartServerApplication(String prtinerServerName) {

    }

    public static void main(String[] args) throws RemoteException {
        ServerApplication marcoServerApplication = new ServerApplication();
        marcoServerApplication.startPrinterServer("server1");

    }

}
