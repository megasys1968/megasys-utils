package quo.vadis.megasys.utils.jooq

import org.jooq.*
import org.jooq.impl.DSL
import org.springframework.data.domain.Pageable
import quo.vadis.megasys.utils.spring.data.limitFrom
import quo.vadis.megasys.utils.spring.data.offsetFrom

open class JooqReadonlyRepository<R : TableRecord<R>, T : Table<R>, POJO>(val dslContext: DSLContext,
                                                                          val table: T,
                                                                          val type: Class<POJO>) {
  fun recordFrom(pojo: POJO): R {
    return table.newRecord().also { it.from(pojo) }
  }

  fun select(conditions: Collection<Condition>? = null, pageable: Pageable = Pageable.unpaged()): List<POJO> {
    return select(conditions, orderFrom(table, pageable), limitFrom(pageable), offsetFrom(pageable)?.toInt())
  }

  fun select(condition: Condition, pageable: Pageable = Pageable.unpaged()): List<POJO> {
    return select(listOf(condition), orderFrom(table, pageable), limitFrom(pageable), offsetFrom(pageable)?.toInt())
  }

  open fun select(conditions: Collection<Condition>? = null, orders: Collection<OrderField<*>>? = null, limit: Int? = null, offset: Int? = null): List<POJO> {
    return dslContext.selectFrom(table)
      .whereIf(conditions)
      .orderByIf(orders)
      .limitOffsetIf(limit, offset)
      .fetchInto(type)
  }

  fun select(condition: Condition, orders: Collection<OrderField<*>>? = null, limit: Int? = null, offset: Int? = null): List<POJO> {
    return select(listOf(condition), orders, limit, offset)
  }

  fun selectOne(conditions: Collection<Condition>? = null): POJO? {
    return select(conditions)
      .also { if (1 < it.size) throw IllegalStateException("複数件のレコードが該当。${it}") }
      .firstOrNull()
  }

  fun selectOne(condition: Condition): POJO? {
    return selectOne(listOf(condition))
  }

  open fun count(conditions: Collection<Condition>? = null): Long {
    return dslContext.selectCount().from(table).whereIf(conditions).fetchOneInto(Long::class.java)
  }

  fun count(condition: Condition): Long {
    return count(listOf(condition))
  }

  fun <Z> fetchRange(field: Field<Z>, lowerInclusive: Z?, upperInclusive: Z?): List<POJO> {
    return dslContext
      .selectFrom(table)
      .where(
        if (lowerInclusive == null) {
          if (upperInclusive == null) {
            DSL.noCondition()
          } else {
            field.le(upperInclusive)
          }
        } else {
          if (upperInclusive == null) {
            field.ge(lowerInclusive)
          } else {
            field.between(lowerInclusive, upperInclusive)
          }
        }
      )
      .fetchInto(type)
  }

  fun <Z> fetch(field: Field<Z>, value: Z): List<POJO> {
    return dslContext
      .selectFrom(table)
      .where(field.equal(value))
      .fetchInto(type)
  }

  fun <Z> fetch(field: Field<Z>, values: Collection<Z>): List<POJO> {
    return dslContext
      .selectFrom(table)
      .where(field.`in`(values))
      .fetchInto(type)
  }

  fun <Z> fetchOne(field: Field<Z>, value: Z): POJO? {
    return dslContext
      .selectFrom(table)
      .where(field.equal(value))
      .fetchOneInto(type)
  }

  protected fun <T> condition(field: Field<T>, value: T): Condition {
    return if (null == value) field.isNull else field.equal(value)
  }
}
