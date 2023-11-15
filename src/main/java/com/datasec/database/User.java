package com.datasec.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;

import java.sql.Timestamp;

@DatabaseTable(tableName = "users")
public class User {

    public static final String USERID = "userId";
    public static final String PASSWORD = "password";
    public static final String LAST_LOGIN = "lastLogin";
    public static final String ROLE_NAME = "roleName";

    @DatabaseField(id = true, unique = true, columnName = USERID, canBeNull = false)
    private String userId;
    @DatabaseField(columnName = PASSWORD, canBeNull = false)
    private String hashedPassword;

    @DatabaseField(columnName = LAST_LOGIN)
    private Timestamp lastLogin;

    @Getter
    @DatabaseField(columnName = ROLE_NAME, canBeNull = true)
    private String roleName;

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
