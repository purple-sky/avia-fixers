package com.aviafix.resources;

import com.aviafix.api.RepairReadRepresentation;
import com.aviafix.api.RepairWriteRepresentation;
import com.aviafix.api.UserFullReadRepresentation;
import com.aviafix.core.*;
import com.aviafix.tools.OptionalFilter;
import com.codahale.metrics.annotation.Timed;
import org.jooq.DSLContext;
import org.jooq.Record;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDate;
import java.util.List;


import static com.aviafix.db.generated.tables.HASPARTS.HASPARTS;
import static com.aviafix.db.generated.tables.REPAIR.REPAIR;
import static com.aviafix.db.generated.tables.EMPLOYEE_USERS.EMPLOYEE_USERS;
import static com.aviafix.db.generated.tables.REPAIRSHOP_EMPLOYEES.REPAIRSHOP_EMPLOYEES;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

/**
 * Created by AlexB on 2016-11-11.
 */
@Path("/schedule")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RepairScheduleResource {

    @GET
    @Timed
    public List<RepairReadRepresentation> getSchedule(
            @QueryParam("partN") Optional<Integer> partN,
            @QueryParam("order") Optional<Integer> order,
            @QueryParam("mechanic") Optional<Integer> mechanic,
            @QueryParam("shop") Optional<String> shop,
            @Context DSLContext database,
            @CookieParam("FixerUID") int fixerUID
    ) {
        LocalDate today = LocalDate.now();
        final UserFullReadRepresentation user =
                UserReader.findUser(fixerUID)
                        .apply(database)
                        .orElseThrow(() -> new WebApplicationException(Response.Status.UNAUTHORIZED));

        if(!user.role.equals(Roles.REPAIR)){
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }

        List<RepairReadRepresentation> schedule =
                database.select(
                        HASPARTS.PARTNUM,
                        HASPARTS.PARTNAME,
                        HASPARTS.REPAIRSTATUS,
                        HASPARTS.REPAIRDATE,
                        HASPARTS.PORDERNUM,
                        HASPARTS.QTY,
                        REPAIR.ERIDREPAIR,
                        EMPLOYEE_USERS.ENAME,
                        REPAIRSHOP_EMPLOYEES.SHOPNAME)
                .from(HASPARTS)
                        .leftJoin(REPAIR).on(HASPARTS.PARTNUM.eq(REPAIR.PNUMREPAIR))
                        .leftJoin(REPAIRSHOP_EMPLOYEES).on(REPAIR.ERIDREPAIR.eq(REPAIRSHOP_EMPLOYEES.REID))
                        .leftJoin(EMPLOYEE_USERS).on(REPAIR.ERIDREPAIR.eq(EMPLOYEE_USERS.EID))
                .where(HASPARTS.REPAIRSTATUS.eq(PartStatus.IN_PROGRESS)
                        .and(OptionalFilter
                                .build()
                                .add(partN.map(HASPARTS.PARTNUM::eq))
                                .add(order.map(HASPARTS.PORDERNUM::eq))
                                .add(mechanic.map(REPAIR.ERIDREPAIR::eq))
                                .add(shop.map(REPAIRSHOP_EMPLOYEES.SHOPNAME::contains))
                                .combineWithAnd()))
                .fetchInto(RepairReadRepresentation.class);

        for (RepairReadRepresentation r:schedule) {
            long daysBetween = ChronoUnit.DAYS.between(today, r.repairDate);
            if (daysBetween > 15) {
                r.setPriority(RepairPriority.LOW);
            } else if (daysBetween > 10) {
                r.setPriority(RepairPriority.MEDIUM);
            } else r.setPriority(RepairPriority.HIGH);
        }
        return schedule;
    }

    @GET
    @Path("/{id}")
    @Timed
    public List<RepairReadRepresentation> getPart(
            @Context DSLContext database,
            @PathParam("id") Integer id
    ) {
        LocalDate today = LocalDate.now();
        List<RepairReadRepresentation> schedule =
                database.select(
                        HASPARTS.PARTNUM,
                        HASPARTS.PARTNAME,
                        HASPARTS.REPAIRSTATUS,
                        HASPARTS.REPAIRDATE,
                        HASPARTS.PORDERNUM,
                        HASPARTS.QTY,
                        REPAIR.ERIDREPAIR,
                        EMPLOYEE_USERS.ENAME,
                        REPAIRSHOP_EMPLOYEES.SHOPNAME)
                        .from(HASPARTS)
                        .leftJoin(REPAIR).on(HASPARTS.PARTNUM.eq(REPAIR.PNUMREPAIR))
                        .leftJoin(REPAIRSHOP_EMPLOYEES).on(REPAIR.ERIDREPAIR.eq(REPAIRSHOP_EMPLOYEES.REID))
                        .leftJoin(EMPLOYEE_USERS).on(REPAIR.ERIDREPAIR.eq(EMPLOYEE_USERS.EID))
                        .where(HASPARTS.REPAIRSTATUS.eq(PartStatus.IN_PROGRESS)
                                .and(HASPARTS.PARTNUM.eq(id)))
                        .fetchInto(RepairReadRepresentation.class);
        for (RepairReadRepresentation r:schedule) {
            long daysBetween = ChronoUnit.DAYS.between(today, r.repairDate);
            if (daysBetween > 15) {
                r.setPriority(RepairPriority.LOW);
            } else if (daysBetween > 10) {
                r.setPriority(RepairPriority.MEDIUM);
            } else r.setPriority(RepairPriority.HIGH);
        }
        return schedule;
    }
    // TODO: fix sorting by priority for repairs

    @POST
    @Path("/{id}")
    @Timed
    @Consumes(MediaType.APPLICATION_JSON)
    public Response repair (
            @Context DSLContext database,
            @PathParam("id") Integer id,
            RepairWriteRepresentation repair,
            @CookieParam("FixerUID") int fixerUID
    ) {
        Record record = database.select(HASPARTS.PARTNUM, HASPARTS.PORDERNUM, HASPARTS.REPAIRSTATUS)
                .from(HASPARTS)
                .where(HASPARTS.PARTNUM.eq(id))
                .fetchOne();

        Record repairmanID = database.select(EMPLOYEE_USERS.EID)
                .from(EMPLOYEE_USERS)
                .where(EMPLOYEE_USERS.EUID.eq(fixerUID))
                .fetchOne();

        if (record != null && repairmanID != null) {
            int order = record.getValue(HASPARTS.PORDERNUM, Integer.class);
            String status = record.getValue(HASPARTS.REPAIRSTATUS, String.class);
            if (status.equals(PartStatus.IN_PROGRESS)) {
                database.insertInto(
                        REPAIR,
                        REPAIR.ERIDREPAIR,
                        REPAIR.PNUMREPAIR,
                        REPAIR.ORDNUMREPAIR
                )
                        .values(repairmanID.getValue(EMPLOYEE_USERS.EID),
                                id,
                                order)
                        .execute();
            }

            if (repair.status.equals(PartStatus.COMPLETE)) {
                database.update(HASPARTS)
                        .set(HASPARTS.REPAIRSTATUS, repair.status)
                        .set(HASPARTS.REPAIRDATE, LocalDate.now())
                        .where(HASPARTS.PARTNUM.equal(id))
                        .execute();

                database.fetch("UPDATE orders SET orderRepairDate = (SELECT MAX(repairDate) FROM hasParts WHERE porderNum = ?) WHERE orderNum = ?", order, order)
                        .format();

                    //check for all parts in order have "Complete" => update order status to "Complete"

                database.fetch("UPDATE orders SET orderStatus = CASE " +
                        "WHEN (SELECT COUNT(*) FROM hasParts WHERE porderNum = ?) = " +
                        "(SELECT COUNT(*) FROM hasParts WHERE porderNum = ? AND repairStatus = ?) " +
                        "THEN ? ELSE ? END " +
                        "WHERE orderNum = ?", order, order, PartStatus.COMPLETE, OrderStatus.COMPLETE, OrderStatus.IN_PROGRESS, order)
                        .format();
            }

            return Response.ok().build();
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

}
