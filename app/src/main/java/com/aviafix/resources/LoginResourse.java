package com.aviafix.resources;

import com.aviafix.api.UserLoginRepresentation;
import com.aviafix.api.UserReadRepresentation;
import org.jooq.DSLContext;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.*;
import java.util.Optional;

import static com.aviafix.db.generated.tables.USERS.USERS;
import static com.aviafix.db.generated.tables.USRNAME_PASS.USRNAME_PASS;

/**
 * Created by AlexB on 2016-11-06.
 */
@Path("/login")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class LoginResourse {
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response logInUser(
            UserLoginRepresentation user,
            @Context DSLContext database
    ) {
        return Optional
                .ofNullable(
                        database.select(USERS.UID, USERS.USRTYPE)
                                .from(USERS)
                                .join(USRNAME_PASS).on(USRNAME_PASS.USRN.eq(USERS.USRNAME))
                                .where(USERS.USRNAME.eq(user.name))
                                .and(USRNAME_PASS.USRPASS.eq(user.password))
                                .fetchOne()
                )
                .map(record -> Response
                            .ok()
                            .cookie(new NewCookie(new Cookie("FixerUID", record.value1().toString())))
                            .entity(new UserReadRepresentation(
                                    record.value1(),
                                    user.name,
                                    record.value2()
                            ))
                            .build()
                )
                .orElse(
                        Response
                                .status(Response.Status.UNAUTHORIZED)
                                .entity("Can't log in, please check login/password")
                                .build()
                );
    }

}
