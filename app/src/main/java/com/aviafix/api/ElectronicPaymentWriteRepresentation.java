package com.aviafix.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public class ElectronicPaymentWriteRepresentation {

    public final int creditCardNumber;
    public final LocalDate expiryDate;
    public final int cardCode;
    public final String cardHolderName;
    public final Double paymentAmount;
    public final Integer customerID;
    public final int orderNumber;
    public final LocalDate paymentDate;

    @JsonCreator
    public ElectronicPaymentWriteRepresentation(
            @JsonProperty("orderNumber") int orderNumber,
            @JsonProperty("creditCardNumber") int creditCardNumber,
            @JsonProperty("expiryDate") LocalDate expiryDate,
            @JsonProperty("cardCode") int cardCode,
            @JsonProperty("cardHolderName") String cardHolderName,
            @JsonProperty("paymentAmount") Double paymentAmount,
            @JsonProperty("customerID") Integer customerID

            //@JsonProperty("paymentDate") LocalDate paymentDate
    ) {

        this.creditCardNumber = creditCardNumber;
        this.expiryDate = expiryDate;
        this.cardCode = cardCode;
        this.cardHolderName = cardHolderName;
        this.paymentAmount = paymentAmount;
        this.customerID = customerID;
        this.orderNumber = orderNumber;
        this.paymentDate = LocalDate.now();
    }

}
