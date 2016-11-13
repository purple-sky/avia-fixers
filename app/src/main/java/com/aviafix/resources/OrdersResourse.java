package com.aviafix.resources;

import com.aviafix.api.OrderReadRepresentation;
import com.aviafix.api.OrderWriteRepresentation;
import com.aviafix.api.PartsReadRepresentation;
import com.aviafix.api.PartsWriteRepresentation;
import com.aviafix.core.OrderStatus;
import com.aviafix.core.PartStatus;
import com.codahale.metrics.annotation.Timed;
import org.jooq.DSLContext;
import org.jooq.Record;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.aviafix.db.generated.tables.HASPARTS.HASPARTS;
import static com.aviafix.db.generated.tables.ORDERS.ORDERS;


@Path("/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrdersResourse {

    // gets all orders
    @GET
    @Timed
    public List<OrderReadRepresentation> getOrders(
            @Context DSLContext database,
            @CookieParam("FixerUID") String cookie
    ) {
        return database.selectFrom(ORDERS)
                       .fetchInto(ORDERS)
                       .stream()
                       .map(order ->
                               new OrderReadRepresentation(
                                       order.ORDERNUM(),
                                       order.DATE(),
                                       order.ORDERSTATUS(),
                                       order.ORDERREPAIRDATE(),
                                       order.ORDERCID(),
                                       order.TOTALPRICE()
                               )
                       )
                       .collect(Collectors.toList());
    }

    @GET
    @Path("/:{id}")
    @Timed
    public List<PartsReadRepresentation> getOrder(
            @Context DSLContext database,
            @PathParam("id") int id
    ) {
        return database.selectFrom(HASPARTS)
                .where(HASPARTS.PORDERNUM.equal(id))
                .fetchInto(HASPARTS)
                .stream()
                .map(part ->
                        new PartsReadRepresentation(
                                part.PARTNUM(),
                                part.PARTNAME(),
                                part.REPAIRSTATUS(),
                                part.REPAIRCOST(),
                                part.SELLPRICE(),
                                part.REPAIRDATE(),
                                part.PORDERNUM(),
                                part.QTY()
                        )
                )
                .collect(Collectors.toList());
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
                LocalDate.now(),
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
    @Path("/:{id}")
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
                    .set(ORDERS.ORDERSTATUS, order.status)
                    .where(ORDERS.ORDERNUM.equal(id))
                    .execute();

            // updates parts' status when Order status is changed to "InProgress"

            if (order.status.equals(OrderStatus.IN_PROGRESS)) {
                database.update(HASPARTS)
                        .set(HASPARTS.REPAIRSTATUS, PartStatus.IN_PROGRESS)
                        .where(HASPARTS.PORDERNUM.equal(id))
                        .execute();
            }
            return "Order #" + id + " updated";
        }

        return "Order doesn't exist";
    }

    @DELETE
    @Path("/:{id}")
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
