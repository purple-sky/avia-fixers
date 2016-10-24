package com.aviafix;

import com.bendb.dropwizard.jooq.JooqFactory;
import io.dropwizard.Configuration;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.db.DataSourceFactory;
import org.hibernate.validator.constraints.*;

import javax.validation.Valid;
import javax.validation.constraints.*;

public class FixerConfiguration extends Configuration {
    @NotEmpty
    private String template;
    @NotEmpty
    private String defaultName = "Stranger";
    @Valid
    @NotNull
    @JsonProperty("database")
    private DataSourceFactory database = new DataSourceFactory();
    @NotNull
    @JsonProperty("jooq")
    private JooqFactory jooq = new JooqFactory();

    @JsonProperty
    public String getTemplate() {
        return template;
    }

    @JsonProperty
    public void setTemplate(String template) {
        this.template = template;
    }

    @JsonProperty
    public String getDefaultName() {
        return defaultName;
    }

    @JsonProperty
    public void setDefaultName(String name) {
        this.defaultName = name;
    }

    public DataSourceFactory getDataSourceFactory() {
        return database;
    }

    public JooqFactory getJooqFactory() {
        return jooq;
    }
}
