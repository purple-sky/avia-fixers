package com.aviafix.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

/**
 * Created by paull on 1/11/2016.
 */
public class ChequeWriteRepresentation {
    public final int chequeNumber;
    public final String bank;
    public final Double amount;

    public final int customerId;
    public final int orderNumber;
    public final int financeEmployeeId;
    public final LocalDate date;

    @JsonCreator
    public ChequeWriteRepresentation(
            @JsonProperty("chequeNum") int chequeNumber,
            @JsonProperty("bank") String bank,
            @JsonProperty("amount") Double amount,

            @JsonProperty("customerId") int customerId,
            @JsonProperty("orderNumber") int orderNumber,
            @JsonProperty("financeEmployeeId") int financeEmployeeId,
            @JsonProperty("date") LocalDate date
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
