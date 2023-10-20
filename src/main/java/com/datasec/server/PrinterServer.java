package com.datasec.server;

import org.apache.commons.lang3.StringUtils;
import com.datasec.remoteInterface.IPrinterServer;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class PrinterServer extends UnicastRemoteObject implements IPrinterServer {

    ArrayList<Printer> printersConnectedToServer = new ArrayList<Printer>();

    // private static final long serialVersionUID = 1L;

    protected PrinterServer() throws RemoteException {
        super();
        printersConnectedToServer.add(new Printer("printer1"));
        printersConnectedToServer.add(new Printer("printer2"));
        printersConnectedToServer.add(new Printer(" "));
    }

    @Override
    public String echo() throws RemoteException {
        // throw new UnsupportedOperationException("Not supported.");
        return "It's working :-)";
    }

    @Override
    public String writeToUpperCase(String s) throws RemoteException {
        return s.toUpperCase();
    }

    @Override
    public void print(String filename, String printer) {
        if (!(StringUtils.isEmpty(filename) || StringUtils.isBlank(filename))) {
            for (Printer pr : printersConnectedToServer) {
                if (printer.equals(pr.getNamePrinter())) {
                    pr.print(filename);
                }
            }
        }

    }

    @Override
    public String queue(String printer) {
        return null;
    }

    @Override
    public String topQueue(String printer, int job) {
        return null;
    }

    @Override
    public String status(String printer) {
        return null;
    }

    @Override
    public String readConfig(String parameter) {
        return null;
    }

    @Override
    public String setConfig(String parameter, String value) {
        return null;
    }
}
