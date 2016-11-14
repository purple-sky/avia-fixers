package com.aviafix.api;

public class UserFullReadRepresentation {
    public final int id;
    public final String name;
    public final String role;
    public final Integer customerId;
    public final Integer employeeId;

    public UserFullReadRepresentation(int id, String name, String role, Integer customerId, Integer employeeId) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.customerId = customerId;
        this.employeeId = employeeId;
    }
}
