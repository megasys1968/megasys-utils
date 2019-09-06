package quo.vadis.megasys.utils.jooq

import org.jooq.*

open class JooqRepository<R : UpdatableRecord<R>, T : Table<R>, POJO, KEY>(dslContext: DSLContext,
                                                                      table: T,
                                                                      type: Class<POJO>)
  : JooqReadonlyKeyedRepository<R, T, POJO, KEY>(dslContext, table, type) {

  fun insert(pojo: POJO): Int {
    return dslContext.executeInsert(recordFrom(pojo))
  }

  fun insert(pojos: Collection<POJO>) {
    pojos.forEach { insert(it) }
  }

  fun <C> insertAfterReturning(pojo: POJO, returningField: TableField<R, C>): C {
    val record = recordFrom(pojo)
    val map = mutableMapOf<Field<*>, Any>()
    for (i in 0 until record.size()) {
      if (record.changed(i) && (null != record[i])) {
        map.put(record.field(i), record[i])
      }
    }
    return dslContext.insertInto(table, map.keys).values(map.values).returning(returningField)
      .fetchOne().get(returningField)
  }

  fun update(pojo: POJO): Int {
    return dslContext.executeUpdate(recordFrom(pojo))
  }

  fun update(pojos: Collection<POJO>) {
    pojos.forEach { update(it) }
  }

  fun delete(pojo: POJO): Int {
    return dslContext.executeDelete(recordFrom(pojo))
  }

  fun delete(pojos: Collection<POJO>) {
    pojos.forEach { delete(it) }
  }

  fun deleteById(key: KEY): Int {
    return dslContext.deleteFrom(table).where(equalKey(key)).execute()
  }

  fun deleteByIds(key: Collection<KEY>): Int {
    return dslContext.deleteFrom(table).where(equalKey(key)).execute()
  }

  fun deleteThenReturn(pojo: POJO): POJO {
    val deleteQuery = dslContext.deleteQuery(table)
    val record = recordFrom(pojo)
    table.primaryKey.fieldsArray
      .forEach { key  ->
        deleteQuery.addConditions(
          condition(key as TableField<R, Any>, record.get(key)))
      }
    deleteQuery.setReturning()
    deleteQuery.execute()
    return deleteQuery.returnedRecord.into(type)
  }

  fun truncate(cascade: Boolean = false): Int {
    return dslContext.truncate(table)
      .let {
        if (cascade) it.cascade() else it
      }
      .execute()
  }

  fun save(pojo: POJO): Int {
    val record = recordFrom(pojo)
    return dslContext.insertInto(table).set(record)
      .onConflict(table.primaryKey.fields)
      .doUpdate().set(record).execute()
  }

  fun compositeKeyRecord(vararg values: Any?): KEY {
    val fields = table.primaryKey.fieldsArray as Array<TableField<R, Any>>
    val result: Record = dslContext.newRecord(*fields)
    for (i in values.indices) {
      result.set(fields[i], fields[i].dataType.convert(values[i]))
    }
    return result as KEY
  }
}
