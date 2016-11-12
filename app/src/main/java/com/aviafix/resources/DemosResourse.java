package com.aviafix.resources;

import com.aviafix.api.*;
import com.aviafix.core.OrderStatus;
import com.aviafix.core.PartStatus;
import com.aviafix.db.generated.tables.HASPARTS;
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
        //TODO : join tables for payments to output one table with all types of payments (see UI representation)
        return "Result";
    }

    @GET
    @Path("/demo2")

    public String getDemo2 (
            @Context DSLContext database
    ) {
        // Done by TimW
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

    public String getDemo4 (
            @Context DSLContext database
    ) {
        // Done by PailL
        //TODO : some nested aggregation query with GROUP BY
        //TODO: show the average amount for the cheque paid of each bank in December 15th, 2014
        Result result = database.fetch(
                "SELECT bank, AVG(amount) " +
                        "FROM payByCheque " +
                        "WHERE chequeNum IN (SELECT cqNumpayOffline " +
                        "FROM payOffline " +
                        "WHERE pymntDatepayOffline = '2014-12-15') " +
                        "GROUP BY bank");

        String resultString = "";

        for (Object r : result) {
            resultString = resultString + r.toString();
        }
        return resultString;
    }

}
