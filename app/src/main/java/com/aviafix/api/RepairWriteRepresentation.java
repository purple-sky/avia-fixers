package com.aviafix.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.List;

/**
 * Created by AlexB on 2016-11-11.
 */
public class RepairWriteRepresentation {


    public final Integer mechanic;
    public final String status;

    @JsonCreator
    public RepairWriteRepresentation(
            @JsonProperty("mechanic") Integer mechanic,
            @JsonProperty("status") String status
    ) {

        this.mechanic = mechanic;
        this.status = status;
    }

}
