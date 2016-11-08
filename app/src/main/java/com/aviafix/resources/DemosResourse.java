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
        //TODO : some division query
        return "Result";
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
