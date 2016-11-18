package com.aviafix.resources;

import com.aviafix.api.*;
import com.aviafix.api.FinancialReportReadRepresentation;
import com.codahale.metrics.annotation.Timed;
import org.jooq.*;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.List;

import static com.aviafix.db.generated.tables.HASPARTS.HASPARTS;
import static com.aviafix.db.generated.tables.ORDERS.ORDERS;
import static org.jooq.impl.DSL.*;

@Path("/report")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)

public class FinancialReportResource {

    @GET
    @Timed
    public List<FinancialReportReadRepresentation> getReport(
            @Context DSLContext database
    ) {
        List<FinancialReportReadRepresentation> representations =
                database.select(
                        ORDERS.ORDERNUM.as("ORDER"),
                        ORDERS.ORDERCID.as("CUSTOMER"),
                        (HASPARTS.SELLPRICE.multiply(HASPARTS.QTY)).sum().as("REVENUE"),
                        (HASPARTS.REPAIRCOST.multiply(HASPARTS.QTY)).sum().as("COST")
                )
                        .from(ORDERS)
                        .join(HASPARTS)
                        .on(ORDERS.ORDERNUM.eq(HASPARTS.PORDERNUM))
                        .groupBy(ORDERS.ORDERNUM)
                        .fetchInto(FinancialReportReadRepresentation.class);

        for (FinancialReportReadRepresentation f: representations) {
            f.setProfit(f);
        }
        return representations;
    }

    @GET
    @Path("/totals")
    @Timed
    public FinanceTotalStatsRepresentation getTotals(
            @Context DSLContext database
    ) {
        Record6 total =
                database.select(
                        sum(field(HASPARTS.REPAIRCOST.mul(HASPARTS.QTY))).cast(Double.class).round(2),
                        avg(field(HASPARTS.REPAIRCOST.mul(HASPARTS.QTY))).cast(Double.class).round(2),
                        sum(field(HASPARTS.SELLPRICE.mul(HASPARTS.QTY))).cast(Double.class).round(2),
                        avg(field(HASPARTS.SELLPRICE.mul(HASPARTS.QTY))).cast(Double.class).round(2),
                        sum(field(HASPARTS.SELLPRICE.mul(HASPARTS.QTY))).minus(sum(field(HASPARTS.REPAIRCOST.multiply(HASPARTS.QTY)))).cast(Double.class).round(2),
                        avg(field(HASPARTS.SELLPRICE.mul(HASPARTS.QTY))).minus(avg(field(HASPARTS.REPAIRCOST.multiply(HASPARTS.QTY)))).cast(Double.class).round(2))
                        .from(HASPARTS)
                .fetchOne();

        return new FinanceTotalStatsRepresentation (
                Double.parseDouble(total.value1().toString()),
                Double.parseDouble(total.value2().toString()),
                Double.parseDouble(total.value3().toString()),
                Double.parseDouble(total.value4().toString()),
                Double.parseDouble(total.value5().toString()),
                Double.parseDouble(total.value6().toString())
        );
    }
}