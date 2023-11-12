package com.datasec.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@DatabaseTable(tableName = "permissions")
public class PermissionUser {

    public static final String USERID_COLUMN_NAME = "userId";
    public static final String START_COLUMN_NAME = "start";
    public static final String STOP_COLUMN_NAME = "stop";
    public static final String RESTART_COLUMN_NAME = "restart";
    public static final String PRINT_COLUMN_NAME = "print";
    public static final String QUEUE_COLUMN_NAME = "queue";
    public static final String TOPQUEUE_COLUMN_NAME = "topQueue";
    public static final String STATUS_COLUMN_NAME = "status";
    public static final String READCONFIG_COLUMN_NAME = "readConfig";
    public static final String READALLCONFIG_COLUMN_NAME = "readAllConfig";
    public static final String SETCONFIG_COLUMN_NAME = "setConfig";


    @DatabaseField(unique = true, columnName = USERID_COLUMN_NAME, canBeNull = false)
    private String userId;

    @DatabaseField(columnName = START_COLUMN_NAME, canBeNull = false)
    private boolean start = false;

    @DatabaseField(columnName = STOP_COLUMN_NAME, canBeNull = false)
    private boolean stop = false;

    @DatabaseField(columnName = RESTART_COLUMN_NAME, canBeNull = false)
    private boolean restart = false;

    @DatabaseField(columnName = PRINT_COLUMN_NAME, canBeNull = false)
    private boolean print = true;

    @DatabaseField(columnName = QUEUE_COLUMN_NAME, canBeNull = false)
    private boolean queue = true;

    @DatabaseField(columnName = TOPQUEUE_COLUMN_NAME, canBeNull = false)
    private boolean topQueue = false;

    @DatabaseField(columnName = STATUS_COLUMN_NAME, canBeNull = false)
    private boolean status = false;

    @DatabaseField(columnName = READCONFIG_COLUMN_NAME, canBeNull = false)
    private boolean readConfig = false;

    @DatabaseField(columnName = READALLCONFIG_COLUMN_NAME, canBeNull = false)
    private boolean readAllConfig = false;

    @DatabaseField(columnName = SETCONFIG_COLUMN_NAME, canBeNull = false)
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
