package quo.vadis.megasys.utils.spring.data

import org.springframework.data.domain.Pageable

fun limitFrom(pageable: Pageable): Int? {
  return if (pageable.isPaged) pageable.pageSize else null
}

fun offsetFrom(pageable: Pageable): Long? {
  return if (pageable.isPaged) pageable.offset else null
}