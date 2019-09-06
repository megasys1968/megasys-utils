package quo.vadis.megasys.utils.jooq

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonTypeRef
import com.google.common.base.CaseFormat
import org.jooq.*
import org.jooq.impl.DSL
import org.springframework.data.domain.Pageable
import quo.vadis.megasys.utils.DEFAULT_SQL_LIMIT_SIZE
import quo.vadis.megasys.utils.spring.data.limitFrom
import quo.vadis.megasys.utils.spring.data.offsetFrom
import kotlin.contracts.contract

fun <T> ObjectMapper.writeValueAsJson(value: T): JSON {
  return JSON.valueOf(this.writeValueAsString(value))
}

fun <T> ObjectMapper.writeValueAsJsonb(value: T): JSONB {
  return JSONB.valueOf(this.writeValueAsString(value))
}

inline fun <reified T> ObjectMapper.readValue(content: JSON): T = readValue(content.data(), jacksonTypeRef<T>())

inline fun <reified T> ObjectMapper.readValue(content: JSONB): T = readValue(content.data(), jacksonTypeRef<T>())

fun orderFrom(tableSpec: Table<*>, pageable: Pageable = Pageable.unpaged()): List<OrderField<*>> {
  val orders = mutableListOf<OrderField<*>>()
  if (pageable.sort.isSorted) {
    pageable.sort.forEach { order ->
      val columnName = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, order.property)
      val fieldSpec = tableSpec.fields()
        .firstOrNull {
          it.name.equals(columnName, ignoreCase = true)
        }
        ?.let {
          orders.add(if (order.isDescending) it.desc() else it)
        }
    }
  }
  return orders
}

fun <R : Record> SelectWhereStep<R>.whereIf(conditions: Collection<Condition>?): SelectConditionStep<R> {
  return if (!conditions.isNullOrEmpty()) {
    this.where(conditions)
  } else {
    this.where(DSL.noCondition())
  }
}

fun <R : Record> SelectOrderByStep<R>.orderByIf(orders: Collection<OrderField<*>>?): SelectLimitStep<R> {
  return if (!orders.isNullOrEmpty()) {
    this.orderBy(orders)
  } else this
}

fun <R : Record> SelectLimitStep<R>.limitOffsetIf(limit: Int? = null, offset: Int? = null): SelectForUpdateStep<R> {
  return when {
    (null != limit) && (null != offset) -> this.limit(limit).offset(offset)
    (null == limit) && (null != offset) -> this.limit(DEFAULT_SQL_LIMIT_SIZE).offset(offset)
    (null != limit) && (null == offset) -> this.limit(limit)
    else -> this
  }
}

fun <R : Record> SelectOrderByStep<R>.orderByAndLimitOffsetIf(tableSpec: Table<*>, pageable: Pageable = Pageable.unpaged()): SelectForUpdateStep<R> {
  return orderByIf(orderFrom(tableSpec, pageable))
    .limitOffsetIf(limitFrom(pageable), offsetFrom(pageable)?.toInt())
}
