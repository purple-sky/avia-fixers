package com.aviafix.api;

/**
  * Created by paull on 12/11/2016.
  */
public class FinancialReportReadRepresentation {

    public final int order;
    public final int customer;
    public final double revenue;
    public final double cost;
    public Double profit = 0.00;

    public FinancialReportReadRepresentation(
            int order, // order #
            int customer,
            double revenue, //SUM(TotalPaymentRepresentation.amount)
            double cost //SUM(hasParts.repairCost)
            //double profit //SUM(hasParts.sellPrice * qty)
            ) {
        this.order = order;
        this.customer = customer;
        this.revenue = revenue;
        this.cost = cost;
        //this.profit = profit;
        }
    public void setProfit(FinancialReportReadRepresentation rep) {
        rep.profit = Double.valueOf(Math.round(rep.revenue - rep.cost));
    }
}
