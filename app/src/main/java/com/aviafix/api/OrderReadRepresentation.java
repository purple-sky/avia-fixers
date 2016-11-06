package com.aviafix.api;

import java.time.LocalDate;

/**
 * Created by AlexB on 2016-10-29.
 */
public class OrderReadRepresentation {

    public final int id;
    public final LocalDate date;
    public final String status;
    public final LocalDate repairDate;
    public final int customerId;
    public final Double totalPrice;

    public OrderReadRepresentation(
            int id,
            LocalDate date,
            String status,
            LocalDate repairDate,
            int customerId,
            Double totalPrice
    ) {
        this.id = id;
        this.date = date;
        this.status = status;
        this.repairDate = repairDate;
        this.customerId = customerId;
        this.totalPrice = totalPrice;
    }

}
