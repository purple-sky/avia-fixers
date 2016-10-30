package com.aviafix.resources;

import com.aviafix.api.OrderWriteRepresentation;
import com.aviafix.api.PartsWriteRepresentation;
import com.aviafix.core.OrderStatus;
import com.aviafix.core.PartStatus;
import com.codahale.metrics.annotation.Timed;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.impl.DSL;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.Optional;

import static com.aviafix.db.generated.tables.ORDERS.ORDERS;
import static com.aviafix.db.generated.tables.HASPARTS.HASPARTS;


// Create a new order
@Path("/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrdersResourse {

    @POST
    @Timed
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postOrder(
            @Context DSLContext database,
            OrderWriteRepresentation order
    ) {

        Record record = database.insertInto(
                ORDERS,
                ORDERS.DATE,
                ORDERS.ORDERSTATUS,
                ORDERS.ORDERCID
        ).values(
                order.date,
                Optional.ofNullable(order.status).orElse(OrderStatus.PLACED),
                order.customerId
        ).returning(
                ORDERS.ORDERNUM
        ).fetchOne();

        final int oredrId = record.getValue(ORDERS.ORDERNUM);

        //if (oredrId != null) {
            for (PartsWriteRepresentation p : order.parts) {

                database.insertInto(
                        HASPARTS,
                        HASPARTS.PARTNAME,
                        HASPARTS.REPAIRSTATUS,
                        HASPARTS.QTY,
                        HASPARTS.PORDERNUM
                ).values(
                        p.name,
                        Optional.ofNullable(p.repairStatus).orElse(PartStatus.PLACED),
                        p.qtty,
                        oredrId
                ).execute();
            }
        //}



        return Response.created(
                URI.create(
                    "orders/" + oredrId)/*database
                        .select(DSL.max(DSL.field("orderNum", int.class)))
                        .from(DSL.table("orders"))
                        .fetchOne(0, int.class)
                )*/
        ).build();

    }

}
