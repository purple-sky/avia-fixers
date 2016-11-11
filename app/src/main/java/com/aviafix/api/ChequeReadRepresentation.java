package com.aviafix.api;

import java.time.LocalDate;

/**
 * Created by PaulL on 2016-11-10.
 */
public class ChequeReadRepresentation {
    public final int chequeNumber;
    public final String bank;
    public final Double amount;
    public final int customerId;
    public final int orderNumber;
    public final int financeEmployeeId;
    public final LocalDate date;

    public ChequeReadRepresentation(
            int chequeNumber,
            String bank,
            Double amount,
            int customerId,
            int orderNumber,
            int financeEmployeeId,
            LocalDate date
    ) {
        this.chequeNumber = chequeNumber;
        this.bank = bank;
        this.amount = amount;
        this.customerId = customerId;
        this.orderNumber = orderNumber;
        this.financeEmployeeId = financeEmployeeId;
        this.date = date;

    }
}
