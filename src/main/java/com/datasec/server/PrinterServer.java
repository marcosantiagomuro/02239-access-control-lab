package com.datasec.server;

import com.datasec.utils.SystemException;
import com.datasec.utils.enums.CommandsActionEnum;
import com.datasec.utils.enums.PrinterParamsEnum;
import com.datasec.utils.Utils;
import org.apache.commons.lang3.StringUtils;
import com.datasec.remoteInterface.PrinterCommandsInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

import static com.datasec.server.UserAcessControl.hasUserPermission;
import static com.datasec.utils.Utils.checkAndPutValueInConfig;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PrinterServer extends UnicastRemoteObject implements PrinterCommandsInterface {
    private static final Logger logger = LogManager.getLogger(ServerApplication.class);

    ArrayList<Printer> printersConnectedToServer = new ArrayList<Printer>();

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

    public static boolean isValidUsername(String username) {
        String pattern = "^[a-zA-Z0-9_]+$";
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(username);
        return matcher.matches();
    }

    @Override
    public String authenticate(String userName, String password) throws RemoteException {
        if (isValidUsername(userName)) {
            boolean auth = Authentication.authenticateUser(userName, password);
            if (auth) {
                Session session = sessionManager.addNewSession(userName);

                logger.info("LOGIN user: " + session.getUserId() + " has performed action: login with new sessionID: "
                        + session.getSessionId());

                Collection<Session> activeSessions = sessionManager.getAllActiveSessions();

                String msg = "";
                for (Session sessions : activeSessions) {
                    Date date = new Date(sessions.getLastInteraction());
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String formattedDate = sdf.format(date);
                    msg += sessions.getUserId() + ": " + sessions.getSessionId() + ", lastInteraction: " + formattedDate
                            + "\n";
                }
                logger.info("Active sessions:\n" + msg);

                return session.getSessionId();
            } else {
                logger.error("user: " + userName + " failed to login");
                return null;
            }
        }
        logger.error("user: [ " + userName + " ] failed to login due to invalid username characters");
        throw new SystemException("20", "ERROR_USERNAME");
    }

    @Override
    public String logOut(String sessionId) throws RemoteException {
        Session session = sessionManager.removeSession(sessionId);
        logger.info("user: " + session.getUserId() + " has performed action: logout with sessionID: "
                + session.getSessionId());
        return "logOut. username: " + session.getUserId() + "\n";
    }

    private String executeCommand(String sessionId, String printer, CommandsActionEnum action,
            String... additionalParameters) throws Exception {

        Session sessionInfo = sessionManager.getSession(sessionId);
        if (!Optional.ofNullable(sessionInfo).isPresent())
            throw new SystemException("10", "ERROR_SESSION");

        sessionManager.getSession(sessionId).setLastInteraction(System.currentTimeMillis());

        if (!hasUserPermission(sessionInfo.getUserId(), action))
            return "user: " + sessionInfo.getUserId() + " does not have permission to perform this action \n";

        if (StringUtils.isEmpty(printer) || StringUtils.isBlank(printer))
            return "printer name not accepted (either blank or empty) \n";

        switch (action) {
            case start:
                for (Printer pr : printersConnectedToServer) {
                    if (printer.equals(pr.getNamePrinter()) && !pr.getIsRunning()) {
                        pr.setIsRunning(true);
                        logger.info("user: " + sessionInfo.getUserId()
                                + " has performed action: start printer: " + printer + " with sessionID: "
                                + sessionInfo.getSessionId());
                        return printer + ": printer started again... \n";
                    }
                }
                return printer + " is not connected to server / does not exist \n";

            case stop:
                for (Printer pr : printersConnectedToServer) {
                    if (printer.equals(pr.getNamePrinter()) && pr.getIsRunning()) {
                        pr.setIsRunning(false);
                        logger.info("user: " + sessionInfo.getUserId() + " has performed action: stop printer: "
                                + printer + " with sessionID: " + sessionInfo.getSessionId());
                        return printer + ": printer stopped... \n";
                    }
                }
                return printer + " is not connected to server / does not exist \n";

            case restart:
                for (Printer pr : printersConnectedToServer) {
                    if (printer.equals(pr.getNamePrinter()) && pr.getIsRunning()) {
                        printersConnectedToServer.remove(pr);
                        printersConnectedToServer.add(new Printer(printer));
                        logger.info("user: " + sessionInfo.getUserId()
                                + " has performed action: restart printer: "
                                + printer + " with sessionID: " + sessionInfo.getSessionId());
                        return printer + ": printer stopped... and restarted \n";
                    }
                }
                return printer + " name not recognised across already running printers \n";

            case print:
                String filename = additionalParameters.length > 0 ? additionalParameters[0] : null;
                if (filename != null) {
                    for (Printer pr : printersConnectedToServer) {
                        if (pr.getIsRunning() && printer.equals(pr.getNamePrinter())) {
                            logger.info("user: " + sessionInfo.getUserId()
                                    + " has performed action: print file: " + filename + " on printer: "
                                    + printer + " with sessionID: " + sessionInfo.getSessionId());
                            return pr.print(filename);
                        }
                    }
                    return printer + ": printer not found connected to server! \n";
                } else {
                    throw new IllegalArgumentException("Filename is required for print operation");
                }

            case queue:
                for (Printer pr : printersConnectedToServer) {
                    if (pr.getIsRunning() && printer.equals(pr.getNamePrinter())) {
                        StringBuilder output = new StringBuilder();
                        output.append(pr.getNamePrinter()).append(": QUEUE \n");
                        for (JobInQueue jobInQueue : pr.getQueuePrinter()) {
                            output.append(jobInQueue.getJobNumber()).append(" : ")
                                    .append(jobInQueue.getJobFileName())
                                    .append("\n");
                        }
                        logger.info(
                                "user: " + sessionInfo.getUserId()
                                        + " has performed action: queued for printer: "
                                        + printer + " with sessionID: " + sessionInfo.getSessionId());
                        return output.toString();
                    }
                }
                return printer + " name not recognised across already running printers \n";

            case topQueue:
                int job = additionalParameters.length > 0 ? Integer.parseInt(additionalParameters[0]) : null;
                for (Printer pr : printersConnectedToServer) {
                    if (pr.getIsRunning() && printer.equals(pr.getNamePrinter())) {
                        for (JobInQueue jobInQueue : pr.getQueuePrinter()) {
                            if (jobInQueue.getJobNumber().equals(job)) {
                                pr.getQueuePrinter().remove(jobInQueue);
                                pr.getQueuePrinter().add(0, jobInQueue);
                                logger.info("user: " + sessionInfo.getUserId()
                                        + " has performed action: moved job: " + job + " on printer: "
                                        + printer
                                        + " to the top with sessionID: " + sessionInfo.getSessionId());
                                return "job: " + job + " moved to top of the queue \n";
                            }
                        }
                        return "job: " + job + " not found \n";
                    }
                }
                return printer + " name not recognised across already running printers \n";

            case status:
                for (Printer pr : printersConnectedToServer) {
                    if (pr.getIsRunning() && printer.equals(pr.getNamePrinter())) {
                        logger.info(
                                "user: " + sessionInfo.getUserId()
                                        + " has performed action: read status of printer: "
                                        + printer + " with sessionID: " + sessionInfo.getSessionId());
                        return pr.getStatusPrinter().toString();
                    }
                }
                return printer + ": printer not found connected to server! \n";

            case readAllConfig:
                for (Printer pr : printersConnectedToServer) {
                    if (pr.getIsRunning() && printer.equals(pr.getNamePrinter())) {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("ALL PARAMS: \n");
                        for (HashMap.Entry<PrinterParamsEnum, Object> entry : pr.getConfigPrinter()
                                .entrySet()) {
                            stringBuilder.append(entry.getKey().toString());
                            stringBuilder.append(": ");
                            stringBuilder.append(entry.getValue());
                            stringBuilder.append("\n");
                        }

                        if (stringBuilder.length() > 0) {
                            stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length());
                        }
                        stringBuilder.append("\n");

                        logger.info("user: " + sessionInfo.getUserId()
                                + " has performed action: read all configs of printer: " + printer
                                + " with sessionID: "
                                + sessionInfo.getSessionId());
                        return stringBuilder.toString();
                    }
                }
                return printer + ": printer not found connected to server! \n";

            case readConfig:
                String param = additionalParameters.length > 0 ? additionalParameters[0] : null;
                for (Printer pr : printersConnectedToServer) {
                    if (pr.getIsRunning() && printer.equals(pr.getNamePrinter())) {
                        try {
                            String value = pr.getConfigPrinter()
                                    .get(Utils.fromStringtoPrinterParamEnum(param))
                                    .toString();
                            logger.info("user: " + sessionInfo.getUserId() + " has performed action: read "
                                    + param
                                    + " config of printer: " + printer + " with sessionID: "
                                    + sessionInfo.getSessionId());
                            return param + ": " + value + "\n";
                        } catch (Exception e) {
                        }
                    }
                }
                return printer + ": printer not found connected to server! \n";

            case setConfig:
                String parameters = additionalParameters.length > 0 ? additionalParameters[0] : null;
                String value = additionalParameters.length > 0 ? additionalParameters[1] : null;
                for (Printer pr : printersConnectedToServer) {
                    if (pr.getIsRunning() && printer.equals(pr.getNamePrinter())) {
                        if (checkAndPutValueInConfig(pr.getConfigPrinter(), parameters, value)) {
                            logger.info("user: " + sessionInfo.getUserId() + " has performed action: set "
                                    + parameters
                                    + " of printer: " + printer + " to " + value + " with sessionID: "
                                    + sessionInfo.getSessionId());
                            return "[ " + parameters + " , " + value + " ] has been set in printer: " + printer
                                    + "\n";
                        }
                        return "given parameter or value are not accepted \n";
                    }
                }
                return printer + ": printer not found connected to server! \n";

            default:
                throw new IllegalArgumentException("Unsupported action: " + action);
        }
    }

    @Override
    public String start(String printer, String sessionId) throws Exception {
        return executeCommand(sessionId, printer, CommandsActionEnum.start);
    }

    @Override
    public String stop(String printer, String sessionId) throws Exception {
        return executeCommand(sessionId, printer, CommandsActionEnum.stop);
    }

    @Override
    public String restart(String printer, String sessionId) throws Exception {
        return executeCommand(sessionId, printer, CommandsActionEnum.restart);
    }

    @Override
    public String print(String filename, String printer, String sessionId) throws Exception {
        return executeCommand(sessionId, printer, CommandsActionEnum.print, filename);
    }

    @Override
    public String queue(String printer, String sessionId) throws Exception {
        return executeCommand(sessionId, printer, CommandsActionEnum.queue);
    }

    @Override
    public String topQueue(String printer, String job, String sessionId) throws Exception {
        return executeCommand(sessionId, printer, CommandsActionEnum.topQueue, job);
    }

    @Override
    public String status(String printer, String sessionId) throws Exception {
        return executeCommand(sessionId, printer, CommandsActionEnum.status);
    }

    @Override
    public String readAllConfigs(String printer, String sessionId) throws Exception {
        return executeCommand(sessionId, printer, CommandsActionEnum.readAllConfig);
    }

    @Override
    public String readConfig(String printer, String parameter, String sessionId) throws Exception {
        return executeCommand(sessionId, printer, CommandsActionEnum.readConfig, parameter);
    }

    @Override
    public String setConfig(String printer, String parameter, String value, String sessionId) throws Exception {
        return executeCommand(sessionId, printer, CommandsActionEnum.setConfig, parameter, value);
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
