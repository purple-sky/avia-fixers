package com.aviafix.api;

/**
 * Created by AlexB on 2016-11-19.
 */
public class CustomerReadRepresentation {

    public final int id;
    public final String legalName;
    public final String address;
    public final int phone;


    public CustomerReadRepresentation(int id, String legalName, String address, int phone) {
        this.id = id;
        this.legalName = legalName;
        this.address = address;
        this.phone = phone;
    }
}
