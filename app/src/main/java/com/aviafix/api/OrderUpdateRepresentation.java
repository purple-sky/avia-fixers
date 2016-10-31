package com.aviafix.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.List;

/**
 * Created by AlexB on 2016-10-29.
 */
public class OrderUpdateRepresentation {

    public final int number;
    public final List<PartsWriteRepresentation> parts;
    public final LocalDate date;
    public final String status;
    public final LocalDate repairDate;
    public final int customerId;
    public final Double totalPrice;

    @JsonCreator
    public OrderUpdateRepresentation(
            @JsonProperty("number") int number,
            @JsonProperty("parts") List<PartsWriteRepresentation> parts,
            @JsonProperty("date") LocalDate date,
            @JsonProperty("status") String status,
            @JsonProperty("repairDate") LocalDate repairDate,
            @JsonProperty("customerID") int customerId,
            @JsonProperty("totalPrice") Double totalPrice
    ) {

        this.number = number;
        this.parts = parts;
        this.date = date;
        this.status = status;
        this.repairDate = repairDate;
        this.customerId = customerId;
        this.totalPrice = totalPrice;
    }

}
