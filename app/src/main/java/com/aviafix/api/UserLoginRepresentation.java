package com.aviafix.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by AlexB on 2016-10-29.
 */
public class UserLoginRepresentation {
    public final String name;
    public final String password;

    @JsonCreator
    public UserLoginRepresentation(
            @JsonProperty("name") String name,
            @JsonProperty("password") String password
    ) {
        this.name = name;
        this.password = password;
    }
}
