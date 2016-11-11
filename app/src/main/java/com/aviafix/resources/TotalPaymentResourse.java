package com.aviafix.resources;

import com.aviafix.api.ElectronicPaymentWriteRepresentation;
import com.aviafix.api.OrderWriteRepresentation;
import com.aviafix.api.PartsReadRepresentation;
import com.aviafix.api.TotalPaymentRepresentation;
import com.aviafix.core.OrderStatus;
import com.aviafix.db.generated.tables.PAYBYCHEQUE;
import com.codahale.metrics.annotation.Timed;
import com.codahale.metrics.health.HealthCheck;
import org.jooq.*;
import org.jooq.impl.DSL;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static com.aviafix.db.generated.tables.HASPARTS.HASPARTS;
import static com.aviafix.db.generated.tables.ORDERS.ORDERS;
import static com.aviafix.db.generated.tables.PAYBYCREDITCARD.PAYBYCREDITCARD;
import static com.aviafix.db.generated.tables.PAYONLINE.PAYONLINE;
import static com.aviafix.db.generated.tables.PAYBYCHEQUE.PAYBYCHEQUE;
import static com.aviafix.db.generated.tables.PAYOFFLINE.PAYOFFLINE;
import static org.jooq.impl.DSL.val;

/**
 * Created by AlexB on 2016-11-10.
 */

@Path("/payments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)


public class TotalPaymentResourse {

    @GET
    @Timed
    public List<TotalPaymentRepresentation> getPayments(
            @Context DSLContext database
    ) {
        List<TotalPaymentRepresentation> representations = database.select(PAYBYCHEQUE.CHEQUENUM.as("NUMB"), PAYOFFLINE.CIDPAYOFFLINE.as("CLIENT"),
                                        PAYOFFLINE.PYMNTDATEPAYOFFLINE.as("DATE"),
                                        PAYOFFLINE.ORNUMPAYOFFLINE.as("ORDER"), PAYBYCHEQUE.AMOUNT.as("AMOUNT"),
                                        val("Offline").as("TYPE"))
                .from(PAYBYCHEQUE)
                .join(PAYOFFLINE)
                .on(PAYBYCHEQUE.CHEQUENUM.equal(PAYOFFLINE.CQNUMPAYOFFLINE))
                .union(database.select(PAYBYCREDITCARD.ETID, PAYONLINE.CIDPAYONLINE, PAYONLINE.PYMNTDATEONLINE, PAYONLINE.ORDNUMPAYONL, PAYBYCREDITCARD.AMOUNT, val("Online"))
                                .from(PAYBYCREDITCARD)
                                .join(PAYONLINE)
                                .on(PAYBYCREDITCARD.ETID.equal(PAYONLINE.ETIDPAYONLINE)))
                .fetchInto(TotalPaymentRepresentation.class);

        return representations;
    }
}
