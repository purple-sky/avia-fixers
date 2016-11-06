package com.aviafix.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

/**
 * Created by paull on 1/11/2016.
 */
public class ChequeWriteRepresentation {
    public final int chequeNum;
    public final String bank;
    public final Double amount;

    public final int cidpayOffline;
    public final int cqNumpayOffline;
    public final int orNumpayOffline;
    public final int feidpayOffline;
    public final LocalDate pymntDatepayOffline;

    @JsonCreator
    public ChequeWriteRepresentation(
            @JsonProperty("chequeNum") int chequeNum,
            @JsonProperty("bank") String bank,
            @JsonProperty("amount") Double amount,

            @JsonProperty("cidpayOffline") int cidpayOffline,
            int cidpayOffline1, @JsonProperty("cqNumpayOffline") int cqNumpayOffline,
            @JsonProperty("orNumpayOffline") int orNumpayOffline,
            @JsonProperty("feidpayOffline") int feidpayOffline,
            @JsonProperty("pymntDatepayOffline") LocalDate pymntDatepayOffline
    ) {

        this.chequeNum = chequeNum;
        this.bank = bank;
        this.amount = amount;

        this.cidpayOffline = cidpayOffline;
        this.cqNumpayOffline = cqNumpayOffline;
        this.orNumpayOffline = orNumpayOffline;
        this.feidpayOffline = feidpayOffline;
        this.pymntDatepayOffline = pymntDatepayOffline;

    }
}
