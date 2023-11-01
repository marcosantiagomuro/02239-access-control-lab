package com.datasec.server;

import java.rmi.RemoteException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class SessionManager {
    private static final Logger logger = LogManager.getLogger(ServerApplication.class);

    private Map<String, Session> activeSessions = new HashMap<>();
    static final long SESSION_TIMEOUT = 2 * 60 * 1000; // TIMEOUT 2 minutes
    static final long SESSION_WARNING_TIMEOUT = SESSION_TIMEOUT - 1 * 60 * 1000; // WARN 1 minute before timeout
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public Session addNewSession(String userId) throws RemoteException {
        String sessionId = generateSessionId();
        while (activeSessions.containsKey(sessionId)) {
            sessionId = UUID.randomUUID().toString();
        }
        Session newUserSession = new Session(sessionId, userId, System.currentTimeMillis());
        activeSessions.put(sessionId, newUserSession);
        System.out.println("added new session. userId: " + newUserSession.getUserId() + ", sessionId: " + newUserSession.getSessionId() + ", lastInteraction: " + newUserSession.getLastInteraction()); //this not needed
        return newUserSession;
    }


    public static String generateSessionId() {
        try {
            // Generate random data using SecureRandom
            byte[] randomBytes = new byte[32]; // Adjust the length as needed
            SecureRandom random = new SecureRandom();
            random.nextBytes(randomBytes);

            // Use a cryptographic hash function to create the session ID
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(randomBytes);

            // Convert the hashed bytes to a hexadecimal string
            StringBuilder hexString = new StringBuilder(2 * hashBytes.length);
            for (byte b : hashBytes) {
                hexString.append(String.format("%02x", b));
            }

            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public Session getSession(String sessionId) {
        return activeSessions.get(sessionId);
    }

    public Session removeSession(String sessionId) {
        return activeSessions.remove(sessionId);
    }


    public void cleanupInactiveSessions() {
        long currentTime = System.currentTimeMillis();
        for (Map.Entry<String, Session> entry : activeSessions.entrySet()) {
            Session session = entry.getValue();
            if (currentTime - session.getLastInteraction() > SESSION_WARNING_TIMEOUT && currentTime - session.getLastInteraction() < SESSION_TIMEOUT) {
                System.out.println("WARNING: " + session.getUserId() +  " you will be logged out shortly");
            }
            if (currentTime - session.getLastInteraction() > SESSION_TIMEOUT) {
                System.out.println("TIMEOUT: " + session.getUserId() +  " you have been logged out!");
                logger.info("user: "+session.getUserId()+" with sessionID: "+session.getSessionId()+" has been logged out due to a timeout");
                // The session has exceeded the allowed inactivity period; remove it.
                removeSession(session.getSessionId());
            }
        }
    }

    public void startSessionCleanupDaemon() {
        long initialDelay = 0; // Start immediately when called
        long period = 30 * 1000; // checking period every 30 seconds (adjust as needed)

        scheduler.scheduleAtFixedRate(this::cleanupInactiveSessions, initialDelay, period, TimeUnit.MILLISECONDS);
    }

    public Collection<Session> getAllActiveSessions() {

        return activeSessions.values();
    }
}
