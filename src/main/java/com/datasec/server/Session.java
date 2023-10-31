package com.datasec.server;


import java.rmi.RemoteException;

public class Session /*extends UnicastRemoteObject implements SessionInterface*/ {
    String sessionId;
    String userId;
    long lastInteraction; //maybe  TimeStamp object

//    protected Session() throws RemoteException {
//    }

    public Session(String sessionId, String userId, long lastInteraction) throws RemoteException {
//        super();
        this.sessionId = sessionId;
        this.userId = userId;
        this.lastInteraction = lastInteraction;
    }


    public String getSessionId() {
        return this.sessionId;
    }


    public String getUserId() {
        return this.userId;
    }


    public long getLastInteraction() {
        return this.lastInteraction;
    }


    public void setLastInteraction(long lastInteraction) {
        this.lastInteraction = lastInteraction;
    }
}
