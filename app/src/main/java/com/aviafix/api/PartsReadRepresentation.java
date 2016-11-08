package com.aviafix.api;

import java.time.LocalDate;

/**
 * Created by AlexB on 2016-11-08.
 */
public class PartsReadRepresentation {

    public final int partNumber;
    public final String name;
    public final String repairStatus;
    public final Double repairCost;
    public final Double sellPrice;
    public final LocalDate repairDate;
    public final int orderNumber;
    public final int qtty;

    public PartsReadRepresentation(
            int partNumber,
            String name,
            String repairStatus,
            Double repairCost,
            Double sellPrice,
            LocalDate repairDate,
            int orderNumber,
            int qtty
    ) {
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
