package com.aviafix.resources;

import com.aviafix.api.ChequeWriteRepresentation;
import com.aviafix.api.OrderWriteRepresentation;
import com.aviafix.core.OrderStatus;
import com.aviafix.db.generated.tables.PAYBYCREDITCARD;
import com.aviafix.db.generated.tables.PAYOFFLINE;
import com.codahale.metrics.annotation.Timed;
import org.jooq.DSLContext;
import org.jooq.Record;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;

import static com.aviafix.db.generated.tables.ORDERS.ORDERS;
import static com.aviafix.db.generated.tables.PAYBYCHEQUE.PAYBYCHEQUE;
import static com.aviafix.db.generated.tables.PAYOFFLINE.PAYOFFLINE;

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

    int orderID;

    @GET
    @Timed
    public String getOrders(
            @Context DSLContext database
    ) {
        return database.select()
                .from(PAYBYCHEQUE)
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
    public Response postOrder(
            @Context DSLContext database,

            ChequeWriteRepresentation cheque
    ) {

        Record record = database.insertInto(
                PAYBYCHEQUE,
                PAYBYCHEQUE.AMOUNT,
                PAYBYCHEQUE.BANK,
                PAYBYCHEQUE.CHEQUENUM
        ).values(

                cheque.amount,
                cheque.bank,
                cheque.chequeNum
        ).returning(
                PAYBYCHEQUE.CHEQUENUM
        ).fetchOne();

        final int CHEQUENUM = record.getValue(PAYBYCHEQUE.CHEQUENUM);


        database.insertInto(
                PAYOFFLINE,
                PAYOFFLINE.CIDPAYOFFLINE,
                PAYOFFLINE.CQNUMPAYOFFLINE,
                PAYOFFLINE.FEIDPAYOFFLINE,
                PAYOFFLINE.ORNUMPAYOFFLINE,
                PAYOFFLINE.PYMNTDATEPAYOFFLINE
        ).values(
                cheque.cidpayOffline,
                cheque.orNumpayOffline,
                cheque.cqNumpayOffline,
                cheque.feidpayOffline,
                cheque.pymntDatepayOffline
        ).execute();

        final int orderID = cheque.orNumpayOffline;
        this.orderID = orderID;

        /*
        PUT:
        don't think about record
        update order status //OrderStatus.PAID
        */
        return Response.created(
                URI.create(
                        "cheque/" + CHEQUENUM)/*database
                        .select(DSL.max(DSL.field("orderNum", int.class)))
                        .from(DSL.table("orders"))
                        .fetchOne(0, int.class)
                )*/
        ).build();
    }

    @PUT
    @Timed
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateCheque(
            @Context DSLContext database,
            ChequeWriteRepresentation cheque
    ) {

        // Check if order exists in DB
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
                        "order/" + orderID)/*database
                        .select(DSL.max(DSL.field("orderNum", int.class)))
                        .from(DSL.table("orders"))
                        .fetchOne(0, int.class)
                )*/
        ).build();

    }




    /*
    Delete Not Needed
    */
}
