package server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class PrintServer extends UnicastRemoteObject implements IPrintServer {

	//private static final long serialVersionUID = 1L;

	protected PrintServer() throws RemoteException {
		super();
	}

	@Override
	public String echo() throws RemoteException {
		//throw new UnsupportedOperationException("Not supported.");
		return "It's working :-)";
	}
}
