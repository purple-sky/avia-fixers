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

    @GET
    @Timed
    public String getParts(
            @Context DSLContext database
    ) {
        return database.select()
                .from(HASPARTS)
                .fetch()
                .format();
    }

     @PUT
     @Timed
     @Consumes(MediaType.APPLICATION_JSON)
     public String updatePart(
             @Context DSLContext database,
             PartsUpdateRepresentation part
     ) {
         final Record record = database.select(
                 HASPARTS.PARTNUM.cast(Integer.class),
                 HASPARTS.REPAIRSTATUS.cast(String.class),
                 HASPARTS.REPAIRCOST.cast(Double.class),
                 HASPARTS.SELLPRICE.cast(Double.class),
                 HASPARTS.REPAIRDATE,
                 HASPARTS.PORDERNUM.cast(Integer.class)
         )
                 .from(HASPARTS)
                 .where(HASPARTS.PARTNUM.equal(part.partNumber))
                 .fetchOne();


         // TODO: write check for status not to be paid
         if (record != null) {

             final Record recordOrder = database.select(
                     ORDERS.ORDERNUM.cast(Integer.class),
                     ORDERS.ORDERSTATUS.cast(String.class)
             )
                     .from(ORDERS)
                     .where(ORDERS.ORDERNUM.equal(record.getValue(HASPARTS.PORDERNUM, Integer.class)))
                     .fetchOne();

             // updating status
             if (part.repairStatus == null) {
                 database.update(HASPARTS)
                         .set(HASPARTS.REPAIRSTATUS,record.getValue(HASPARTS.REPAIRSTATUS, String.class))
                         .where(HASPARTS.PARTNUM.equal(part.partNumber))
                         .execute();
                 System.out.println("Part #" + part.partNumber + " same status");
             } else {
                 database.update(HASPARTS)
                         .set(HASPARTS.REPAIRSTATUS,part.repairStatus)
                         .where(HASPARTS.PARTNUM.equal(part.partNumber))
                         .execute();
                 System.out.println("Part #" + part.partNumber + " new status");
             }

             // updating repair cost
             if (part.repairCost == null) {
                 database.update(HASPARTS)
                         .set(HASPARTS.REPAIRCOST,record.getValue(HASPARTS.REPAIRCOST, Double.class))
                         .where(HASPARTS.PARTNUM.equal(part.partNumber))
                         .execute();
                 System.out.println("Part #" + part.partNumber + " same cost");
             } else {
                 database.update(HASPARTS)
                         .set(HASPARTS.REPAIRCOST,part.repairCost)
                         .where(HASPARTS.PARTNUM.equal(part.partNumber))
                         .execute();
                 System.out.println("Part #" + part.partNumber + " new cost");
             }

             // updating sell price
             if (part.sellPrice == null) {
                 database.update(HASPARTS)
                         .set(HASPARTS.SELLPRICE,record.getValue(HASPARTS.SELLPRICE, Double.class))
                         .where(HASPARTS.PARTNUM.equal(part.partNumber))
                         .execute();
                 System.out.println("Part #" + part.partNumber + " same sell price");
             } else {
                 database.update(HASPARTS)
                         .set(HASPARTS.SELLPRICE,part.sellPrice)
                         .where(HASPARTS.PARTNUM.equal(part.partNumber))
                         .execute();
                 System.out.println("Part #" + part.partNumber + " new sell price");
             }

             // updating repair date
             if (part.repairDate == null) {
                 database.update(HASPARTS)
                         .set(HASPARTS.REPAIRDATE,record.getValue(HASPARTS.REPAIRDATE))
                         .where(HASPARTS.PARTNUM.equal(part.partNumber))
                         .execute();
                 System.out.println("Part #" + part.partNumber + " same repair date");
             } else {
                 database.update(HASPARTS)
                         .set(HASPARTS.REPAIRDATE,part.repairDate)
                         .where(HASPARTS.PARTNUM.equal(part.partNumber))
                         .execute();
                 System.out.println("Part #" + part.partNumber + " new repair date");
             }

             return "Part #" + part.partNumber + " updated";
         } else {
             return "Part #" + part.partNumber + " doesn't exist";
         }







         /*final HASPARTSPROJECTION partProjection = database.update(HASPARTS)

                 .set(HASPARTS.REPAIRCOST,part.repairCost)
                 .set(HASPARTS.SELLPRICE, part.sellPrice)
                 .set(HASPARTS.REPAIRDATE,part.repairDate)
                 .where(HASPARTS.PARTNUM.equal(part.partNumber))
                 .returning()
                 .fetchOne()
                 .into(HASPARTSPROJECTION.class);

         if (partProjection.REPAIRCOST() != null &&
                 partProjection.SELLPRICE() != null &&
                 partProjection.REPAIRDATE() != null) {
             database.update(HASPARTS)
                     .set(HASPARTS.REPAIRSTATUS, PartStatus.IN_PROGRESS)
                     .where(HASPARTS.PORDERNUM.equal(partProjection.PORDERNUM()));
         }*/

     }


}
