/**
 * This class is generated by jOOQ
 */
package com.aviafix.db.generated.tables.pojos;


import java.io.Serializable;

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
public class USRNAME_PASSPROJECTION implements Serializable {

    private static final long serialVersionUID = 621813780;

    private String USRN;
    private String USRPASS;

    public USRNAME_PASSPROJECTION() {}

    public USRNAME_PASSPROJECTION(USRNAME_PASSPROJECTION value) {
        this.USRN = value.USRN;
        this.USRPASS = value.USRPASS;
    }

    public USRNAME_PASSPROJECTION(
        String USRN,
        String USRPASS
    ) {
        this.USRN = USRN;
        this.USRPASS = USRPASS;
    }

    public String USRN() {
        return this.USRN;
    }

    public void USRN(String USRN) {
        this.USRN = USRN;
    }

    public String USRPASS() {
        return this.USRPASS;
    }

    public void USRPASS(String USRPASS) {
        this.USRPASS = USRPASS;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("USRNAME_PASSPROJECTION (");

        sb.append(USRN);
        sb.append(", ").append(USRPASS);

        sb.append(")");
        return sb.toString();
    }
}
