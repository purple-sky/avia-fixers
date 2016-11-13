package com.aviafix.api;

import java.time.LocalDate;

/**
 * Created by paull on 12/11/2016.
 */
public class FinancialReportReadRepresentation {

    public final double revenue;
    public final double costOfGoodsSold;
    public final double profit;


    public FinancialReportReadRepresentation(
            double revenue, //SUM(TotalPaymentRepresentation.amount)
            double costOfGoodsSold, //SUM(hasParts.repairCost)
            double profit //SUM(hasParts.sellPrice * qty)
    ) {
        this.revenue = revenue;
        this.costOfGoodsSold = costOfGoodsSold;
        this.profit = profit;
    }
}
