package com.datasec.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.sql.Timestamp;

@DatabaseTable(tableName = "users")
public class User {

    public static final String USERID_COLUMN_NAME = "userId";
    public static final String PASSWORD_COLUMN_NAME = "password";
    public static final String LAST_LOGIN_COLUMN_NAME = "lastLogin";

    @DatabaseField(id = true, unique = true, columnName = USERID_COLUMN_NAME, canBeNull = false)
    private String userId;
    @DatabaseField(columnName = PASSWORD_COLUMN_NAME, canBeNull = false)
    private String hashedPassword;

    @DatabaseField(columnName = LAST_LOGIN_COLUMN_NAME)
    private Timestamp lastLogin;

    public User() {
        // ORMLite needs a no-arg constructor
    }

    public User(String userId, String hashedPassword) {
        this.userId = userId;
        this.hashedPassword = hashedPassword;
    }

    public String getUser() {
        return this.userId;
    }

    public String getPassword() {
        return hashedPassword;
    }

}
