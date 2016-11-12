package com.aviafix.resources;

import com.aviafix.api.OrderReadRepresentation;
import com.aviafix.client.BasicAuthenticator;
import com.aviafix.client.User;
import com.codahale.metrics.annotation.Timed;
import io.dropwizard.auth.Auth;
import io.dropwizard.auth.basic.BasicCredentials;
import org.jooq.DSLContext;
import org.jooq.Record;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.stream.Collectors;

import static com.aviafix.db.generated.tables.USERS.USERS;
import static com.aviafix.db.generated.tables.USRNAME_PASS.USRNAME_PASS;

/**
 * Created by AlexB on 2016-11-06.
 */
@Path("/login")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LoginResourse {
    // gets all orders
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String logInUser(
            @Auth User user,
            @Context DSLContext database) {

        final Record login = database.select(USERS.USRNAME, USERS.USRTYPE)
                .from(USERS)
                .where(USERS.USRNAME.equal(user.getName()))
                .fetchOne();
        final Record password = database.select(USRNAME_PASS.USRPASS)
                .from(USRNAME_PASS)
                .where(USRNAME_PASS.USRN.equal(user.getName()))
                .fetchOne();

        if (login == null || password == null) {
            return "Can't log in, please check login/password";
        }
        User newUser = new User(login.getValue(USERS.USRNAME, String.class), login.getValue(USERS.USRTYPE, String.class));
        return "Welcome " + newUser.getName() + " with role: " + newUser.getRole();
    }

}
