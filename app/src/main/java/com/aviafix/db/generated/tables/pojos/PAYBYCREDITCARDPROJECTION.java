/**
 * This class is generated by jOOQ
 */
package com.aviafix.db.generated.tables.pojos;


import java.io.Serializable;
import java.sql.Date;

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
public class PAYBYCREDITCARDPROJECTION implements Serializable {

    private static final long serialVersionUID = -369597715;

    private Integer ETID;
    private Integer CREDITCARDNUM;
    private Date    EXPDATE;
    private Integer CODE;
    private String  CARDHOLDERNAME;
    private Double  AMOUNT;

    public PAYBYCREDITCARDPROJECTION() {}

    public PAYBYCREDITCARDPROJECTION(PAYBYCREDITCARDPROJECTION value) {
        this.ETID = value.ETID;
        this.CREDITCARDNUM = value.CREDITCARDNUM;
        this.EXPDATE = value.EXPDATE;
        this.CODE = value.CODE;
        this.CARDHOLDERNAME = value.CARDHOLDERNAME;
        this.AMOUNT = value.AMOUNT;
    }

    public PAYBYCREDITCARDPROJECTION(
        Integer ETID,
        Integer CREDITCARDNUM,
        Date    EXPDATE,
        Integer CODE,
        String  CARDHOLDERNAME,
        Double  AMOUNT
    ) {
        this.ETID = ETID;
        this.CREDITCARDNUM = CREDITCARDNUM;
        this.EXPDATE = EXPDATE;
        this.CODE = CODE;
        this.CARDHOLDERNAME = CARDHOLDERNAME;
        this.AMOUNT = AMOUNT;
    }

    public Integer ETID() {
        return this.ETID;
    }

    public void ETID(Integer ETID) {
        this.ETID = ETID;
    }

    public Integer CREDITCARDNUM() {
        return this.CREDITCARDNUM;
    }

    public void CREDITCARDNUM(Integer CREDITCARDNUM) {
        this.CREDITCARDNUM = CREDITCARDNUM;
    }

    public Date EXPDATE() {
        return this.EXPDATE;
    }

    public void EXPDATE(Date EXPDATE) {
        this.EXPDATE = EXPDATE;
    }

    public Integer CODE() {
        return this.CODE;
    }

    public void CODE(Integer CODE) {
        this.CODE = CODE;
    }

    public String CARDHOLDERNAME() {
        return this.CARDHOLDERNAME;
    }

    public void CARDHOLDERNAME(String CARDHOLDERNAME) {
        this.CARDHOLDERNAME = CARDHOLDERNAME;
    }

    public Double AMOUNT() {
        return this.AMOUNT;
    }

    public void AMOUNT(Double AMOUNT) {
        this.AMOUNT = AMOUNT;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("PAYBYCREDITCARDPROJECTION (");

        sb.append(ETID);
        sb.append(", ").append(CREDITCARDNUM);
        sb.append(", ").append(EXPDATE);
        sb.append(", ").append(CODE);
        sb.append(", ").append(CARDHOLDERNAME);
        sb.append(", ").append(AMOUNT);

        sb.append(")");
        return sb.toString();
    }
}
