package server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IPrintServer extends Remote {
	public String echo() throws RemoteException;
}
