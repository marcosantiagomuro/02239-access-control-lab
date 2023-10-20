package com.datasec.remoteInterface;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IServerApplication extends Remote {

    void startPrinterServer(String printerServerName) throws RemoteException;
    void stopServerApplication(String printerServerName) throws RemoteException;
    void restartServerApplication(String printerServerName) throws RemoteException;
}
