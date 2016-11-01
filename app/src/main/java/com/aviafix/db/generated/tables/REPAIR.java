/**
 * This class is generated by jOOQ
 */
package com.aviafix.db.generated.tables;


import com.aviafix.db.generated.Aviafixers;
import com.aviafix.db.generated.Keys;
import com.aviafix.db.generated.tables.records.REPAIRRECORD;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.ForeignKey;
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
public class REPAIR extends TableImpl<REPAIRRECORD> {

    private static final long serialVersionUID = 347194657;

    /**
     * The reference instance of <code>AviaFixers.repair</code>
     */
    public static final REPAIR REPAIR = new REPAIR();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<REPAIRRECORD> getRecordType() {
        return REPAIRRECORD.class;
    }

    /**
     * The column <code>AviaFixers.repair.eridrepair</code>.
     */
    public final TableField<REPAIRRECORD, Integer> ERIDREPAIR = createField("eridrepair", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>AviaFixers.repair.pNumrepair</code>.
     */
    public final TableField<REPAIRRECORD, Integer> PNUMREPAIR = createField("pNumrepair", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>AviaFixers.repair.ordNumrepair</code>.
     */
    public final TableField<REPAIRRECORD, Integer> ORDNUMREPAIR = createField("ordNumrepair", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * Create a <code>AviaFixers.repair</code> table reference
     */
    public REPAIR() {
        this("repair", null);
    }

    /**
     * Create an aliased <code>AviaFixers.repair</code> table reference
     */
    public REPAIR(String alias) {
        this(alias, REPAIR);
    }

    private REPAIR(String alias, Table<REPAIRRECORD> aliased) {
        this(alias, aliased, null);
    }

    private REPAIR(String alias, Table<REPAIRRECORD> aliased, Field<?>[] parameters) {
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
    public UniqueKey<REPAIRRECORD> getPrimaryKey() {
        return Keys.KEY_REPAIR_PRIMARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<REPAIRRECORD>> getKeys() {
        return Arrays.<UniqueKey<REPAIRRECORD>>asList(Keys.KEY_REPAIR_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ForeignKey<REPAIRRECORD, ?>> getReferences() {
        return Arrays.<ForeignKey<REPAIRRECORD, ?>>asList(Keys.ERIDREPAIR, Keys.PNUMREPAIR, Keys.ORDNUMREPAIR);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public REPAIR as(String alias) {
        return new REPAIR(alias, this);
    }

    /**
     * Rename this table
     */
    public REPAIR rename(String name) {
        return new REPAIR(name, null);
    }
}