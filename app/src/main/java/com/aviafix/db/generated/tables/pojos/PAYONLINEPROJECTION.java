/**
 * This class is generated by jOOQ
 */
package com.aviafix.db.generated.tables.pojos;


import java.io.Serializable;
import java.time.LocalDate;

import javax.annotation.Generated;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.8.5"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class PAYONLINEPROJECTION implements Serializable {

    private static final long serialVersionUID = 129621842;

    private Integer   CIDPAYONLINE;
    private Integer   ORDNUMPAYONL;
    private Integer   ETIDPAYONLINE;
    private LocalDate PYMNTDATEONLINE;

    public PAYONLINEPROJECTION() {}

    public PAYONLINEPROJECTION(PAYONLINEPROJECTION value) {
        this.CIDPAYONLINE = value.CIDPAYONLINE;
        this.ORDNUMPAYONL = value.ORDNUMPAYONL;
        this.ETIDPAYONLINE = value.ETIDPAYONLINE;
        this.PYMNTDATEONLINE = value.PYMNTDATEONLINE;
    }

    public PAYONLINEPROJECTION(
        Integer   CIDPAYONLINE,
        Integer   ORDNUMPAYONL,
        Integer   ETIDPAYONLINE,
        LocalDate PYMNTDATEONLINE
    ) {
        this.CIDPAYONLINE = CIDPAYONLINE;
        this.ORDNUMPAYONL = ORDNUMPAYONL;
        this.ETIDPAYONLINE = ETIDPAYONLINE;
        this.PYMNTDATEONLINE = PYMNTDATEONLINE;
    }

    public Integer CIDPAYONLINE() {
        return this.CIDPAYONLINE;
    }

    public void CIDPAYONLINE(Integer CIDPAYONLINE) {
        this.CIDPAYONLINE = CIDPAYONLINE;
    }

    public Integer ORDNUMPAYONL() {
        return this.ORDNUMPAYONL;
    }

    public void ORDNUMPAYONL(Integer ORDNUMPAYONL) {
        this.ORDNUMPAYONL = ORDNUMPAYONL;
    }

    public Integer ETIDPAYONLINE() {
        return this.ETIDPAYONLINE;
    }

    public void ETIDPAYONLINE(Integer ETIDPAYONLINE) {
        this.ETIDPAYONLINE = ETIDPAYONLINE;
    }

    public LocalDate PYMNTDATEONLINE() {
        return this.PYMNTDATEONLINE;
    }

    public void PYMNTDATEONLINE(LocalDate PYMNTDATEONLINE) {
        this.PYMNTDATEONLINE = PYMNTDATEONLINE;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("PAYONLINEPROJECTION (");

        sb.append(CIDPAYONLINE);
        sb.append(", ").append(ORDNUMPAYONL);
        sb.append(", ").append(ETIDPAYONLINE);
        sb.append(", ").append(PYMNTDATEONLINE);

        sb.append(")");
        return sb.toString();
    }
}
