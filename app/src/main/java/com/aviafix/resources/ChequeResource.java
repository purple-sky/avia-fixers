package com.aviafix.resources;

import com.aviafix.api.ChequeReadRepresentation;
import com.aviafix.api.ChequeWriteRepresentation;
import com.aviafix.api.UserFullReadRepresentation;
import com.aviafix.core.OrderStatus;
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
import java.util.List;
import java.util.Optional;

import static com.aviafix.db.generated.tables.ORDERS.ORDERS;
import static com.aviafix.db.generated.tables.PAYBYCHEQUE.PAYBYCHEQUE;
import static com.aviafix.db.generated.tables.PAYOFFLINE.PAYOFFLINE;
/**
 * Created by paull on 1/11/2016.
 * Modified by AlexB on 16/11/2016
 */

@Path("/cheque")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ChequeResource {
    //combine both cheque and payoffline
    //actions we can do for certain object
    //no put (no update)
    //no delete (cannot directly delete a cheque)

    @GET
    @Timed
    public List<ChequeReadRepresentation> getCheques(
            @QueryParam("order") Optional<Integer> order,
            @QueryParam("customer") Optional<Integer> customer,
            @QueryParam("priceFrom") Optional<Double> priceFrom,
            @QueryParam("priceTo") Optional<Double> priceTo,
            @QueryParam("cheque") Optional<Integer> cheque,
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

        List<ChequeReadRepresentation> representations = database.select(
                PAYBYCHEQUE.CHEQUENUM,
                PAYBYCHEQUE.BANK,
                PAYBYCHEQUE.AMOUNT,
                PAYOFFLINE.CIDPAYOFFLINE,
                PAYOFFLINE.ORNUMPAYOFFLINE,
                PAYOFFLINE.FEIDPAYOFFLINE,
                PAYOFFLINE.PYMNTDATEPAYOFFLINE.as("DATE"))
                .from(PAYBYCHEQUE)
                .join(PAYOFFLINE)
                .on(PAYBYCHEQUE.CHEQUENUM.equal(PAYOFFLINE.CQNUMPAYOFFLINE))
                .where(OptionalFilter
                        .build()
                        .add(order.map(PAYOFFLINE.ORNUMPAYOFFLINE::eq))
                        .add(customer.map(PAYOFFLINE.CIDPAYOFFLINE::eq))
                        .add(cheque.map(PAYBYCHEQUE.CHEQUENUM::eq))
                        .add(priceFrom.map(PAYBYCHEQUE.AMOUNT::ge))
                        .add(priceTo.map(PAYBYCHEQUE.AMOUNT::le))
                        .combineWithAnd())
                .orderBy(DSL.field(
                        orderBy.orElse("chequeNum")
                ))
                .fetchInto(ChequeReadRepresentation.class);

        return representations;
    }

    @GET
    @Path("/{id}")
    @Timed
    public ChequeReadRepresentation getCheque(
            @Context DSLContext database,
            @PathParam("id") Integer id
    ) {
        Record record = database.select(PAYBYCHEQUE.CHEQUENUM,
                PAYBYCHEQUE.BANK,
                PAYBYCHEQUE.AMOUNT,
                PAYOFFLINE.CIDPAYOFFLINE,
                PAYOFFLINE.ORNUMPAYOFFLINE,
                PAYOFFLINE.FEIDPAYOFFLINE,
                PAYOFFLINE.PYMNTDATEPAYOFFLINE)
                .from(PAYBYCHEQUE)
                .join(PAYOFFLINE)
                .on(PAYBYCHEQUE.CHEQUENUM.equal(PAYOFFLINE.CQNUMPAYOFFLINE))
                .where(PAYBYCHEQUE.CHEQUENUM.eq(id))
                .fetchOne();
        return new ChequeReadRepresentation (
                record.getValue(PAYBYCHEQUE.CHEQUENUM),
                record.getValue(PAYBYCHEQUE.BANK),
                record.getValue(PAYBYCHEQUE.AMOUNT),
                record.getValue(PAYOFFLINE.CIDPAYOFFLINE),
                record.getValue(PAYOFFLINE.ORNUMPAYOFFLINE),
                record.getValue(PAYOFFLINE.FEIDPAYOFFLINE),
                record.getValue(PAYOFFLINE.PYMNTDATEPAYOFFLINE)
        );
    }
    /*
    POST:
    first table: create a new entry for paybycheque
    second table: \/ payoffline
    Third table: payment status at table orders
    */
    @POST
    @Timed
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postCheque(
            @Context DSLContext database,
            ChequeWriteRepresentation cheque,
            @CookieParam("FixerUID") int fixerUID
    ) {

        final UserFullReadRepresentation user =
                UserReader.findUser(fixerUID)
                        .apply(database)
                        .orElseThrow(() -> new WebApplicationException(Response.Status.UNAUTHORIZED));

        if(!user.role.equals(Roles.FINANCE)){
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }

        int employeeID = user.employeeId;

        final Record orderRecord = database.select(ORDERS.ORDERNUM, ORDERS.ORDERSTATUS)
                .from(ORDERS)
                .where(ORDERS.ORDERNUM.equal(cheque.orderNumber))
                .fetchOne();

        if (orderRecord != null && orderRecord.getValue(ORDERS.ORDERSTATUS, String.class).equals(OrderStatus.COMPLETE)) {

            database.insertInto(
                    PAYBYCHEQUE,
                    PAYBYCHEQUE.CHEQUENUM,
                    PAYBYCHEQUE.BANK,
                    PAYBYCHEQUE.AMOUNT
            ).values(
                    cheque.chequeNumber,
                    cheque.bank,
                    cheque.amount
            ).execute();

            database.insertInto(
                    PAYOFFLINE,
                    PAYOFFLINE.CIDPAYOFFLINE,
                    PAYOFFLINE.CQNUMPAYOFFLINE,
                    PAYOFFLINE.ORNUMPAYOFFLINE,
                    PAYOFFLINE.FEIDPAYOFFLINE,
                    PAYOFFLINE.PYMNTDATEPAYOFFLINE
            ).values(
                    cheque.customerId,
                    cheque.chequeNumber,
                    cheque.orderNumber,
                    employeeID,
                    cheque.date
            ).execute();

            database.update(ORDERS)
                    .set(ORDERS.ORDERSTATUS, OrderStatus.PAID)
                    .where(ORDERS.ORDERNUM.equal(cheque.orderNumber))
                    .execute();

        /*
        PUT:
        don't think about record
        update order status //OrderStatus.PAID
        */
            return Response.ok().build();

        }
        return Response.status(Response.Status.METHOD_NOT_ALLOWED).build();
    }

    /*
    Put, Delete Not Needed
    */
}
