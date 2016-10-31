package com.aviafix.resources;

import com.aviafix.api.OrderWriteRepresentation;
import com.aviafix.api.PartsUpdateRepresentation;
import com.aviafix.api.PartsWriteRepresentation;
import com.aviafix.core.OrderStatus;
import com.aviafix.core.PartStatus;
import com.aviafix.db.generated.tables.HASPARTS;
import com.aviafix.db.generated.tables.pojos.HASPARTSPROJECTION;
import com.aviafix.db.generated.tables.records.HASPARTSRECORD;
import com.codahale.metrics.annotation.Timed;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;
import java.util.Optional;

import static com.aviafix.db.generated.tables.ORDERS.ORDERS;
import static com.aviafix.db.generated.tables.HASPARTS.HASPARTS;

/**
 * Created by AlexB on 2016-10-30.
 */
@Path("/parts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PartsResourse {

     @PUT
     @Timed
     @Consumes(MediaType.APPLICATION_JSON)
     public Response updatePart(
             @Context DSLContext database,
             PartsUpdateRepresentation part
     ) {



         final HASPARTSPROJECTION partProjection = database.update(HASPARTS)

                 .set(HASPARTS.REPAIRCOST,part.repairCost)
                 .set(HASPARTS.SELLPRICE, part.sellPrice)
                 .set(HASPARTS.REPAIRDATE,part.repairDate)
                 .where(HASPARTS.PARTNUM.equal(part.partNumber))
                 .returning()
                 .fetchOne()
                 .into(HASPARTSPROJECTION.class);

         /*if (partProjection.REPAIRCOST() != null &&
                 partProjection.SELLPRICE() != null &&
                 partProjection.REPAIRDATE() != null) {
             database.update(HASPARTS)
                     .set(HASPARTS.REPAIRSTATUS, PartStatus.IN_PROGRESS)
                     .where(HASPARTS.PORDERNUM.equal(partProjection.PORDERNUM()));
         }*/

         return Response.created(
                 URI.create(
                         "parts/" + part.partNumber)/*database
                        .select(DSL.max(DSL.field("orderNum", int.class)))
                        .from(DSL.table("orders"))
                        .fetchOne(0, int.class)
                )*/
         ).build();
     }


}
