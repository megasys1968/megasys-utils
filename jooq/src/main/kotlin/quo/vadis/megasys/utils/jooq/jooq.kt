package quo.vadis.megasys.utils.jooq

import org.jooq.*
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort

open class JooqRepository<R: Record>(private val dsl: DSLContext, private val tableSpec: Table<R>) {
  fun truncate() = dsl.truncate(tableSpec)

  fun find(condition: Condition? = null, page: Pageable? = null): List<R> {
    val select = dsl.selectFrom(tableSpec)
    condition?.also {
      select.where(it)
    }

    page?.also {
      if (page.isPaged) {
        select.limit(page.offset.toInt(), page.pageSize)
      }
      if (page.sort.isSorted) {
        val sortFields: List<SortField<*>>  = page.sort.map {
          val propertyName = it.property.toUpperCase()
          val field = tableSpec.field(propertyName)
          field.sort(if (it.direction == Sort.Direction.ASC) { SortOrder.ASC } else { SortOrder.DESC })
        }.toMutableList()
        select.orderBy(sortFields)
      }
    }
    return select.fetchInto(tableSpec.recordType)
  }

  fun insert(record: R): R {
    dsl.insertInto(tableSpec).set(record).execute()
    return record
  }

  fun count(condition: Condition? = null): Int {
    return if (null == condition) {
      dsl.fetchCount(tableSpec)
    } else {
      dsl.fetchCount(tableSpec, condition)
    }
  }
}
