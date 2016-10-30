package com.aviafix.resources;

import com.aviafix.api.Saying;
import com.codahale.metrics.annotation.Timed;
import org.jooq.DSLContext;
//import org.jooq.Field;
//import org.jooq.Query;
import org.jooq.impl.DSL;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.concurrent.atomic.AtomicLong;
import java.util.Optional;

@Path("/aviafix")
@Produces(MediaType.APPLICATION_JSON)
public class FixerResource {
    private final String template;
    private final String defaultName;
    private final AtomicLong counter;

    public FixerResource(String template, String defaultName) {
        this.template = template;
        this.defaultName = defaultName;
        this.counter = new AtomicLong();
    }

    @GET
    @Timed
    public Saying sayHello(
            @QueryParam("name") Optional<String> name,
            @Context DSLContext database
    ) {
        //System.out.println(database.select().from("users").fetch().format());
        final String val1 = (database.select().from("users").fetch().format());

        final String val2 = database
                .select(DSL.field("partNum", int.class))
                .from(DSL.table("hasParts"))
                .where(DSL.field("partNum", int.class).eq(21))
                .fetch()
                .format();
        final String value = String.format(template, name.orElse(defaultName));

        final String val3 = database
                .fetch("SELECT partNum FROM hasParts WHERE partNum = ?", 21)
                .format();

        //return new Saying(counter.incrementAndGet(), value);
        return new Saying(counter.incrementAndGet(), val3);
    }
}

/*
@Path("/orders")
@Produces(MediaType.APPLICATION_JSON)
public class FixerResource {
    private final String template;
    private final String defaultName;
    private final AtomicLong counter;

    public FixerResource(String template, String defaultName) {
        this.template = template;
        this.defaultName = defaultName;
        this.counter = new AtomicLong();
    }

    @GET
    @Timed
    public String sayHello(
            @QueryParam("opartNum") Optional<Integer> opartNum,
            @Context DSLContext database
    ) {

        return database
                .fetch("SELECT * FROM orders WHERE opartNum = ?", opartNum.orElse(0))
                .format();
    }
}*/
