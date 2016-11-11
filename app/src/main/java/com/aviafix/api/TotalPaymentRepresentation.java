package com.aviafix.api;

import java.time.LocalDate;

/**
 * Created by AlexB on 2016-11-10.
 */
public class TotalPaymentRepresentation {
    public final int id;
    public final int customerId;
    public final LocalDate date;
    public final int order;
    public final Double amount;
    public final String type;


    public TotalPaymentRepresentation(
            int id,
            int customerId,
            LocalDate date,
            int order,
            Double amount,
            String type
    ) {
        this.id = id;
        this.customerId = customerId;
        this.date = date;
        this.order = order;
        this.amount = amount;
        this.type = type;
    }
}
