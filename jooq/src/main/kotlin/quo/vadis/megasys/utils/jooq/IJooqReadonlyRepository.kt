package quo.vadis.megasys.utils.jooq

import org.jooq.*
import org.jooq.impl.DSL
import org.springframework.data.domain.Pageable
import quo.vadis.megasys.utils.spring.data.limitFrom
import quo.vadis.megasys.utils.spring.data.offsetFrom

interface IJooqReadonlyRepository<R : TableRecord<R>, T : Table<R>, POJO> {
  fun configuration(): Configuration
  fun getTable(): T
  fun getType(): Class<POJO>

  fun recordFrom(pojo: POJO): R {
    return getTable().newRecord().also { it.from(pojo) }
  }

  fun dslContext() = DSL.using(configuration())

  fun select(conditions: Collection<Condition>? = null, pageable: Pageable = Pageable.unpaged()): List<POJO> {
    return select(conditions, orderFrom(getTable(), pageable), limitFrom(pageable), offsetFrom(pageable)?.toInt())
  }

  fun select(condition: Condition, pageable: Pageable = Pageable.unpaged()): List<POJO> {
    return select(listOf(condition), orderFrom(getTable(), pageable), limitFrom(pageable), offsetFrom(pageable)?.toInt())
  }

  fun select(conditions: Collection<Condition>? = null, orders: Collection<OrderField<*>>? = null, limit: Int? = null, offset: Int? = null): List<POJO> {
    return dslContext().selectFrom(getTable())
      .whereIf(conditions)
      .orderByIf(orders)
      .limitOffsetIf(limit, offset)
      .fetchInto(getType())
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

  fun count(conditions: Collection<Condition>? = null): Long {
    return dslContext().selectCount().from(getTable()).whereIf(conditions).fetchOneInto(Long::class.java)
  }

  fun <T> condition(field: Field<T>, value: T): Condition {
    return if (null == value) field.isNull else field.equal(value)
  }
}
