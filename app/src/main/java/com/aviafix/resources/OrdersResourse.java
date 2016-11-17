package com.aviafix.resources;

import com.aviafix.api.*;
import com.aviafix.core.OrderStatus;
import com.aviafix.core.PartStatus;
import com.aviafix.core.Roles;
import com.aviafix.core.UserReader;
import com.aviafix.tools.OptionalFilter;
import com.codahale.metrics.annotation.Timed;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.impl.DSL;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.aviafix.db.generated.tables.HASPARTS.HASPARTS;
import static com.aviafix.db.generated.tables.ORDERS.ORDERS;
import static com.aviafix.db.generated.tables.CUSTOMER_USERS.CUSTOMER_USERS;


@Path("/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrdersResourse {

    // gets all orders
    @GET
    @Timed
    public List<OrderReadRepresentation> getOrders(
            @QueryParam("order") Optional<Integer> order,
            @QueryParam("customer") Optional<Integer> customer,
            @QueryParam("priceFrom") Optional<Double> priceFrom,
            @QueryParam("priceTo") Optional<Double> priceTo,
            @QueryParam("status") Optional<String> status,
            @QueryParam("orderBy") Optional<String> orderBy,
            @Context DSLContext database,
            @CookieParam("FixerUID") int fixerUID
    ) {

        final UserFullReadRepresentation user =
                UserReader.findUser(fixerUID)
                          .apply(database)
                          .orElseThrow(() -> new WebApplicationException(Response.Status.UNAUTHORIZED));

        if(user.role.equals(Roles.REPAIR)){
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }

        if(!user.role.equals(Roles.FINANCE)){
            customer = Optional.of(user.customerId);
        }

        return database.selectFrom(ORDERS)
                       .where(OptionalFilter
                                      .build()
                                      .add(order.map(ORDERS.ORDERNUM::eq))
                                      .add(customer.map(ORDERS.ORDERCID::eq))
                                      .add(priceFrom.map(ORDERS.TOTALPRICE::ge))
                                      .add(priceTo.map(ORDERS.TOTALPRICE::le))
                                      .add(status.map(ORDERS.ORDERSTATUS::eq))
                                      .combineWithAnd())
                       .orderBy(DSL.field(
                               orderBy.orElse("orderNum")
                       ))
                       .fetchInto(ORDERS)
                       .stream()
                       .map(orderRecord ->
                               new OrderReadRepresentation(
                                       orderRecord.ORDERNUM(),
                                       orderRecord.DATE(),
                                       orderRecord.ORDERSTATUS(),
                                       orderRecord.ORDERREPAIRDATE(),
                                       orderRecord.ORDERCID(),
                                       orderRecord.TOTALPRICE()
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
            OrderWriteRepresentation order,
            @CookieParam("FixerUID") int fixerUID
    ) {

        Record customerRec = database.select(CUSTOMER_USERS.CID)
                .from(CUSTOMER_USERS)
                .where(CUSTOMER_USERS.CUID.eq(fixerUID))
                .fetchOne();

        Record record = database.insertInto(
                ORDERS,
                ORDERS.DATE,
                ORDERS.ORDERSTATUS,
                ORDERS.ORDERCID
        ).values(
                LocalDate.now(),
                Optional.ofNullable(order.status).orElse(OrderStatus.PLACED),
                customerRec.getValue(CUSTOMER_USERS.CID)
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

        return Response.ok().build();
    }

    @PUT
    @Path("/:{id}")
    @Timed
    public Response updateOrder (
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
            return Response.ok().build();
        }

        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    @DELETE
    @Path("/:{id}")
    public Response deleteOrder (
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
                    return Response.serverError().build();
                }

                return Response.ok().build();
            }
            return Response.notModified().build();
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }



}
