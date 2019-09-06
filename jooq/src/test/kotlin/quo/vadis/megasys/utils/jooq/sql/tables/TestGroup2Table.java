/*
 * This file is generated by jOOQ.
 */
package quo.vadis.megasys.utils.jooq.sql.tables;


import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Index;
import org.jooq.JSONB;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row3;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;

import quo.vadis.megasys.utils.jooq.sql.DefaultSchema;
import quo.vadis.megasys.utils.jooq.sql.Indexes;
import quo.vadis.megasys.utils.jooq.sql.Keys;
import quo.vadis.megasys.utils.jooq.sql.tables.records.TestGroup2Record;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class TestGroup2Table extends TableImpl<TestGroup2Record> {

    private static final long serialVersionUID = -2010500999;

    /**
     * The reference instance of <code>test_group2</code>
     */
    public static final TestGroup2Table TEST_GROUP2 = new TestGroup2Table();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<TestGroup2Record> getRecordType() {
        return TestGroup2Record.class;
    }

    /**
     * The column <code>test_group2.group_id</code>.
     */
    public final TableField<TestGroup2Record, Long> GROUP_ID = createField(DSL.name("group_id"), org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>test_group2.group_name</code>.
     */
    public final TableField<TestGroup2Record, String> GROUP_NAME = createField(DSL.name("group_name"), org.jooq.impl.SQLDataType.VARCHAR(32).nullable(false), this, "");

    /**
     * The column <code>test_group2.payload</code>.
     */
    public final TableField<TestGroup2Record, JSONB> PAYLOAD = createField(DSL.name("payload"), org.jooq.impl.SQLDataType.JSONB, this, "");

    /**
     * Create a <code>test_group2</code> table reference
     */
    public TestGroup2Table() {
        this(DSL.name("test_group2"), null);
    }

    /**
     * Create an aliased <code>test_group2</code> table reference
     */
    public TestGroup2Table(String alias) {
        this(DSL.name(alias), TEST_GROUP2);
    }

    /**
     * Create an aliased <code>test_group2</code> table reference
     */
    public TestGroup2Table(Name alias) {
        this(alias, TEST_GROUP2);
    }

    private TestGroup2Table(Name alias, Table<TestGroup2Record> aliased) {
        this(alias, aliased, null);
    }

    private TestGroup2Table(Name alias, Table<TestGroup2Record> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""));
    }

    public <O extends Record> TestGroup2Table(Table<O> child, ForeignKey<O, TestGroup2Record> key) {
        super(child, key, TEST_GROUP2);
    }

    @Override
    public Schema getSchema() {
        return DefaultSchema.DEFAULT_SCHEMA;
    }

    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.TEST_GROUP2_PKEY);
    }

    @Override
    public UniqueKey<TestGroup2Record> getPrimaryKey() {
        return Keys.TEST_GROUP2_PKEY;
    }

    @Override
    public List<UniqueKey<TestGroup2Record>> getKeys() {
        return Arrays.<UniqueKey<TestGroup2Record>>asList(Keys.TEST_GROUP2_PKEY);
    }

    @Override
    public TestGroup2Table as(String alias) {
        return new TestGroup2Table(DSL.name(alias), this);
    }

    @Override
    public TestGroup2Table as(Name alias) {
        return new TestGroup2Table(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public TestGroup2Table rename(String name) {
        return new TestGroup2Table(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public TestGroup2Table rename(Name name) {
        return new TestGroup2Table(name, null);
    }

    // -------------------------------------------------------------------------
    // Row3 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row3<Long, String, JSONB> fieldsRow() {
        return (Row3) super.fieldsRow();
    }
}