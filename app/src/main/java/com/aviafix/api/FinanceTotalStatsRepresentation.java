package com.aviafix.api;

/**
 * Created by AlexB on 2016-11-17.
 */
public class FinanceTotalStatsRepresentation {
    public final Double totalCost;
    public final Double aveCost;
    public final Double totalRevenue;
    public final Double aveRevenue;
    public final Double totalProfit;
    public final Double aveProfit;

    public FinanceTotalStatsRepresentation(
            Double totalCost,
            Double aveCost,
            Double totalRevenue,
            Double aveRevenue,
            Double totalProfit,
            Double aveProfit
    ) {
        this.totalCost = totalCost;
        this.aveCost = aveCost;
        this.totalRevenue = totalRevenue;
        this.aveRevenue = aveRevenue;
        this.totalProfit = totalProfit;
        this.aveProfit = aveProfit;
    }
}

