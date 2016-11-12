package com.aviafix.resources;

import com.aviafix.api.*;
import com.aviafix.core.OrderStatus;
import com.aviafix.core.PartStatus;
import com.aviafix.db.generated.tables.*;
import com.aviafix.db.generated.tables.pojos.HASPARTSPROJECTION;
import com.aviafix.db.generated.tables.records.HASPARTSRECORD;
import com.codahale.metrics.annotation.Timed;
import org.jooq.*;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.aviafix.db.generated.tables.ORDERS.ORDERS;
import static com.aviafix.db.generated.tables.HASPARTS.HASPARTS;
import static com.aviafix.db.generated.tables.PAYBYCREDITCARD.PAYBYCREDITCARD;
import static com.aviafix.db.generated.tables.PAYONLINE.PAYONLINE;
import static com.aviafix.db.generated.tables.PAYBYCHEQUE.PAYBYCHEQUE;
import static com.aviafix.db.generated.tables.PAYOFFLINE.PAYOFFLINE;
import static org.jooq.impl.DSL.select;
import static org.jooq.impl.DSL.sum;
import static org.jooq.impl.DSL.val;

/**
 * Created by AlexB on 2016-11-08.
 */

@Path("/demos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DemosResourse {

    @GET
    @Path("/demo1")

    public String getDemo1 (
            @Context DSLContext database
    ) {

        String onlineResult = "SELECT ETID, CREDITCARDNUM, EXPDATE, CODE, CARDHOLDERNAME, AMOUNT," +
                " CIDPAYONLINE, ORDNUMPAYONL, PYMNTDATEONLINE, 'Credit Card' AS 'Type'" +
                "FROM payByCreditCard JOIN payOnline WHERE payOnline.etidPayOnline = payByCreditCard.ETID" +
                "ORDER BY ETID";

        String offlineResult = "SELECT CHEQUENUM, BANK, AMOUNT, CIDPAYOFFLINE, ORNUMPAYOFFLINE," +
                " FEIDPAYOFFLINE, PYMNTDATEPAYOFFLINE, 'Cheque' AS 'Type'" +
                "FROM payByCheque JOIN payOffline WHERE payOffline.CQNUMPAYOFFLINE = payByCheque.CHEQUENUM" +
                "ORDER BY CHEQUENUM";

        String totalResult = database.fetch("SELECT ETID as 'ETID or ChequeNumber', CARDHOLDERNAME as 'CardHolder or BankName', " +
                "Amount, CIDPAYONLINE as CustomerID, ORDNUMPAYONL as OrderNumber, PYMNTDATEONLINE as PaymentDate, " +
                "'Credit Card' AS 'Type'" +
                "FROM (?) t1" +
                "UNION ALL" +
                "SELECT CHEQUENUM, BANK, AMOUNT, CIDPAYOFFLINE, ORNUMPAYOFFLINE, PYMNTDATEPAYOFFLINE, 'Cheque' AS 'Type'" +
                "FROM (?) t2", onlineResult, offlineResult).format();

        return totalResult;
    }

    @GET
    @Path("/demo2")

    public String getDemo2 (
            @Context DSLContext database
    ) {
        //TODO : some division query (find the name and id of finance employees who processed all the cheque orders above 400
        final int threshold = 400;

        String result = database.fetch("SELECT distinct eName, eID" +
                "FROM  employee_users eu, payOffline pf" +
                "WHERE eu.eID = pf.feidpayOffline and NOT EXISTS" +
                "   (SELECT feidpayOffline, amount" +
                "   FROM payOffline pf, payByCheque pc" +
                "   WHERE pf.feidpayOffline = eu.eID" +
                "   AND pc.chequeNum = pf.cqNumpayOffline" +
                "   AND pc.amount < ?)", threshold).format();

        return result;
    }

    @GET
    @Path("/demo3")

    public String getDemo3 (
            @Context DSLContext database
    ) {
        //TODO : some aggregation query
        return "Result";
    }

    @GET
    @Path("/demo4")

    public String getDemo4 (
            @Context DSLContext database
    ) {
        //TODO : some nested aggregation query with GROUP BY
        return "Result";
    }

}
