package com.aviafix.resources;

import com.aviafix.api.*;
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
import static com.aviafix.db.generated.tables.PAYBYCREDITCARD.PAYBYCREDITCARD;
import static com.aviafix.db.generated.tables.CUSTOMER_USERS.CUSTOMER_USERS;
import static com.aviafix.db.generated.tables.PAYONLINE.PAYONLINE;

@Path("/epayments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ElectronicPaymentResource {

    // gets all credit card payments

    @GET
    @Timed
    public List<ElectronicPaymentReadRepresentation> getEpayments(
            @QueryParam("order") Optional<Integer> order,
            @QueryParam("customer") Optional<Integer> customer,
            @QueryParam("priceFrom") Optional<Double> priceFrom,
            @QueryParam("priceTo") Optional<Double> priceTo,
            @QueryParam("etid") Optional<Integer> etid,
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

        List<ElectronicPaymentReadRepresentation> representations = database.select(
                    PAYBYCREDITCARD.ETID,
                    PAYONLINE.ORDNUMPAYONL,
                    PAYBYCREDITCARD.CREDITCARDNUM,
                    PAYBYCREDITCARD.EXPDATE,
                    PAYBYCREDITCARD.CODE,
                    PAYBYCREDITCARD.CARDHOLDERNAME,
                    PAYBYCREDITCARD.AMOUNT,
                    PAYONLINE.CIDPAYONLINE,
                    PAYONLINE.PYMNTDATEONLINE)
                .from(PAYBYCREDITCARD)
                .join(PAYONLINE)
                .on(PAYBYCREDITCARD.ETID.equal(PAYONLINE.ETIDPAYONLINE))
                .where(OptionalFilter
                        .build()
                        .add(order.map(PAYONLINE.ORDNUMPAYONL::eq))
                        .add(customer.map(PAYONLINE.CIDPAYONLINE::eq))
                        .add(etid.map(PAYBYCREDITCARD.ETID::eq))
                        .add(priceFrom.map(PAYBYCREDITCARD.AMOUNT::ge))
                        .add(priceTo.map(PAYBYCREDITCARD.AMOUNT::le))
                        .combineWithAnd())
                .orderBy(DSL.field(
                        orderBy.orElse("ETID")
                ))
                .fetchInto(ElectronicPaymentReadRepresentation.class);

        return representations;
    }

    @GET
    @Path("/{id}")
    @Timed
    public ElectronicPaymentReadRepresentation getEpayments(
            @Context DSLContext database,
            @PathParam("id") Integer id
    ) {
        Record record = database.select(
                PAYBYCREDITCARD.ETID,
                PAYONLINE.ORDNUMPAYONL,
                PAYBYCREDITCARD.CREDITCARDNUM,
                PAYBYCREDITCARD.EXPDATE,
                PAYBYCREDITCARD.CODE,
                PAYBYCREDITCARD.CARDHOLDERNAME,
                PAYBYCREDITCARD.AMOUNT,
                PAYONLINE.CIDPAYONLINE,
                PAYONLINE.PYMNTDATEONLINE)
                .from(PAYBYCREDITCARD)
                .join(PAYONLINE)
                .on(PAYBYCREDITCARD.ETID.equal(PAYONLINE.ETIDPAYONLINE))
                .where(PAYBYCREDITCARD.ETID.eq(id))
                .fetchOne();
        return new ElectronicPaymentReadRepresentation (
                record.getValue(PAYBYCREDITCARD.ETID),
                record.getValue(PAYONLINE.ORDNUMPAYONL),
                record.getValue(PAYBYCREDITCARD.CREDITCARDNUM),
                record.getValue(PAYBYCREDITCARD.EXPDATE),
                record.getValue(PAYBYCREDITCARD.CODE),
                record.getValue(PAYBYCREDITCARD.CARDHOLDERNAME),
                record.getValue(PAYBYCREDITCARD.AMOUNT),
                record.getValue(PAYONLINE.CIDPAYONLINE),
                record.getValue(PAYONLINE.PYMNTDATEONLINE)
        );
    }

    // Create a new credit card payment
    @POST
    @Timed
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postEPayment(
            @Context DSLContext database,
            ElectronicPaymentWriteRepresentation epayment,
            @CookieParam("FixerUID") int fixerUID

    ) {
        final UserFullReadRepresentation user =
                UserReader.findUser(fixerUID)
                        .apply(database)
                        .orElseThrow(() -> new WebApplicationException(Response.Status.UNAUTHORIZED));

        if(!user.role.equals(Roles.CUSTOMER)){
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }

        Record rec = database.select(CUSTOMER_USERS.CID)
                .from(CUSTOMER_USERS)
                .where(CUSTOMER_USERS.CUID.eq(fixerUID))
                .fetchOne();

        int customerID = rec.getValue(CUSTOMER_USERS.CID);

        if (epayment.paymentAmount <= 5000) {

            final int orderID = epayment.orderNumber;

            Record record2 = database.select(
                    ORDERS.ORDERNUM,
                    ORDERS.ORDERCID,
                    ORDERS.ORDERSTATUS
            )
                    .from(ORDERS)
                    .where(ORDERS.ORDERNUM.equal(orderID), ORDERS.ORDERSTATUS.equal(OrderStatus.COMPLETE))
                    .fetchOne();

            if (record2 != null) {

                Record record = database.insertInto(
                        PAYBYCREDITCARD,
                        PAYBYCREDITCARD.CREDITCARDNUM,
                        PAYBYCREDITCARD.EXPDATE,
                        PAYBYCREDITCARD.CODE,
                        PAYBYCREDITCARD.CARDHOLDERNAME,
                        PAYBYCREDITCARD.AMOUNT
                ).values(
                        epayment.creditCardNumber,
                        epayment.expiryDate,
                        epayment.cardCode,
                        epayment.cardHolderName,
                        epayment.paymentAmount
                ).returning(
                        PAYBYCREDITCARD.ETID
                ).fetchOne();

                final int ETID = record.getValue(PAYBYCREDITCARD.ETID);

                database.insertInto(
                        PAYONLINE,
                        PAYONLINE.CIDPAYONLINE,
                        PAYONLINE.ORDNUMPAYONL,
                        PAYONLINE.ETIDPAYONLINE,
                        PAYONLINE.PYMNTDATEONLINE
                ).values(
                        customerID,
                        epayment.orderNumber,
                        ETID,
                        epayment.paymentDate
                ).execute();


                database.update(ORDERS)
                        .set(ORDERS.ORDERSTATUS, OrderStatus.PAID)
                        .where(ORDERS.ORDERNUM.equal(orderID))
                        .execute();

                return Response.ok().build();
            }
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.status(Response.Status.METHOD_NOT_ALLOWED).build();
    }

}
