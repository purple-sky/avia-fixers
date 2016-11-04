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
import org.jooq.*;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;
import java.util.Optional;

import static com.aviafix.db.generated.tables.ORDERS.ORDERS;
import static com.aviafix.db.generated.tables.HASPARTS.HASPARTS;
import static org.jooq.impl.DSL.select;
import static org.jooq.impl.DSL.sum;
import static org.jooq.impl.DSL.val;

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

         final int ordNum = record.getValue(HASPARTS.PORDERNUM, Integer.class);

         // TODO: write check for status not to be paid
         if (record != null) {

             /*final Record recordOrder = database.select(
                     ORDERS.ORDERNUM.cast(Integer.class),
                     ORDERS.ORDERSTATUS.cast(String.class)
             )
                     .from(ORDERS)
                     .where(ORDERS.ORDERNUM.equal(record.getValue(HASPARTS.PORDERNUM, Integer.class)))
                     .fetchOne();*/

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

                 //TODO: add check for all parts in order have "Complete" => update order status to complete
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

                 // update Order price = part_sell_price*qty

                 database.fetch("UPDATE orders SET totalPrice = (SELECT ROUND(sum(sellPrice * qty), 2) FROM hasParts WHERE porderNum = ?) WHERE orderNum = ?", ordNum, ordNum)
                         .format();
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

                 // TODO: update Order repair date = max repair date for all parts
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

    @DELETE
    @Path("/{id}")
    public String deletePart (
            @Context DSLContext database,
            @PathParam("id") Integer id
    ) {

        final Record record = database.select(
                HASPARTS.PARTNUM.cast(Integer.class),
                HASPARTS.REPAIRSTATUS.cast(String.class),
                HASPARTS.PORDERNUM.cast(Integer.class))
                .from(HASPARTS)
                .where(HASPARTS.PARTNUM.equal(id))
                .fetchOne();

        if (record != null) {
            if (record.getValue(HASPARTS.REPAIRSTATUS, String.class).equals(PartStatus.PLACED)) {
                try {
                    database.delete(HASPARTS)
                            .where(HASPARTS.PARTNUM.equal(id))
                            .execute();
                } catch (Exception e) {
                    return "Sorry something went wrong";
                }

                // Code below checks whether or not order has other parts.
                // If no more parts, we delete order

                final Record record1 = database.select()
                .from(HASPARTS)
                        .where(HASPARTS.PORDERNUM.equal(record.getValue(HASPARTS.PORDERNUM, Integer.class)))
                        .fetchAny();


                if (record1 == null) {
                    database.delete(ORDERS)
                            .where(ORDERS.ORDERNUM.equal(record.getValue(HASPARTS.PORDERNUM, Integer.class)))
                            .execute();
                } else {
                    // TODO: update price for order
                }

                return "Part #" + id + " deleted";
            }
            return "Part #" + id + " can't be deleted";
        }
        return "Part doesn't exist";
    }

}
