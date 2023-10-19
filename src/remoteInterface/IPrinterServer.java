package remoteInterface;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IPrinterServer extends Remote {

	String echo() throws RemoteException;

	String writeToUpperCase(String s) throws RemoteException;

	void print(String filename, String printer) throws RemoteException;
	String queue(String printer) throws RemoteException;
	String topQueue(String printer, int job) throws RemoteException;
	String status(String printer) throws RemoteException;
	String readConfig(String parameter) throws RemoteException;
	String setConfig(String parameter, String value) throws RemoteException;
}
