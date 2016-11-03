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

    @JsonCreator
    public ElectronicPaymentWriteRepresentation(
            @JsonProperty("creditCardNumber") int creditCardNumber,
            @JsonProperty("expiryDate") LocalDate expiryDate,
            @JsonProperty("cardCode") int cardCode,
            @JsonProperty("cardHolderName") String cardHolderName,
            @JsonProperty("paymentAmount") Double paymentAmount
    ) {

        this.creditCardNumber = creditCardNumber;
        this.expiryDate = expiryDate;
        this.cardCode = cardCode;
        this.cardHolderName = cardHolderName;
        this.paymentAmount = paymentAmount;
    }

}
