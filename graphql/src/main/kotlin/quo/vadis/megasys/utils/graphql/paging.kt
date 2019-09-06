package quo.vadis.megasys.utils.graphql

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import kotlin.math.ceil

data class Order(
  val direction: Sort.Direction?,
  val property: String
)

data class Page (
  val page: Int?,
  val size: Int?,
  val sort: List<Order>?
)

data class PageInfo(
  var size: Int,
  var totalElements: Int,
  var totalPages: Int,
  var number: Int
)

class UnpagedButSortable(private val sort: Sort) : Pageable by Pageable.unpaged() {
  override fun getSort(): Sort {
    return this.sort
  }
}

fun convertPageable(page: Page?): Pageable? {
  return page?.let { pageable ->

    val sort = pageable.sort?.map {
      Sort.Order(it.direction ?: Sort.Direction.ASC, it.property)
    }?.toList()?.let { Sort.by(it) } ?: Sort.unsorted()

    if ((null == pageable.page) && (null == pageable.size)) {
      UnpagedButSortable(sort)
    } else {
      PageRequest.of(pageable.page ?: 0, pageable.size ?: 20, sort)
    }
  }
}

fun convertPageInfo(page: Pageable?, tocalCount: Int): PageInfo {
  return if (true == page?.isPaged) {
    PageInfo(page.pageSize, tocalCount,
      ceil(tocalCount.toFloat() / page.pageSize).toInt(), page.pageNumber)
  } else {
    PageInfo(tocalCount, tocalCount, 1, 0)
  }
}
