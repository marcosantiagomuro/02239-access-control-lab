package com.datasec.remoteInterface;

import com.datasec.server.Session;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface PrinterCommandsInterface extends Remote {

    String authenticate(String userName, String password) throws RemoteException;

    String logOut(String sessionId) throws RemoteException;

    String start(String printer, String sessionId) throws RemoteException;

    String stop(String printer, String sessionId) throws RemoteException;

    String restart(String printer, String sessionId) throws RemoteException;

    String print(String filename, String printer, String sessionId) throws RemoteException;

    String queue(String printer, String sessionId) throws RemoteException;

    String topQueue(String printer, int job, String sessionId) throws RemoteException;

    String status(String printer, String sessionId) throws RemoteException;

    String readAllConfigs(String printer, String sessionId) throws RemoteException; // should be more printAllConfigs

    String readConfig(String printer, String parameter, String sessionId) throws RemoteException;

    String setConfig(String printer, String parameter, String value, String sessionId) throws RemoteException;

    Boolean isPrinterRunning(String printer) throws RemoteException;
}
