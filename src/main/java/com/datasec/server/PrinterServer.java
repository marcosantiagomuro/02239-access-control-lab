package com.datasec.server;

import com.datasec.utils.enums.PrinterParamsEnum;
import com.datasec.utils.Utils;
import com.datasec.utils.enums.PrinterStatusEnum;
import org.apache.commons.lang3.StringUtils;
import com.datasec.remoteInterface.PrinterCommandsInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;

import static com.datasec.utils.Utils.checkAndPutValueInConfig;

public class PrinterServer extends UnicastRemoteObject implements PrinterCommandsInterface {

    ArrayList<Printer> printersConnectedToServer = new ArrayList<Printer>();


    protected PrinterServer() throws RemoteException {
        super();
        printersConnectedToServer.add(new Printer("printer1"));
        printersConnectedToServer.add(new Printer("printer2"));
        printersConnectedToServer.add(new Printer("printer3"));
        printersConnectedToServer.add(new Printer("printer4"));
    }


    @Override
    public String start(String printer) throws RemoteException {
        if (!(StringUtils.isEmpty(printer) || StringUtils.isBlank(printer))) {
            for (Printer pr : printersConnectedToServer) {
                if (printer.equals(pr.getNamePrinter()) && !pr.getIsRunning()) {
                    pr.setIsRunning(true);
                    return printer + ": printer started again...";
                }
            }
            return printer + " is not connected to server / does not exist";
        }
        return "printer name not accepted (either blank or empty)";
    }

    @Override
    public String stop(String printer) throws RemoteException {
        if (!(StringUtils.isEmpty(printer) || StringUtils.isBlank(printer))) {
            for (Printer pr : printersConnectedToServer) {
                if (printer.equals(pr.getNamePrinter()) && pr.getIsRunning()) {
                    pr.setIsRunning(false);
                    return printer + ": printer stopped...";
                }
            }
            return printer + " is not connected to server / does not exist";
        }
        return "printer name not accepted (either blank or empty)";
    }

    @Override
    public String restart(String printer) throws RemoteException {
        if (!(StringUtils.isEmpty(printer) || StringUtils.isBlank(printer))) {
            for (Printer pr : printersConnectedToServer) {
                if (printer.equals(pr.getNamePrinter()) && pr.getIsRunning()) {
                    printersConnectedToServer.remove(pr);
                    printersConnectedToServer.add(new Printer(printer));
                    return printer + ": printer stopped... and restarted";
                }
            }
            return printer + " name not recognised across already running printers";
        }
        return "printer name not accepted (either blank or empty)";
    }


    @Override
    public void print(String filename, String printer) {
        if (!(StringUtils.isEmpty(filename) || StringUtils.isBlank(filename))) {
            boolean isPrinterFound = false;
            for (Printer pr : printersConnectedToServer) {
                if (pr.getIsRunning() && printer.equals(pr.getNamePrinter())) {
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
    public String queue(String printer) {
        if (!(StringUtils.isEmpty(printer) || StringUtils.isBlank(printer))) {
            for (Printer pr : printersConnectedToServer) {
                if (pr.getIsRunning() && printer.equals(pr.getNamePrinter())) {
                    ArrayList<JobInQueue> printerQueue = pr.getQueuePrinter();
                    StringBuilder output = new StringBuilder();
                    for (JobInQueue jobInQueue : pr.getQueuePrinter()) {
                        output.append(jobInQueue.getJobNumber()).append(" : ").append(jobInQueue.getJobFileName()).append("\n");
                    }
//                    printerQueue.forEach(item -> {
//                        output += item.getJobNumber() + " : " + item.getJobFileName() + "\n";
//                    });
                    return output.toString();
                }
            }
            return printer + " name not recognised across already running printers";
        }
        return "printer name not accepted (either blank or empty)";
    }

    @Override
    public String topQueue(String printer, int job) {
        return null;
    }

    @Override
    public String status(String printer) {
        for (Printer pr : printersConnectedToServer) {
            if (pr.getIsRunning() && printer.equals(pr.getNamePrinter())) {
                return pr.getStatusPrinter().toString();
            }
        }
        return printer + ": printer not found connected to server!";
    }

    @Override
    public String readAllConfigs(String printer) throws RemoteException {
        boolean isPrinterFound = false;
        for (Printer pr : printersConnectedToServer) {
            if (pr.getIsRunning() && printer.equals(pr.getNamePrinter())) {
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
            if (pr.getIsRunning() && printer.equals(pr.getNamePrinter())) {
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
            if (pr.getIsRunning() && printer.equals(pr.getNamePrinter())) {
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
