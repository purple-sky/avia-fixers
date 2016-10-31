package com.aviafix.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

/**
 * Created by AlexB on 2016-10-29.
 */
public class PartsUpdateRepresentation {

    public final int partNumber;
    public final String name;
    public final String repairStatus;
    public final Double repairCost;
    public final Double sellPrice;
    public final LocalDate repairDate;
    public final int orderNumber;
    public final int qtty;

    @JsonCreator
    public PartsUpdateRepresentation(
            @JsonProperty("partNumber") int partNumber,
            @JsonProperty("name") String name,
            @JsonProperty("status") String repairStatus,
            @JsonProperty("repairCost") Double repairCost,
            @JsonProperty("sellPrice") Double sellPrice,
            @JsonProperty("repairDate") LocalDate repairDate,
            @JsonProperty("orderNumber") int orderNumber,
            @JsonProperty("qtty") int qtty) {

        this.partNumber = partNumber;
        this.name = name;
        this.repairStatus = repairStatus;
        this.repairCost = repairCost;
        this.sellPrice = sellPrice;
        this.repairDate = repairDate;
        this.orderNumber = orderNumber;
        this.qtty = qtty;

    }

}
