package com.aviafix.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Length;

public class Saying {
    public final long id;

    //@Length(max = 3)
    public String content;

    /*public Saying() {
        // Jackson deserialization
    }*/

    public Saying(long id, String content) {
        this.id = id;
        this.content = content;
    }

    /*@JsonProperty
    public long getId() {
        return id;
    }

    @JsonProperty
    public String getContent() {
        return content;
    }*/
}