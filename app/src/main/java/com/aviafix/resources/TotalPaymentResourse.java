package com.aviafix.resources;

import com.aviafix.api.*;
import com.aviafix.core.Roles;
import com.aviafix.core.UserReader;
import com.aviafix.tools.OptionalFilter;
import com.codahale.metrics.annotation.Timed;
import org.jooq.*;
import org.jooq.impl.DSL;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;

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
            @QueryParam("orderNum") Optional<Integer> orderNum,
            @QueryParam("customer") Optional<Integer> customer,
            @QueryParam("priceFrom") Optional<Double> priceFrom,
            @QueryParam("priceTo") Optional<Double> priceTo,
            @QueryParam("number") Optional<Integer> pymntID,
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


        List<TotalPaymentRepresentation> representations =
                database.select(
                        PAYBYCHEQUE.CHEQUENUM.as("NUMB"),
                        PAYOFFLINE.CIDPAYOFFLINE.as("CLIENT"),
                        PAYOFFLINE.PYMNTDATEPAYOFFLINE.as("DATE1"),
                        PAYOFFLINE.ORNUMPAYOFFLINE.as("ORDER1"), PAYBYCHEQUE.AMOUNT.as("AMOUNT1"),
                        val("Offline").as("TYPE1"))
                .from(PAYBYCHEQUE)
                .join(PAYOFFLINE)
                .on(PAYBYCHEQUE.CHEQUENUM.equal(PAYOFFLINE.CQNUMPAYOFFLINE))
                        .where(OptionalFilter
                                .build()
                                .add(orderNum.map(PAYOFFLINE.ORNUMPAYOFFLINE::eq))
                                .add(customer.map(PAYOFFLINE.CIDPAYOFFLINE::eq))
                                .add(pymntID.map(PAYBYCHEQUE.CHEQUENUM::eq))
                                .add(priceFrom.map(PAYBYCHEQUE.AMOUNT::ge))
                                .add(priceTo.map(PAYBYCHEQUE.AMOUNT::le))
                                .combineWithAnd())
                .union(database.select(
                                PAYBYCREDITCARD.ETID.as("NUMB"),
                                PAYONLINE.CIDPAYONLINE.as("CLIENT"),
                                PAYONLINE.PYMNTDATEONLINE.as("DATE1"),
                                PAYONLINE.ORDNUMPAYONL.as("ORDER1"),
                                PAYBYCREDITCARD.AMOUNT.as("AMOUNT1"),
                                val("Online").as("TYPE1"))
                                .from(PAYBYCREDITCARD)
                                .join(PAYONLINE)
                                .on(PAYBYCREDITCARD.ETID.equal(PAYONLINE.ETIDPAYONLINE))
                        .where(OptionalFilter
                                .build()
                                .add(orderNum.map(PAYONLINE.ORDNUMPAYONL::eq))
                                .add(customer.map(PAYONLINE.CIDPAYONLINE::eq))
                                .add(pymntID.map(PAYBYCREDITCARD.ETID::eq))
                                .add(priceFrom.map(PAYBYCREDITCARD.AMOUNT::ge))
                                .add(priceTo.map(PAYBYCREDITCARD.AMOUNT::le))
                                .combineWithAnd()))
                .orderBy(DSL.field(
                        orderBy.orElse("NUMB")
                ))
                .fetchInto(TotalPaymentRepresentation.class);

        return representations;
    }
}
