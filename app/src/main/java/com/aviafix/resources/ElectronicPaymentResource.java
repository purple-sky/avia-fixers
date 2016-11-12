package com.aviafix.resources;

import com.aviafix.api.ChequeReadRepresentation;
import com.aviafix.api.ElectronicPaymentReadRepresentation;
import com.aviafix.api.ElectronicPaymentWriteRepresentation;
import com.aviafix.api.OrderWriteRepresentation;
import com.aviafix.core.OrderStatus;
import com.codahale.metrics.annotation.Timed;
import org.jooq.DSLContext;
import org.jooq.Record;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;

import static com.aviafix.db.generated.tables.ORDERS.ORDERS;
import static com.aviafix.db.generated.tables.PAYBYCHEQUE.PAYBYCHEQUE;
import static com.aviafix.db.generated.tables.PAYBYCREDITCARD.PAYBYCREDITCARD;
import static com.aviafix.db.generated.tables.PAYOFFLINE.PAYOFFLINE;
import static com.aviafix.db.generated.tables.PAYONLINE.PAYONLINE;
import static org.jooq.impl.DSL.val;

@Path("/epayments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ElectronicPaymentResource {

    // gets all credit card payments

    @GET
    @Timed
    public List<ElectronicPaymentReadRepresentation> getEpayments(
            @Context DSLContext database
    ) {
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
                .fetchInto(ElectronicPaymentReadRepresentation.class);

        return representations;
    }

    // TODO: make return an object
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
            ElectronicPaymentWriteRepresentation epayment
    ) {

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
                epayment.customerID,
                epayment.orderNumber,
                ETID,
                epayment.paymentDate
        ).execute();

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
            database.update(ORDERS)
                    .set(ORDERS.ORDERSTATUS, OrderStatus.PAID)
                    .where(ORDERS.ORDERNUM.equal(orderID))
                    .execute();
        }

        return Response.created(
                URI.create(
                        "epayments/" + ETID)/*database
                        .select(DSL.max(DSL.field("ETID", int.class)))
                        .from(DSL.table("payByCreditCard"))
                        .fetchOne(0, int.class)
                )*/
        ).build();
    }

}
