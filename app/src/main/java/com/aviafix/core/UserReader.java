package com.aviafix.core;

import com.aviafix.api.UserFullReadRepresentation;
import org.jooq.DSLContext;

import java.util.Optional;
import java.util.function.Function;

import static com.aviafix.db.generated.tables.CUSTOMER_USERS.CUSTOMER_USERS;
import static com.aviafix.db.generated.tables.EMPLOYEE_USERS.EMPLOYEE_USERS;
import static com.aviafix.db.generated.tables.USERS.USERS;

public class UserReader {
    public static Function<DSLContext, Optional<UserFullReadRepresentation>> findUser(int userId){
        return db -> Optional.ofNullable(db
                .select(
                        USERS.UID,
                        USERS.USRNAME,
                        USERS.USRTYPE,
                        CUSTOMER_USERS.CID,
                        EMPLOYEE_USERS.EID
                )
                .from(USERS)
                .leftOuterJoin(CUSTOMER_USERS).on(USERS.UID.eq(CUSTOMER_USERS.CUID))
                .leftOuterJoin(EMPLOYEE_USERS).on(USERS.UID.eq(EMPLOYEE_USERS.EUID))
                .where(USERS.UID.eq(userId))
                .fetchOneInto(UserFullReadRepresentation.class));
    }
}
