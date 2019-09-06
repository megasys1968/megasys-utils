package quo.vadis.megasys.utils

import java.util.*

fun <T> TreeSet<T>.find(key: T): T? {
  return firstOrNull { 0 == comparator().compare(it, key) }
}
