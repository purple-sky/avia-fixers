package com.aviafix.resources;

import com.aviafix.api.RepairReadRepresentation;
import com.aviafix.api.RepairWriteRepresentation;
import com.aviafix.core.OrderStatus;
import com.aviafix.core.PartStatus;
import com.aviafix.core.RepairPriority;
import com.codahale.metrics.annotation.Timed;
import org.jooq.DSLContext;
import org.jooq.Record;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;


import static com.aviafix.db.generated.tables.HASPARTS.HASPARTS;
import static com.aviafix.db.generated.tables.REPAIR.REPAIR;
import static com.aviafix.db.generated.tables.EMPLOYEE_USERS.EMPLOYEE_USERS;
import static com.aviafix.db.generated.tables.REPAIRSHOP_EMPLOYEES.REPAIRSHOP_EMPLOYEES;
import static org.jooq.impl.DSL.val;
import java.time.temporal.ChronoUnit;

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
            @Context DSLContext database
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
                .where(HASPARTS.REPAIRSTATUS.notEqual(PartStatus.COMPLETE))
                .fetchInto(RepairReadRepresentation.class);
        for (RepairReadRepresentation r:schedule) {
            long daysBetween = ChronoUnit.DAYS.between(r.repairDate, today);
            if (daysBetween > 15) {
                r.setPriority(RepairPriority.LOW);
            } else if (daysBetween > 10) {
                r.setPriority(RepairPriority.MEDIUM);
            } else r.setPriority(RepairPriority.HIGH);
        }
        return schedule;
    }

    @GET
    @Path("/:{id}")
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
                        .where(HASPARTS.REPAIRSTATUS.notEqual(PartStatus.COMPLETE)
                                .and(HASPARTS.PARTNUM.eq(id)))
                        .fetchInto(RepairReadRepresentation.class);
        for (RepairReadRepresentation r:schedule) {
            long daysBetween = ChronoUnit.DAYS.between(r.repairDate, today);
            if (daysBetween > 15) {
                r.setPriority(RepairPriority.LOW);
            } else if (daysBetween > 10) {
                r.setPriority(RepairPriority.MEDIUM);
            } else r.setPriority(RepairPriority.HIGH);
        }
        return schedule;
    }

    @POST
    @Path("/{id}")
    @Timed
    @Consumes(MediaType.APPLICATION_JSON)
    public Response repair (
            @Context DSLContext database,
            @PathParam("id") Integer id,
            RepairWriteRepresentation repair
    ) {
        Record record = database.select(HASPARTS.PARTNUM, HASPARTS.PORDERNUM, HASPARTS.REPAIRSTATUS)
                .from(HASPARTS)
                .where(HASPARTS.PARTNUM.eq(id))
                .fetchOne();
        Record record2 = database.select(EMPLOYEE_USERS.EID)
                .from(EMPLOYEE_USERS)
                .where(EMPLOYEE_USERS.EID.eq(repair.mechanic))
                .fetchOne();
        if (record != null && record2 != null) {
            int order = record.getValue(HASPARTS.PORDERNUM, Integer.class);
            String status = record.getValue(HASPARTS.REPAIRSTATUS, String.class);
            if (status.equals(PartStatus.IN_PROGRESS)) {
                database.insertInto(
                        REPAIR,
                        REPAIR.ERIDREPAIR,
                        REPAIR.PNUMREPAIR,
                        REPAIR.ORDNUMREPAIR
                )
                        .values(repair.mechanic,
                                id,
                                order)
                        .execute();

                if (repair.status.equals(PartStatus.COMPLETE)) {
                    database.update(HASPARTS)
                            .set(HASPARTS.REPAIRSTATUS, repair.status)
                            .where(HASPARTS.PARTNUM.equal(id))
                            .execute();

                    //check for all parts in order have "Complete" => update order status to "Complete"

                    database.fetch("UPDATE orders SET orderStatus = CASE " +
                            "WHEN (SELECT COUNT(*) FROM hasParts WHERE porderNum = ?) = " +
                            "(SELECT COUNT(*) FROM hasParts WHERE porderNum = ? AND repairStatus = ?) " +
                            "THEN ? ELSE ? END " +
                            "WHERE orderNum = ?", order, order, PartStatus.COMPLETE, OrderStatus.COMPLETE, OrderStatus.IN_PROGRESS, order)
                            .format();
                }
            }

        }
        return Response.created(
                URI.create(
                        "/view")).build();
    }

}
