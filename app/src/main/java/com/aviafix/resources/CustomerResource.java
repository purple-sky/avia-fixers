package com.aviafix.resources;

import com.aviafix.api.CustomerReadRepresentation;
import com.aviafix.api.UserFullReadRepresentation;
import com.aviafix.core.Roles;
import com.aviafix.core.UserReader;
import com.codahale.metrics.annotation.Timed;
import org.jooq.DSLContext;
import org.jooq.Record;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static com.aviafix.db.generated.tables.CUSTOMER_USERS.CUSTOMER_USERS;

/**
 * Created by AlexB on 2016-11-19.
 */

@Path("/customer")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)

public class CustomerResource {

    @GET
    @Timed
    public CustomerReadRepresentation getCustomer(
            @Context DSLContext database,
            @CookieParam("FixerUID") int fixerUID
    ) {
        final UserFullReadRepresentation user =
                UserReader.findUser(fixerUID)
                        .apply(database)
                        .orElseThrow(() -> new WebApplicationException(Response.Status.UNAUTHORIZED));

        if(!user.role.equals(Roles.CUSTOMER)){
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }

        Record record =  database
                .select(CUSTOMER_USERS.CUSTNAME, CUSTOMER_USERS.CUSTADDRESS, CUSTOMER_USERS.PHONENUM)
                .from(CUSTOMER_USERS)
                .where(CUSTOMER_USERS.CID.eq(user.customerId))
                .fetchOne();
        return new CustomerReadRepresentation(
                user.customerId,
                record.getValue(CUSTOMER_USERS.CUSTNAME,String.class),
                record.getValue(CUSTOMER_USERS.CUSTADDRESS, String.class),
                record.getValue(CUSTOMER_USERS.PHONENUM, Integer.class)
        );
    }
}