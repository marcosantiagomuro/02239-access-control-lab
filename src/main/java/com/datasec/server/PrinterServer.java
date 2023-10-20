package com.datasec.server;

import com.datasec.utils.enums.PrinterParamsEnum;
import com.datasec.utils.Utils;
import org.apache.commons.lang3.StringUtils;
import com.datasec.remoteInterface.IPrinterServer;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;

import static com.datasec.utils.Utils.checkAndPutValueInConfig;
import static com.datasec.utils.Utils.configParamExists;

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
            boolean isPrinterFound = false;
            for (Printer pr : printersConnectedToServer) {
                if (printer.equals(pr.getNamePrinter())) {
                    pr.print(filename);
                    isPrinterFound = true;
                }
            }
            if (!isPrinterFound) {
                System.out.println(printer + ": printer not found connected to server!");
            }
        }
    }

    @Override
    public ArrayList<JobInQueue> queue(String printer) {
        return null;
    }

    @Override
    public String topQueue(String printer, int job) {
        return null;
    }

    @Override
    public String status(String printer) {
        boolean isPrinterFound = false;
        for (Printer pr : printersConnectedToServer) {
            if (printer.equals(pr.getNamePrinter())) {
                return pr.getStatusPrinter().toString();
            }
        }
        return printer + ": printer not found connected to server!";
    }

    @Override
    public String readAllConfigs(String printer) throws RemoteException {
        boolean isPrinterFound = false;
        for (Printer pr : printersConnectedToServer) {
            if (printer.equals(pr.getNamePrinter())) {
                // Initialize a StringBuilder to build the string
                StringBuilder stringBuilder = new StringBuilder();

                // Iterate through the HashMap and append key-value pairs to the StringBuilder
                for (HashMap.Entry<PrinterParamsEnum, Object> entry : pr.getConfigPrinter().entrySet()) {
                    stringBuilder.append(entry.getKey().toString());
                    stringBuilder.append(": ");
                    stringBuilder.append(entry.getValue());
                    stringBuilder.append(", \t");
                }

                // Remove the trailing ", " if present
                if (stringBuilder.length() > 0) {
                    stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length());
                }

                // Convert the StringBuilder to a String
                return stringBuilder.toString();
            }
        }
        return printer + ": printer not found connected to server!";
    }

    @Override
    public String readConfig(String printer, String parameter) {
        for (Printer pr : printersConnectedToServer) {
            if (printer.equals(pr.getNamePrinter())) {
                try {
                    String value = pr.getConfigPrinter().get(Utils.fromStringtoPrinterParamEnum(parameter)).toString();
                    return parameter + ": " + value;
                } catch (Exception e) {
                }
            }
        }
        return printer + ": printer not found connected to server!";
    }


    @Override
    public String setConfig(String printer, String parameter, String value) {
        System.out.println("santi1");
        for (Printer pr : printersConnectedToServer) {
            if (printer.equals(pr.getNamePrinter())) {
                System.out.println("santi2");
                if (checkAndPutValueInConfig(pr.getConfigPrinter(), parameter, value)) {
                    return "[ " + parameter + " , " + value + " ] has been set in printer: " + printer;
                }
                return "given parameter or value are not accepted";
            }
        }
        return printer + ": printer not found connected to server!";
    }
}
