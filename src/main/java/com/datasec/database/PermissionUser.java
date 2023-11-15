package com.datasec.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@DatabaseTable(tableName = "permissions")
public class PermissionUser {

    public static final String USERID = "userId";
    public static final String START = "start";
    public static final String STOP = "stop";
    public static final String RESTART = "restart";
    public static final String PRINT = "print";
    public static final String QUEUE = "queue";
    public static final String TOPQUEUE = "topQueue";
    public static final String STATUS = "status";
    public static final String READCONFIG = "readConfig";
    public static final String READALLCONFIG = "readAllConfig";
    public static final String SETCONFIG = "setConfig";


    @DatabaseField(unique = true, columnName = USERID, canBeNull = false)
    private String userId;

    @DatabaseField(columnName = START, canBeNull = false)
    private boolean start = false;

    @DatabaseField(columnName = STOP, canBeNull = false)
    private boolean stop = false;

    @DatabaseField(columnName = RESTART, canBeNull = false)
    private boolean restart = false;

    @DatabaseField(columnName = PRINT, canBeNull = false)
    private boolean print = true;

    @DatabaseField(columnName = QUEUE, canBeNull = false)
    private boolean queue = true;

    @DatabaseField(columnName = TOPQUEUE, canBeNull = false)
    private boolean topQueue = false;

    @DatabaseField(columnName = STATUS, canBeNull = false)
    private boolean status = false;

    @DatabaseField(columnName = READCONFIG, canBeNull = false)
    private boolean readConfig = false;

    @DatabaseField(columnName = READALLCONFIG, canBeNull = false)
    private boolean readAllConfig = false;

    @DatabaseField(columnName = SETCONFIG, canBeNull = false)
    private boolean setConfig;


    public PermissionUser() {
        // ORMLite needs a no-arg constructor
    }

    //permission users with no operations arguments created as basic user
    public PermissionUser(String userId) {
        this.userId = userId;
    }

    //permission users with  operations arguments created,  print and queue are set to true by default
    public PermissionUser(String userId, boolean start, boolean stop, boolean restart, boolean topQueue, boolean status, boolean readConfig, boolean readAllConfig, boolean setConfig) {
        this.userId = userId;
        this.start = start;
        this.stop = stop;
        this.restart = restart;
        this.topQueue = topQueue;
        this.status = status;
        this.readConfig = readConfig;
        this.readAllConfig = readAllConfig;
        this.setConfig = setConfig;
    }


}
