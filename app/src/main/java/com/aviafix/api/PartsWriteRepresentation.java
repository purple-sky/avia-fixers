package com.aviafix.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

/**
 * Created by AlexB on 2016-10-29.
 */
public class PartsWriteRepresentation {

    public final String name;
    public final String repairStatus;
    public final Double repairCost;
    public final Double sellPrice;
    public final Date repairDate;
    public final int orderNumber;
    public final int qtty;

    @JsonCreator
    public PartsWriteRepresentation (
            @JsonProperty("name") String name,
            @JsonProperty("status") String repairStatus,
            @JsonProperty("repairCost") Double repairCost,
            @JsonProperty("sellPrice") Double sellPrice,
            @JsonProperty("repairDate") Date repairDate,
            @JsonProperty("orderNumber") int orderNumber,
            @JsonProperty("qtty") int qtty) {

        this.name = name;
        this.repairStatus = repairStatus;
        this.repairCost = repairCost;
        this.sellPrice = sellPrice;
        this.repairDate = repairDate;
        this.orderNumber = orderNumber;
        this.qtty = qtty;

    }

}
