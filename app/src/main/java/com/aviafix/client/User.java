package com.aviafix.client;

import java.security.Principal;

/**
 * Created by AlexB on 2016-11-05.
 */
public class User implements Principal {
    private final String name;
    private final String role;

    public User(String name, String role) {
        this.name = name;
        this.role = role;
    }


    @Override
    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }
}
