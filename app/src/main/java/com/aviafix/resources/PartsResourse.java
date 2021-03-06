package com.aviafix.resources;

import com.aviafix.api.*;
import com.aviafix.core.OrderStatus;
import com.aviafix.core.PartStatus;
import com.aviafix.core.Roles;
import com.aviafix.core.UserReader;
import com.aviafix.tools.OptionalFilter;
import com.codahale.metrics.annotation.Timed;
import org.jooq.DSLContext;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.Record;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.aviafix.db.generated.tables.ORDERS.ORDERS;
import static com.aviafix.db.generated.tables.HASPARTS.HASPARTS;
import static com.aviafix.db.generated.tables.CUSTOMER_USERS.CUSTOMER_USERS;

/**
 * Created by AlexB on 2016-10-30.
 */
@Path("/parts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PartsResourse {

    @GET
    @Timed
    public List<PartsReadRepresentation> getParts(
            @QueryParam("partN") Optional<Integer> partN,
            @QueryParam("order") Optional<Integer> order,
            @QueryParam("priceFrom") Optional<Double> priceFrom,
            @QueryParam("priceTo") Optional<Double> priceTo,
            @QueryParam("costFrom") Optional<Double> costFrom,
            @QueryParam("costTo") Optional<Double> costTo,
            @QueryParam("status") Optional<String> status,
            @QueryParam("like") Optional<String> like,
            @QueryParam("orderBy") Optional<String> orderBy,
            @Context DSLContext database,
            @CookieParam("FixerUID") int fixerUID
    ) {
        final UserFullReadRepresentation user =
                UserReader.findUser(fixerUID)
                        .apply(database)
                        .orElseThrow(() -> new WebApplicationException(Response.Status.UNAUTHORIZED));

        if(user.role.equals(Roles.CUSTOMER)){
            final Record customerRecord =
                    database.select(CUSTOMER_USERS.CID)
                            .from(CUSTOMER_USERS)
                            .where(CUSTOMER_USERS.CUID.eq(fixerUID))
                            .fetchOne();
            int customer = customerRecord.getValue(CUSTOMER_USERS.CID);

            List<PartsReadRepresentation> parts =
                    database.select(HASPARTS.PARTNUM, HASPARTS.PARTNAME, HASPARTS.REPAIRSTATUS,
                            HASPARTS.REPAIRCOST,HASPARTS.SELLPRICE, HASPARTS.REPAIRDATE, HASPARTS.PORDERNUM, HASPARTS.QTY)
                            .from(HASPARTS)
                            .join(ORDERS)
                            .on(HASPARTS.PORDERNUM.eq(ORDERS.ORDERNUM))
                            .where(ORDERS.ORDERCID.eq(customer))
                            .and(OptionalFilter
                                            .build()
                                            .add(partN.map(HASPARTS.PARTNUM::eq))
                                            .add(order.map(HASPARTS.PORDERNUM::eq))
                                            .add(priceFrom.map(HASPARTS.SELLPRICE::ge))
                                            .add(priceTo.map(HASPARTS.SELLPRICE::le))
                                            .add(costFrom.map(HASPARTS.REPAIRCOST::ge))
                                            .add(costTo.map(HASPARTS.REPAIRCOST::le))
                                            .add(status.map(HASPARTS.REPAIRSTATUS::eq))
                                            .add(like.map(HASPARTS.PARTNAME::contains))
                                            .combineWithAnd())
                            .orderBy(DSL.field(
                                    orderBy.orElse("partNum")
                            ))
                            .fetchInto(PartsReadRepresentation.class);
            return parts;


        }
        // TODO: think about join
        return database.selectFrom(HASPARTS)
                .where(OptionalFilter
                        .build()
                        .add(partN.map(HASPARTS.PARTNUM::eq))
                        .add(order.map(HASPARTS.PORDERNUM::eq))
                        .add(priceFrom.map(HASPARTS.SELLPRICE::ge))
                        .add(priceTo.map(HASPARTS.SELLPRICE::le))
                        .add(costFrom.map(HASPARTS.REPAIRCOST::ge))
                        .add(costTo.map(HASPARTS.REPAIRCOST::le))
                        .add(status.map(HASPARTS.REPAIRSTATUS::eq))
                        .add(like.map(HASPARTS.PARTNAME::contains))
                        .combineWithAnd())
                .orderBy(DSL.field(
                        orderBy.orElse("partNum")
                ))
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

    @GET
    @Path("/:{id}")
    @Timed
    public PartsReadRepresentation getPart(
            @Context DSLContext database,
            @PathParam("id") Integer id
    ) {
        Record record = database.select()
                .from(HASPARTS)
                .where(HASPARTS.PARTNUM.equal(id))
                .fetchOne();
        return new PartsReadRepresentation(
                record.getValue(HASPARTS.PARTNUM),
                record.getValue(HASPARTS.PARTNAME, String.class),
                record.getValue(HASPARTS.REPAIRSTATUS, String.class),
                record.getValue(HASPARTS.REPAIRCOST, Double.class),
                record.getValue(HASPARTS.SELLPRICE, Double.class),
                record.getValue(HASPARTS.REPAIRDATE),
                record.getValue(HASPARTS.PORDERNUM),
                record.getValue(HASPARTS.QTY)
        );
    }

     @PUT
     @Timed
     @Consumes(MediaType.APPLICATION_JSON)
     public Response updatePart(
             @Context DSLContext database,
             PartsUpdateRepresentation part
     ) {
         final Record record = database.select(
                 HASPARTS.PARTNUM.cast(Integer.class),
                 HASPARTS.REPAIRSTATUS.cast(String.class),
                 HASPARTS.REPAIRCOST.cast(Double.class),
                 HASPARTS.SELLPRICE.cast(Double.class),
                 HASPARTS.REPAIRDATE,
                 HASPARTS.PORDERNUM.cast(Integer.class)
         )
                 .from(HASPARTS)
                 .where(HASPARTS.PARTNUM.equal(part.partNumber))
                 .fetchOne();

         final int ordNum = record.getValue(HASPARTS.PORDERNUM, Integer.class);

         if (record != null) {
             // check for part repair status not to be completed
            if (!record.getValue(HASPARTS.REPAIRSTATUS).equals(PartStatus.COMPLETE)) {

                // updating repair cost

                if (part.repairCost == null) {
                    database.update(HASPARTS)
                            .set(HASPARTS.REPAIRCOST, record.getValue(HASPARTS.REPAIRCOST, Double.class))
                            .where(HASPARTS.PARTNUM.equal(part.partNumber))
                            .execute();
                    System.out.println("Part #" + part.partNumber + " same cost");
                } else {
                    // check that status is Placed
                    if (record.getValue(HASPARTS.REPAIRSTATUS).equals(PartStatus.PLACED)) {
                        database.update(HASPARTS)
                                .set(HASPARTS.REPAIRCOST, part.repairCost)
                                .where(HASPARTS.PARTNUM.equal(part.partNumber))
                                .execute();
                    } else {
                        return Response.status(Response.Status.BAD_REQUEST).build();
                    }
                }

                // updating sell price

                if (part.sellPrice == null) {
                    database.update(HASPARTS)
                            .set(HASPARTS.SELLPRICE, record.getValue(HASPARTS.SELLPRICE, Double.class))
                            .where(HASPARTS.PARTNUM.equal(part.partNumber))
                            .execute();
                    System.out.println("Part #" + part.partNumber + " same sell price");
                } else {
                    // check that status is Placed
                    if (record.getValue(HASPARTS.REPAIRSTATUS).equals(PartStatus.PLACED)) {
                        database.update(HASPARTS)
                                .set(HASPARTS.SELLPRICE, part.sellPrice)
                                .where(HASPARTS.PARTNUM.equal(part.partNumber))
                                .execute();

                        // update Order price = part_sell_price*qty

                        database.fetch("UPDATE orders SET totalPrice = (SELECT ROUND(sum(sellPrice * qty), 2) FROM hasParts WHERE porderNum = ?) WHERE orderNum = ?", ordNum, ordNum)
                                .format();
                    } else {
                        return Response.status(Response.Status.METHOD_NOT_ALLOWED).build();
                    }
                }

                // updating repair date

                if (part.repairDate == null) {
                    database.update(HASPARTS)
                            .set(HASPARTS.REPAIRDATE, record.getValue(HASPARTS.REPAIRDATE))
                            .where(HASPARTS.PARTNUM.equal(part.partNumber))
                            .execute();
                    System.out.println("Part #" + part.partNumber + " same repair date");
                } else {
                    database.update(HASPARTS)
                            .set(HASPARTS.REPAIRDATE, part.repairDate)
                            .where(HASPARTS.PARTNUM.equal(part.partNumber))
                            .execute();
                    // update Order repair date = max repair date for all parts

                    database.fetch("UPDATE orders SET orderRepairDate = (SELECT MAX(repairDate) FROM hasParts WHERE porderNum = ?) WHERE orderNum = ?", ordNum, ordNum)
                            .format();
                }

                // updating status

                if (part.repairStatus == null) {
                    database.update(HASPARTS)
                            .set(HASPARTS.REPAIRSTATUS, record.getValue(HASPARTS.REPAIRSTATUS, String.class))
                            .where(HASPARTS.PARTNUM.equal(part.partNumber))
                            .execute();
                } else {
                    database.update(HASPARTS)
                            .set(HASPARTS.REPAIRSTATUS, part.repairStatus)
                            .where(HASPARTS.PARTNUM.equal(part.partNumber))
                            .execute();

                    // update dates in case something was changed

                    database.fetch("UPDATE orders SET orderRepairDate = (SELECT MAX(repairDate) FROM hasParts WHERE porderNum = ?) WHERE orderNum = ?", ordNum, ordNum)
                            .format();

                    //check for all parts in order have "Complete" => update order status to "Complete"
                    if (part.repairStatus.equals(PartStatus.COMPLETE)) {
                        database.fetch("UPDATE orders SET orderStatus = CASE " +
                                "WHEN (SELECT COUNT(*) FROM hasParts WHERE porderNum = ?) = " +
                                "(SELECT COUNT(*) FROM hasParts WHERE porderNum = ? AND repairStatus = ?) " +
                                "THEN ? ELSE ? END " +
                                "WHERE orderNum = ?", ordNum, ordNum, PartStatus.COMPLETE, OrderStatus.COMPLETE, OrderStatus.IN_PROGRESS, ordNum)
                                .format();
                    }
                }

                return Response.ok().build();
            } return Response.status(Response.Status.METHOD_NOT_ALLOWED).build();
         } else {
             return Response.status(Response.Status.BAD_REQUEST).build();
         }

     }

    @DELETE
    @Path("/{id}")
    public Response deletePart (
            @Context DSLContext database,
            @PathParam("id") Integer id
    ) {

        final Record record = database.select(
                HASPARTS.PARTNUM.cast(Integer.class),
                HASPARTS.REPAIRSTATUS.cast(String.class),
                HASPARTS.PORDERNUM.cast(Integer.class))
                .from(HASPARTS)
                .where(HASPARTS.PARTNUM.equal(id))
                .fetchOne();

        if (record != null) {
            final int ordNum = record.getValue(HASPARTS.PORDERNUM, Integer.class);
            if (record.getValue(HASPARTS.REPAIRSTATUS, String.class).equals(PartStatus.PLACED)) {
                try {
                    database.delete(HASPARTS)
                            .where(HASPARTS.PARTNUM.equal(id))
                            .execute();
                } catch (Exception e) {
                    return Response.status(Response.Status.BAD_REQUEST).build();
                }

                // Code below checks whether or not order has other parts.
                // If no more parts, we delete order

                final Record record1 = database.select()
                .from(HASPARTS)
                        .where(HASPARTS.PORDERNUM.equal(record.getValue(HASPARTS.PORDERNUM, Integer.class)))
                        .fetchAny();


                if (record1 == null) {
                    database.delete(ORDERS)
                            .where(ORDERS.ORDERNUM.equal(record.getValue(HASPARTS.PORDERNUM, Integer.class)))
                            .execute();
                } else {
                    // update price for order
                    database.fetch("UPDATE orders SET totalPrice = (SELECT ROUND(sum(sellPrice * qty), 2) FROM hasParts WHERE porderNum = ?) WHERE orderNum = ?", ordNum, ordNum)
                            .format();
                }
                return Response.ok().build();
            }
            return Response.status(Response.Status.METHOD_NOT_ALLOWED).build();
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

}
