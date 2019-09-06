package quo.vadis.megasys.utils

import com.fasterxml.uuid.Generators
import org.apache.commons.lang3.StringUtils
import java.util.*

/**
 * 空白文字の正規化
 * 全角スペースを含め、前後の空白文字を取り除き、連続する空白文字は空白1文字にする。
 */
fun String.normalizeSpace(): String {
  // 全角スペースを半角に置換
  return this.replace("　", StringUtils.SPACE)
    // 前後の空白文字を取り除く
    .trim()
    // 複数連続のスペースを1
    .replace("""\s+""".toRegex(), StringUtils.SPACE)
}

/**
 * 全ての空白文字の削除
 */
fun String.removeAllSpace(): String {
  return this.replace("""[\s　]+""".toRegex(), StringUtils.EMPTY)
}

fun String.removeZeroPadding(): String {
  return this.toLong().toString()
}




fun <T> TreeSet<T>.find(key: T): T? {
  return firstOrNull { 0 == comparator().compare(it, key) }
}

/**
 * 両方null なら null、それ以外はnullを0として計算
 */
operator fun Int?.plus(value: Int?): Int? =
  if ((null == this) && (null == value)) {
    null
  } else {
    (this ?: 0) + (value ?: 0)
  }

fun String.toNullIfBlank(): String? {
  return if (isBlank()) null else this
}

fun String.toNullIfEmpty(): String? {
  return if (isEmpty()) null else this
}

fun <T> Collection<T>.toNullIfEmpty(): Collection<T>? {
  if (this.isEmpty()) {
    return null
  } else {
    return this
  }
}


fun initialUUID() = UUID(0, 0)

fun generateUUID() = Generators.timeBasedGenerator().generate()
