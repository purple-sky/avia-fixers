package com.aviafix.resources;

import com.aviafix.api.*;
import org.jooq.*;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

import static com.aviafix.db.generated.tables.EMPLOYEE_USERS.EMPLOYEE_USERS;
import static com.aviafix.db.generated.tables.PAYOFFLINE.PAYOFFLINE;
import static com.aviafix.db.generated.tables.PAYBYCHEQUE.PAYBYCHEQUE;
import static org.jooq.impl.DSL.*;

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
        //TODO : join tables for payments to output one table with all types of payments (see UI representation)
        return "Result";
    }

    @GET
    @Path("/demo2")

    public  List<Demo2Representation> getDemo2 (
            @Context DSLContext database
    ) {
        // Done by TimW
        //some division query (find the name and id of finance employees who processed all the cheque orders above 400)
        final int threshold = 400;

        /* Query in Plain SQL works as well, just outputs string
        String result = database.fetch("SELECT distinct eName, eID " +
                " FROM  employee_users eu, payOffline pf " +
                " WHERE eu.eID = pf.feidpayOffline and NOT EXISTS " +
                "   (SELECT feidpayOffline, amount" +
                "   FROM payOffline pf, payByCheque pc" +
                "   WHERE pf.feidpayOffline = eu.eID" +
                "   AND pc.chequeNum = pf.cqNumpayOffline" +
                "   AND pc.amount < ?)", threshold).format();
        return result;*/

        List<Demo2Representation> result =
                database
                        .selectDistinct(EMPLOYEE_USERS.ENAME, EMPLOYEE_USERS.EID)
                        .from(EMPLOYEE_USERS, PAYOFFLINE)
                        .where(EMPLOYEE_USERS.EID.eq(PAYOFFLINE.FEIDPAYOFFLINE)
                                .and(notExists(
                                        database
                                                .select(PAYOFFLINE.FEIDPAYOFFLINE, PAYBYCHEQUE.AMOUNT)
                                                .from(PAYOFFLINE, PAYBYCHEQUE)
                                                .where(PAYOFFLINE.FEIDPAYOFFLINE.eq(EMPLOYEE_USERS.EID)
                                                        .and(PAYBYCHEQUE.CHEQUENUM.eq(PAYOFFLINE.CQNUMPAYOFFLINE))
                                                        .and(PAYBYCHEQUE.AMOUNT.le(400.00))
                                                )
                                ))
                        )
                        .fetchInto(Demo2Representation.class);
        return result;
    }

    @GET
    @Path("/demo3")

    public String getDemo3 (
            @Context DSLContext database
    ) {
        // Done by PailL
        //TODO : some aggregation query
        //TODO: Count the number of hasPart with order number equals to 1
        Result result = database.fetch("SELECT COUNT(*) FROM hasParts WHERE porderNum = 31");

        String resultString = "";

        for (Object r : result) {
            resultString = resultString + r.toString();
        }
        return resultString;
    }


    @GET
    @Path("/demo4")

    public  List<Demo4Representation> getDemo4 (
            @Context DSLContext database
    ) {
        // Done by PailL
        //some nested aggregation query with GROUP BY
        //show the average amount for the cheque paid of each bank in December 15th, 2014
        /*Result result = database.fetch(
                "SELECT bank, AVG(amount) " +
                        "FROM payByCheque " +
                        "WHERE chequeNum IN (SELECT cqNumpayOffline " +
                        "FROM payOffline " +
                        "WHERE pymntDatepayOffline = '2014-12-15') " +
                        "GROUP BY bank");*/
        List<Demo4Representation> result =
                database
                        .select(PAYBYCHEQUE.BANK, avg(PAYBYCHEQUE.AMOUNT).cast(Double.class))
                        .from(PAYBYCHEQUE)
                        .where(PAYBYCHEQUE.CHEQUENUM.in(
                                database
                                        .select(PAYOFFLINE.CQNUMPAYOFFLINE)
                                        .from(PAYOFFLINE)
                                        .where(PAYOFFLINE.PYMNTDATEPAYOFFLINE.eq(LocalDate.parse("2014-12-15")))
                        ))
                        .groupBy(PAYBYCHEQUE.BANK)
                        .stream()
                        .map(res -> new Demo4Representation(
                                res.value1(),
                                res.value2()
                        )).collect(Collectors.toList());

        return result;
    }

}
