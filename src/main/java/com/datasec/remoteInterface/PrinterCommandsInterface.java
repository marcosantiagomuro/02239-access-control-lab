package com.datasec.remoteInterface;

import com.datasec.server.JobInQueue;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface PrinterCommandsInterface extends Remote {

    String start(String printer) throws RemoteException;

    String stop(String printer) throws RemoteException;

    String restart(String printer) throws RemoteException;

    String print(String filename, String printer) throws RemoteException;

    String queue(String printer) throws RemoteException;

    String topQueue(String printer, int job) throws RemoteException;

    String status(String printer) throws RemoteException;

    String readAllConfigs(String printer) throws RemoteException;  //should be more printAllConfigs

    String readConfig(String printer, String parameter) throws RemoteException;

    String setConfig(String printer, String parameter, String value) throws RemoteException;
}
