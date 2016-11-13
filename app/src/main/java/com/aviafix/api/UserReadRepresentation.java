package com.aviafix.api;

/**
 * Created by vb on 2016-11-12.
 */
public class UserReadRepresentation {
    public final int id;
    public final String name;
    public final String role;

    public UserReadRepresentation(int id, String name, String role) {
        this.id = id;
        this.name = name;
        this.role = role;
    }
}
