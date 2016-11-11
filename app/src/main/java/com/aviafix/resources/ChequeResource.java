package com.aviafix.resources;

import com.aviafix.api.ChequeReadRepresentation;
import com.aviafix.api.ChequeWriteRepresentation;
import com.aviafix.api.TotalPaymentRepresentation;
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

/**
 * Created by paull on 1/11/2016.
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
    public List<ChequeReadRepresentation> getOrders(
            @Context DSLContext database
    ) {
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
                .fetchInto(ChequeReadRepresentation.class);

        return representations;
    }

    // TODO: make return an object
    @GET
    @Path("/{id}")
    @Timed
    public String getEpayments(
            @Context DSLContext database,
            @PathParam("id") Integer id
    ) {
        return database.select()
                .from(PAYBYCHEQUE)
                .where(PAYBYCHEQUE.CHEQUENUM.equal(id))
                .fetch()
                .format();
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
            ChequeWriteRepresentation cheque
    ) {

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
                    cheque.financeEmployeeId,
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
            return Response.created(
                    URI.create("cheque/" + cheque.chequeNumber))
                    .build();

        }
        return Response.created(
                URI.create(
                        "orders/error"))
                .build();
    }

    /*
    Put, Delete Not Needed
    */
}
