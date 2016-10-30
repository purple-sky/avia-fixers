/**
 * This class is generated by jOOQ
 */
package com.aviafix.db.generated.tables;


import com.aviafix.db.DateConverter;
import com.aviafix.db.generated.Aviafixers;
import com.aviafix.db.generated.Keys;
import com.aviafix.db.generated.tables.records.ORDERSRECORD;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.TableImpl;


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
public class ORDERS extends TableImpl<ORDERSRECORD> {

    private static final long serialVersionUID = -1842821437;

    /**
     * The reference instance of <code>AviaFixers.orders</code>
     */
    public static final ORDERS ORDERS = new ORDERS();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<ORDERSRECORD> getRecordType() {
        return ORDERSRECORD.class;
    }

    /**
     * The column <code>AviaFixers.orders.orderNum</code>.
     */
    public final TableField<ORDERSRECORD, Integer> ORDERNUM = createField("orderNum", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>AviaFixers.orders.date</code>.
     */
    public final TableField<ORDERSRECORD, LocalDate> DATE = createField("date", org.jooq.impl.SQLDataType.DATE, this, "", new DateConverter());

    /**
     * The column <code>AviaFixers.orders.orderStatus</code>.
     */
    public final TableField<ORDERSRECORD, String> ORDERSTATUS = createField("orderStatus", org.jooq.impl.SQLDataType.VARCHAR.length(20).defaultValue(org.jooq.impl.DSL.inline("placed", org.jooq.impl.SQLDataType.VARCHAR)), this, "");

    /**
     * The column <code>AviaFixers.orders.orderRepairDate</code>.
     */
    public final TableField<ORDERSRECORD, LocalDate> ORDERREPAIRDATE = createField("orderRepairDate", org.jooq.impl.SQLDataType.DATE, this, "", new DateConverter());

    /**
     * The column <code>AviaFixers.orders.ordercID</code>.
     */
    public final TableField<ORDERSRECORD, Integer> ORDERCID = createField("ordercID", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>AviaFixers.orders.totalPrice</code>.
     */
    public final TableField<ORDERSRECORD, Double> TOTALPRICE = createField("totalPrice", org.jooq.impl.SQLDataType.DOUBLE, this, "");

    /**
     * Create a <code>AviaFixers.orders</code> table reference
     */
    public ORDERS() {
        this("orders", null);
    }

    /**
     * Create an aliased <code>AviaFixers.orders</code> table reference
     */
    public ORDERS(String alias) {
        this(alias, ORDERS);
    }

    private ORDERS(String alias, Table<ORDERSRECORD> aliased) {
        this(alias, aliased, null);
    }

    private ORDERS(String alias, Table<ORDERSRECORD> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, "");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return Aviafixers.AVIAFIXERS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Identity<ORDERSRECORD, Integer> getIdentity() {
        return Keys.IDENTITY_ORDERS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<ORDERSRECORD> getPrimaryKey() {
        return Keys.KEY_ORDERS_PRIMARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<ORDERSRECORD>> getKeys() {
        return Arrays.<UniqueKey<ORDERSRECORD>>asList(Keys.KEY_ORDERS_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ForeignKey<ORDERSRECORD, ?>> getReferences() {
        return Arrays.<ForeignKey<ORDERSRECORD, ?>>asList(Keys.ORDERCID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ORDERS as(String alias) {
        return new ORDERS(alias, this);
    }

    /**
     * Rename this table
     */
    public ORDERS rename(String name) {
        return new ORDERS(name, null);
    }
}
