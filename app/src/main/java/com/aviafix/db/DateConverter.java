package com.aviafix.db;

import org.jooq.Converter;

import java.sql.Date;
import java.time.LocalDate;

/**
 * Created by OrangeLion on 2016-10-30.
 */
public class DateConverter implements Converter<Date, LocalDate> {
    @Override public LocalDate from(Date date) { return date == null ? null : date.toLocalDate(); }
    @Override public Date to(LocalDate ld) { return ld == null ? null : Date.valueOf(ld); }
    @Override public Class<Date> fromType() { return Date.class; }
    @Override public Class<LocalDate> toType() { return LocalDate.class; }
}
