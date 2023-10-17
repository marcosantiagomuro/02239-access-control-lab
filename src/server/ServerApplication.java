package server;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServerApplication {
	public static void main(String[] args) throws RemoteException {
		Registry registry = LocateRegistry.createRegistry(9999);
		registry.rebind("test", new PrintServer());
	}
}
