package com.datasec.remoteInterface;

import com.datasec.server.Session;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface PrinterCommandsInterface extends Remote {

    String authenticate(String userName, String password) throws RemoteException;

    String logOut(String sessionId) throws RemoteException;

    String start(String printer, String sessionId) throws Exception;

    String stop(String printer, String sessionId) throws Exception;

    String restart(String printer, String sessionId) throws Exception;

    String print(String filename, String printer, String sessionId) throws Exception;

    String queue(String printer, String sessionId) throws Exception;

    String topQueue(String printer, int job, String sessionId) throws Exception;

    String status(String printer, String sessionId) throws Exception;

    String readAllConfigs(String printer, String sessionId) throws Exception; // should be more printAllConfigs

    String readConfig(String printer, String parameter, String sessionId) throws Exception;

    String setConfig(String printer, String parameter, String value, String sessionId) throws Exception;

    Boolean isPrinterRunning(String printer) throws RemoteException;
}
