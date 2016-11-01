package com.aviafix.resources;

import com.aviafix.api.OrderUpdateRepresentation;
import com.aviafix.api.OrderWriteRepresentation;
import com.aviafix.api.PartsWriteRepresentation;
import com.aviafix.core.OrderStatus;
import com.aviafix.core.PartStatus;
import com.codahale.metrics.annotation.Timed;
import io.dropwizard.jersey.params.IntParam;
import org.jooq.DSLContext;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.impl.DSL;
import org.jooq.util.postgres.PostgresDataType;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.Optional;

import static com.aviafix.db.generated.tables.ORDERS.ORDERS;
import static com.aviafix.db.generated.tables.HASPARTS.HASPARTS;


@Path("/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrdersResourse {

    // gets all orders
    @GET
    @Timed
    public String getOrders(
            @Context DSLContext database
    ) {
        return database.select()
                .from(ORDERS)
                .fetch()
                .format();
    }

    // Create a new order
    @POST
    @Timed
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postOrder(
            @Context DSLContext database, // assuresconnection to db
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

        return Response.created(
                URI.create(
                    "orders/" + oredrId)/*database
                        .select(DSL.max(DSL.field("orderNum", int.class)))
                        .from(DSL.table("orders"))
                        .fetchOne(0, int.class)
                )*/
        ).build();
    }

    @PUT
    @Path("/{id}")
    @Timed
    public String updateOrder (
            @Context DSLContext database,
            @PathParam("id") Integer id,
            OrderWriteRepresentation order
    ) {

        // Check if order exists in DB
        final Record record = database.select(
                ORDERS.ORDERNUM.cast(Integer.class),
                ORDERS.ORDERSTATUS.cast(String.class)
        )
                .from(ORDERS)
                .where(ORDERS.ORDERNUM.equal(id))
                .fetchOne();

        if (record != null) {
             database.update(ORDERS)
                    .set(ORDERS.ORDERSTATUS,order.status)
                    .where(ORDERS.ORDERNUM.equal(id))
                    .execute();
            return "Order #" + id + " updated";
        }

        return "Order doesn't exist";
    }

    @DELETE
    @Path("/{id}")
    public String deleteOrder (
            @Context DSLContext database,
            @PathParam("id") Integer id
            ) {

        final Record record = database.select(ORDERS.ORDERNUM.cast(Integer.class),
                ORDERS.ORDERSTATUS.cast(String.class))
                .from(ORDERS)
                .where(ORDERS.ORDERNUM.equal(id))
                .fetchOne();

        if (record != null) {
            if (record.getValue(ORDERS.ORDERSTATUS, String.class).equals("Placed")) {
                try {
                    database.delete(ORDERS)
                            .where(ORDERS.ORDERNUM.equal(id))
                            .execute();
                } catch (Exception e) {
                    return "Sorry something went wrong";
                }

                return "Order #" + id + " deleted";
            }
            return "Order #" + id + " can't be deleted";
        }
        return "Order doesn't exist";
    }



}
