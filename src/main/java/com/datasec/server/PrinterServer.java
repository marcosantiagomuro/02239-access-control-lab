package com.datasec.server;

import com.datasec.utils.enums.PrinterParamsEnum;
import com.datasec.utils.Utils;
import org.apache.commons.lang3.StringUtils;
import com.datasec.remoteInterface.PrinterCommandsInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;

import static com.datasec.utils.Utils.checkAndPutValueInConfig;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class PrinterServer extends UnicastRemoteObject implements PrinterCommandsInterface {

    private static final Logger logger = LogManager.getLogger(ServerApplication.class);

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
        logger.info("Client called 'start' method");
        if (!(StringUtils.isEmpty(printer) || StringUtils.isBlank(printer))) {
            for (Printer pr : printersConnectedToServer) {
                if (printer.equals(pr.getNamePrinter()) && !pr.getIsRunning()) {
                    pr.setIsRunning(true);
//                    logger.info(printer + ": printer started again...");
                    return printer + ": printer started again...";
                }
            }
            logger.error("Method 'start' failed to execute. Error: " + printer + " is not connected to server / does not exist");
            return printer + " is not connected to server / does not exist";
        }
        logger.error("Method 'start' failed to execute. Error: printer name not accepted (either blank or empty");
        return "printer name not accepted (either blank or empty)";
    }

    @Override
    public String stop(String printer) throws RemoteException {
        logger.info("Client called 'stop' method");
        if (!(StringUtils.isEmpty(printer) || StringUtils.isBlank(printer))) {
            for (Printer pr : printersConnectedToServer) {
                if (printer.equals(pr.getNamePrinter()) && pr.getIsRunning()) {
                    pr.setIsRunning(false);
//                    logger.info(printer + ": printer stopped...");
                    return printer + ": printer stopped...";
                }
            }
            logger.error("Method 'stop' failed to execute. Error: " + printer + " is not connected to server / does not exist");
            return printer + " is not connected to server / does not exist";
        }
        logger.error("Method 'stop' failed to execute. Error: printer name not accepted (either blank or empty");
        return "printer name not accepted (either blank or empty)";
    }

    @Override
    public String restart(String printer) throws RemoteException {
        logger.info("Client called 'restart' method");
        if (!(StringUtils.isEmpty(printer) || StringUtils.isBlank(printer))) {
            for (Printer pr : printersConnectedToServer) {
                if (printer.equals(pr.getNamePrinter()) && pr.getIsRunning()) {
                    printersConnectedToServer.remove(pr);
                    printersConnectedToServer.add(new Printer(printer));
//                    logger.info(printer + ": printer stopped... and restarted");
                    return printer + ": printer stopped... and restarted";
                }
            }
            logger.error("Method 'restart' failed to execute. Error: " + printer + " name not recognised across already running printers");
            return printer + " name not recognised across already running printers";
        }
        logger.error("Method 'stop' failed to execute. Error: printer name not accepted (either blank or empty");
        return "printer name not accepted (either blank or empty)";
    }


    @Override
    public String print(String filename, String printer) {
        logger.info("Client called 'print' method. File: " + filename + ", Printer: " + printer);
        if (!(StringUtils.isEmpty(filename) || StringUtils.isBlank(filename))) {
            for (Printer pr : printersConnectedToServer) {
                if (pr.getIsRunning() && printer.equals(pr.getNamePrinter())) {
//                    logger.info(pr.print(filename));
                    return pr.print(filename);
                }
            }
            logger.error("Method 'print' failed to execute. Error: " + printer + " is not connected to server / does not exist");
            return printer + ": printer not found connected to server!";
        }
        logger.error("Method 'print' failed to execute. Error: filename not accepted (either blank or empty");
        return "filename not accepted (either blank or empty)";
    }

    @Override
    public String queue(String printer) {
        logger.info("Client called 'queue' method for printer: " + printer);
        if (!(StringUtils.isEmpty(printer) || StringUtils.isBlank(printer))) {
            for (Printer pr : printersConnectedToServer) {
                if (pr.getIsRunning() && printer.equals(pr.getNamePrinter())) {
                    StringBuilder output = new StringBuilder();
                    output.append(pr.getNamePrinter()).append(": QUEUE \n");
                    for (JobInQueue jobInQueue : pr.getQueuePrinter()) {
                        output.append(jobInQueue.getJobNumber()).append(" : ").append(jobInQueue.getJobFileName()).append("\n");
                    }
                    ArrayList<JobInQueue> printerQueue = pr.getQueuePrinter();
//                    printerQueue.forEach(item -> {
//                        output += item.getJobNumber() + " : " + item.getJobFileName() + "\n";
//                    });
                    return output.toString();
                }
            }
            logger.error("Method 'queue' failed to execute. Error: " + printer + " name not recognised across already running printers");
            return printer + " name not recognised across already running printers";
        }
        logger.error("Method 'print' failed to execute. Error: filename not accepted (either blank or empty");
        return "printer name not accepted (either blank or empty)";
    }

    @Override
    public String topQueue(String printer, int job) {
        logger.info("Client called 'topQueue' method for printer: " + printer + ", Job: " + job);
        if (!(StringUtils.isEmpty(printer) || StringUtils.isBlank(printer))) {
            for (Printer pr : printersConnectedToServer) {
                if (pr.getIsRunning() && printer.equals(pr.getNamePrinter())) {
                    for (JobInQueue jobInQueue : pr.getQueuePrinter()) {
                        if (jobInQueue.getJobNumber().equals(job)) {
                            pr.getQueuePrinter().remove(jobInQueue);
                            pr.getQueuePrinter().add(0, jobInQueue);
//                            logger.info("job: " + job + " moved to top of the queue");
                            return "job: " + job + " moved to top of the queue";
                        }
                    }
                    logger.error("Method 'topQueue' failed to execute. Error: job: " + job + " not found");
                    return "job: " + job + " not found";
                }
            }
            logger.error("Method 'topQueue' failed to execute. Error: " + printer + " name not recognised across already running printers");
            return printer + " name not recognised across already running printers";
        }
        logger.error("Method 'topQueue' failed to execute. Error: filename not accepted (either blank or empty");
        return "printer name not accepted (either blank or empty)";
    }

    @Override
    public String status(String printer) {
        logger.info("Client called 'status' method for printer: " + printer);
        for (Printer pr : printersConnectedToServer) {
            if (pr.getIsRunning() && printer.equals(pr.getNamePrinter())) {
//                logger.info(pr.getStatusPrinter().toString());
                return pr.getStatusPrinter().toString();
            }
        }
        logger.error("Method 'print' failed to execute. Error: " + printer + " printer not found connected to server!");
        return printer + ": printer not found connected to server!";
    }

    @Override
    public String readAllConfigs(String printer) throws RemoteException {
        logger.info("Client called 'readAllConfigs' method for printer: " + printer);
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
//                logger.info(stringBuilder.toString());
                return stringBuilder.toString();
            }
        }
        logger.error("Method 'readAllConfigs' failed to execute. Error: " + printer + " printer not found connected to server!");
        return printer + ": printer not found connected to server!";
    }

    @Override
    public String readConfig(String printer, String parameter) {
        logger.info("Client called 'readConfig' method for parameter: " + parameter);
        for (Printer pr : printersConnectedToServer) {
            if (pr.getIsRunning() && printer.equals(pr.getNamePrinter())) {
                try {
                    String value = pr.getConfigPrinter().get(Utils.fromStringtoPrinterParamEnum(parameter)).toString();
//                    logger.info(parameter + ": " + value);
                    return parameter + ": " + value;
                } catch (Exception e) {
                }
            }
        }
        logger.error("Method 'readConfig' failed to execute. Error: " + printer + " printer not found connected to server!");
        return printer + ": printer not found connected to server!";
    }


    @Override
    public String setConfig(String printer, String parameter, String value) {
        logger.info("Client called 'setConfig' method. Parameter: " + parameter + ", Value: " + value);
//        System.out.println("santi1");
        for (Printer pr : printersConnectedToServer) {
            if (pr.getIsRunning() && printer.equals(pr.getNamePrinter())) {
//                System.out.println("santi2");
                if (checkAndPutValueInConfig(pr.getConfigPrinter(), parameter, value)) {
//                    logger.info("[ " + parameter + " , " + value + " ] has been set in printer: " + printer);
                    return "[ " + parameter + " , " + value + " ] has been set in printer: " + printer;
                }
                logger.error("Method 'setConfig' failed to execute. Error: given parameter or value are not accepted");
                return "given parameter or value are not accepted";
            }
        }
        logger.error("Method 'setConfig' failed to execute. Error: " + printer + " printer not found connected to server!");
        return printer + ": printer not found connected to server!";
    }
}
