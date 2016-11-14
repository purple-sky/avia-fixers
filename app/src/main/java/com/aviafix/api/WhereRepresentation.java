package com.aviafix.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

/**
 * Created by AlexB on 2016-11-13.
 */
public class WhereRepresentation {

    public final Integer customerNumbr;
    public final Integer employee;
    public final Integer orderNum;
    public final Integer numberAny;
    public final String statusType;
    public final LocalDate dateFrom;
    public final LocalDate dateTo;
    public final Double priceFrom;
    public final Double priceTo;
    public final Double costFrom;
    public final Double costTo;
    public final String like;

    @JsonCreator
    public WhereRepresentation (
            @JsonProperty ("customer") Integer customerNumbr,
            @JsonProperty ("employee") Integer employee,
            @JsonProperty ("orderNum") Integer orderNum,
            @JsonProperty ("number") Integer numberAny,
            @JsonProperty ("statusType") String statusType,
            @JsonProperty ("dateFrom") LocalDate dateFrom,
            @JsonProperty ("dateTo") LocalDate dateTo,
            @JsonProperty ("priceFrom") Double priceFrom,
            @JsonProperty ("priceTo") Double priceTo,
            @JsonProperty ("costFrom") Double costFrom,
            @JsonProperty ("costTo") Double costTo,
            @JsonProperty ("like") String like
    ) {
        this.customerNumbr = customerNumbr;
        this.employee = employee;
        this.orderNum = orderNum;
        this.numberAny = numberAny;
        this.statusType =statusType;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.priceFrom = priceFrom;
        this.priceTo = priceTo;
        this.costFrom = costFrom;
        this.costTo = costTo;
        this.like = like;
    }

}
