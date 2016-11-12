package com.aviafix.api;

import com.aviafix.core.RepairPriority;

import java.time.LocalDate;

/**
 * Created by AlexB on 2016-11-11.
 */
public class RepairReadRepresentation {

    public final int partNumber;
    public final String name;
    public final String repairStatus;
    public final LocalDate repairDate;
    public final int orderNumber;
    public final int qtty;
    public final int master;
    public final String masterName;
    public final String shop;
    public String priority = RepairPriority.LOW;

    public RepairReadRepresentation(
            int partNumber,
            String name,
            String repairStatus,
            LocalDate repairDate,
            int orderNumber,
            int qtty,
            int master,
            String masterName,
            String shop
    ) {
        this.partNumber = partNumber;
        this.name = name;
        this.repairStatus = repairStatus;
        this.repairDate = repairDate;
        this.orderNumber = orderNumber;
        this.qtty = qtty;
        this.master = master;
        this.masterName = masterName;
        this.shop = shop;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

}
