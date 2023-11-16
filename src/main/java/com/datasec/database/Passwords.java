package com.datasec.database;

import com.password4j.Hash;
import com.password4j.Password;

/**
 * This class is only used to create hashed password with salt and pepper.
 * It is not used in the application itself.
 */
public class Passwords {
    private static final String pepper = "bFhVcnFiWndYV3hUZk1PeQ==";

    public static void main(String[] args) {
        String[] users = {"alice", "bob", "cecilia", "david", "erica", "fred", "george", "henry", "ida"};

        for (String user: users) {
            Hash hash = Password.hash(user + "123").addRandomSalt().addPepper(pepper).withScrypt();
            System.out.println("Hashed password for " + user);
            System.out.println(hash.getResult());
        }
    }
}
