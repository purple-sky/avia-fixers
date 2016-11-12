package com.aviafix.api;

import java.time.LocalDate;

public class ElectronicPaymentReadRepresentation {

    public final int ETID;
    public final int creditCardNumber;
    public final LocalDate expiryDate;
    public final int cardCode;
    public final String cardHolderName;
    public final Double paymentAmount;
    public final int customerID;
    public final int orderNumber;
    public final LocalDate paymentDate;

    public ElectronicPaymentReadRepresentation(
            int ETID,
            int creditCardNumber,
            LocalDate expiryDate,
            int cardCode,
            String cardHolderName,
            Double paymentAmount,
            int customerID,
            int orderNumber,
            LocalDate paymentDate
    ) {
        this.ETID = ETID;
        this.creditCardNumber = creditCardNumber;
        this.expiryDate = expiryDate;
        this.cardCode = cardCode;
        this.cardHolderName = cardHolderName;
        this.paymentAmount = paymentAmount;
        this.customerID = customerID;
        this.orderNumber = orderNumber;
        this.paymentDate = paymentDate;
    }

}
