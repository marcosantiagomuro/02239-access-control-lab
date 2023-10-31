package com.datasec.server;

import com.datasec.utils.SystemException;
import com.datasec.utils.enums.PrinterParamsEnum;
import com.datasec.utils.Utils;
import org.apache.commons.lang3.StringUtils;
import com.datasec.remoteInterface.PrinterCommandsInterface;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

import static com.datasec.utils.Utils.checkAndPutValueInConfig;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class PrinterServer extends UnicastRemoteObject implements PrinterCommandsInterface {

    private static final Logger logger = LogManager.getLogger(ServerApplication.class);

    private SessionManager sessionManager;

    public PrinterServer(SessionManager sessionManager) throws RemoteException {
        this.sessionManager = sessionManager;
    }

    protected PrinterServer() throws RemoteException {
        super();
        sessionManager = new SessionManager();
        printersConnectedToServer.add(new Printer("printer1"));
        printersConnectedToServer.add(new Printer("printer2"));
        printersConnectedToServer.add(new Printer("printer3"));
        printersConnectedToServer.add(new Printer("printer4"));
        sessionManager.startSessionCleanupDaemon();
    }

    @Override
    public String authenticate(String userName, String password) throws RemoteException {
        boolean auth = Authentication.authenticateUser(userName, password);
        if (auth) {
            Session session = sessionManager.addNewSession(userName);
            return session.getSessionId();
        } else {
            return null;
        }
    }

    @Override
    public String logOut(String sessionId) throws RemoteException {
        Session session = sessionManager.removeSession(sessionId);
        //TODO REMOVE SESSIONID
        return "logOut. username: " + session.getUserId() + " , sessionId: " + session.getSessionId() + "\n" ;
    }

    @Override
    public String start(String printer, String sessionId) throws RemoteException {
        Session sessionInfo = sessionManager.getSession(sessionId);
        if (Optional.ofNullable(sessionInfo).isPresent()) {
            sessionManager.getSession(sessionId).setLastInteraction(System.currentTimeMillis());
            if (!(StringUtils.isEmpty(printer) || StringUtils.isBlank(printer))) {
                for (Printer pr : printersConnectedToServer) {
                    if (printer.equals(pr.getNamePrinter()) && !pr.getIsRunning()) {
                        pr.setIsRunning(true);
                        return printer + ": printer started again... \n";
                    }
                }
                return printer + " is not connected to server / does not exist \n";
            }
            return "printer name not accepted (either blank or empty) \n";
        }
        throw new SystemException("10", "ERROR_SESSION");
    }

    @Override
    public String stop(String printer, String sessionId) throws RemoteException {
        Session sessionInfo = sessionManager.getSession(sessionId);
        if (Optional.ofNullable(sessionInfo).isPresent()) {
            sessionManager.getSession(sessionId).setLastInteraction(System.currentTimeMillis());
            if (!(StringUtils.isEmpty(printer) || StringUtils.isBlank(printer))) {
                for (Printer pr : printersConnectedToServer) {
                    if (printer.equals(pr.getNamePrinter()) && pr.getIsRunning()) {
                        pr.setIsRunning(false);
                        return printer + ": printer stopped... \n";
                    }
                }
                return printer + " is not connected to server / does not exist \n";
            }
            return "printer name not accepted (either blank or empty) \n";
        }
        throw new SystemException("10", "ERROR_SESSION");
    }

    @Override
    public String restart(String printer, String sessionId) throws RemoteException {
        Session sessionInfo = sessionManager.getSession(sessionId);
        if (Optional.ofNullable(sessionInfo).isPresent()) {
            sessionManager.getSession(sessionId).setLastInteraction(System.currentTimeMillis());
            if (!(StringUtils.isEmpty(printer) || StringUtils.isBlank(printer))) {
                for (Printer pr : printersConnectedToServer) {
                    if (printer.equals(pr.getNamePrinter()) && pr.getIsRunning()) {
                        printersConnectedToServer.remove(pr);
                        printersConnectedToServer.add(new Printer(printer));
                        return printer + ": printer stopped... and restarted \n";
                    }
                }
                return printer + " name not recognised across already running printers \n";
            }
            return "printer name not accepted (either blank or empty) \n";
        }
        throw new SystemException("10", "ERROR_SESSION");
    }

    @Override
    public String print(String filename, String printer, String sessionId) {
        Session sessionInfo = sessionManager.getSession(sessionId);
        if (Optional.ofNullable(sessionInfo).isPresent()) {
            sessionManager.getSession(sessionId).setLastInteraction(System.currentTimeMillis());
            if (!(StringUtils.isEmpty(filename) || StringUtils.isBlank(filename))) {
                for (Printer pr : printersConnectedToServer) {
                    if (pr.getIsRunning() && printer.equals(pr.getNamePrinter())) {
                        return pr.print(filename);
                    }
                }
                return printer + ": printer not found connected to server! \n";
            }
            return "printer name not accepted (either blank or empty) \n";
        }
        throw new SystemException("10", "ERROR_SESSION");
    }

    @Override
    public String queue(String printer, String sessionId) {
        Session sessionInfo = sessionManager.getSession(sessionId);
        if (Optional.ofNullable(sessionInfo).isPresent()) {
            sessionManager.getSession(sessionId).setLastInteraction(System.currentTimeMillis());
            if (!(StringUtils.isEmpty(printer) || StringUtils.isBlank(printer))) {
                for (Printer pr : printersConnectedToServer) {
                    if (pr.getIsRunning() && printer.equals(pr.getNamePrinter())) {
                        StringBuilder output = new StringBuilder();
                        output.append(pr.getNamePrinter()).append(": QUEUE \n");
                        for (JobInQueue jobInQueue : pr.getQueuePrinter()) {
                            output.append(jobInQueue.getJobNumber()).append(" : ").append(jobInQueue.getJobFileName())
                                    .append("\n");
                        }
                        ArrayList<JobInQueue> printerQueue = pr.getQueuePrinter();
                        // printerQueue.forEach(item -> {
                        // output += item.getJobNumber() + " : " + item.getJobFileName() + "\n";
                        // });
                        System.out.println(output);
                        return output.toString();
                    }
                }
                return printer + " name not recognised across already running printers \n";
            }
            return "printer name not accepted (either blank or empty) \n";
        }
        throw new SystemException("10", "ERROR_SESSION");
    }

    @Override
    public String topQueue(String printer, int job, String sessionId) {
        Session sessionInfo = sessionManager.getSession(sessionId);
        if (Optional.ofNullable(sessionInfo).isPresent()) {
            sessionManager.getSession(sessionId).setLastInteraction(System.currentTimeMillis());
            if (!(StringUtils.isEmpty(printer) || StringUtils.isBlank(printer))) {
                for (Printer pr : printersConnectedToServer) {
                    if (pr.getIsRunning() && printer.equals(pr.getNamePrinter())) {
                        for (JobInQueue jobInQueue : pr.getQueuePrinter()) {
                            if (jobInQueue.getJobNumber().equals(job)) {
                                pr.getQueuePrinter().remove(jobInQueue);
                                pr.getQueuePrinter().add(0, jobInQueue);
                                return "job: " + job + " moved to top of the queue \n";
                            }
                        }
                        return "job: " + job + " not found \n";
                    }
                }
                return printer + " name not recognised across already running printers \n";
            }
            return "printer name not accepted (either blank or empty) \n";
        }
        throw new SystemException("10", "ERROR_SESSION");
    }

    @Override
    public String status(String printer, String sessionId) {
        Session sessionInfo = sessionManager.getSession(sessionId);
        if (Optional.ofNullable(sessionInfo).isPresent()) {
            sessionManager.getSession(sessionId).setLastInteraction(System.currentTimeMillis());
            for (Printer pr : printersConnectedToServer) {
                if (pr.getIsRunning() && printer.equals(pr.getNamePrinter())) {
                    return pr.getStatusPrinter().toString();
                }
            }
            return printer + ": printer not found connected to server! \n";
        }
        throw new SystemException("10", "ERROR_SESSION");
    }

    @Override
    public String readAllConfigs(String printer, String sessionId) throws RemoteException {
        Session sessionInfo = sessionManager.getSession(sessionId);
        if (Optional.ofNullable(sessionInfo).isPresent()) {
            sessionManager.getSession(sessionId).setLastInteraction(System.currentTimeMillis());
            for (Printer pr : printersConnectedToServer) {
                if (pr.getIsRunning() && printer.equals(pr.getNamePrinter())) {
                    // Initialize a StringBuilder to build the string
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("ALL PARAMS: \n");
                    // Iterate through the HashMap and append key-value pairs to the StringBuilder
                    for (HashMap.Entry<PrinterParamsEnum, Object> entry : pr.getConfigPrinter().entrySet()) {
                        stringBuilder.append(entry.getKey().toString());
                        stringBuilder.append(": ");
                        stringBuilder.append(entry.getValue());
                        stringBuilder.append("\n");
                    }

                    // Remove the trailing ", " if present
                    if (stringBuilder.length() > 0) {
                        stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length());
                    }
                    stringBuilder.append("\n");

                    // Convert the StringBuilder to a String
                    return stringBuilder.toString();
                }
            }
            return printer + ": printer not found connected to server! \n";
        }
        throw new SystemException("10", "ERROR_SESSION");
    }

    @Override
    public String readConfig(String printer, String parameter, String sessionId) {
        Session sessionInfo = sessionManager.getSession(sessionId);
        if (Optional.ofNullable(sessionInfo).isPresent()) {
            sessionManager.getSession(sessionId).setLastInteraction(System.currentTimeMillis());
            for (Printer pr : printersConnectedToServer) {
                if (pr.getIsRunning() && printer.equals(pr.getNamePrinter())) {
                    try {
                        String value = pr.getConfigPrinter().get(Utils.fromStringtoPrinterParamEnum(parameter)).toString();
                        return parameter + ": " + value + "\n";
                    } catch (Exception e) {
                    }
                }
            }
            return printer + ": printer not found connected to server! \n";
        }
        throw new SystemException("10", "ERROR_SESSION");
    }

    @Override
    public String setConfig(String printer, String parameter, String value, String sessionId) {
        Session sessionInfo = sessionManager.getSession(sessionId);
        if (Optional.ofNullable(sessionInfo).isPresent()) {
            sessionManager.getSession(sessionId).setLastInteraction(System.currentTimeMillis());
            for (Printer pr : printersConnectedToServer) {
                if (pr.getIsRunning() && printer.equals(pr.getNamePrinter())) {
                    if (checkAndPutValueInConfig(pr.getConfigPrinter(), parameter, value)) {
                        return "[ " + parameter + " , " + value + " ] has been set in printer: " + printer + "\n";
                    }
                    return "given parameter or value are not accepted \n";
                }
            }
            return printer + ": printer not found connected to server! \n";
        }
        throw new SystemException("10", "ERROR_SESSION");
    }

    @Override
    public Boolean isPrinterRunning(String printer) {
        for (Printer pr : printersConnectedToServer) {
            if (printer.equals(pr.getNamePrinter())) {
                return pr.getIsRunning();
            }
        }
        return false;
    }
}
