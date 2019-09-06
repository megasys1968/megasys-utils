package quo.vadis.megasys.utils.jooq

import org.jooq.*
import org.jooq.impl.DSL

open class JooqReadonlyKeyedRepository<R : TableRecord<R>, T : Table<R>, POJO, KEY>(dslContext: DSLContext,
                                                                               table: T,
                                                                               type: Class<POJO>)
  : JooqReadonlyRepository<R, T, POJO>(dslContext, table, type) {


  fun selectById(id: KEY): POJO? {
    return dslContext.selectFrom(table)
      .where(equalKey(id))
      .fetchOneInto(type)
  }

  protected open fun primaryKeyFields(): List<TableField<R, *>> {
    return table.primaryKey.fields
  }

  protected fun equalKey(id: KEY): Condition {
    val pk = primaryKeyFields()
    return if (pk.size == 1) {
      (pk.first() as Field<KEY>).equal(id)
    } else {
      DSL.row(pk).equal(id as Record)
    }
  }

  protected fun equalKey(ids: Collection<KEY>): Condition {
    val pk = primaryKeyFields()
    return if (pk.size == 1) {
      if (ids.size == 1) {
        equalKey(ids.first())
      } else {
        (pk.first() as Field<KEY>).`in`(pk.first().dataType.convert(ids))
      }
    } else {
      DSL.row(pk).`in`(ids as Collection<RowN>)
    }
  }


}
