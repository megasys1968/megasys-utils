package quo.vadis.megasys.utils

import java.time.*
import java.time.format.DateTimeFormatter
import kotlin.math.abs

val DATE_FORMATTER_JP = DateTimeFormatter.ofPattern("yyyy/MM/dd")
val DATE_TIME_FORMATTER_JP = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")

fun LocalDateTime.floorSecondAndNano(): LocalDateTime = this.withSecond(0).withNano(0)
fun LocalTime.floorSecondAndNano(): LocalTime = this.withSecond(0).withNano(0)

/******************************************************************************
 * 年月
 *****************************************************************************/
fun nowYYYYMM() = yearMonthToYYYYMM(YearMonth.now())

fun dateTimeToYYYYMM(dateTime: LocalDateTime): Int {
  return dateTime.year * 100 + dateTime.monthValue
}

fun dateToYYYYMM(date: LocalDate): Int {
  return date.year * 100 + date.monthValue
}

fun yearMonthToYYYYMM(year: Int, month: Int): Int {
  return year * 100 + month
}

fun yearMonthToYYYYMM(date: YearMonth): Int {
  return date.year * 100 + date.monthValue
}

fun yyyyMMToYYYYMMdd(yyyyMM: Int, date: Int = 1): Int {
  return yyyyMM * 100 + date
}

fun yyyyMMToYearMonth(yyyyMM: Int): YearMonth {
  return YearMonth.of(yyyyMMToyyyy(yyyyMM), yyyyMMToMM(yyyyMM))
}

fun yyyyMMToyyyy(yyyyMM: Int): Int {
  return yyyyMM / 100
}

fun yyyyMMToMM(yyyyMM: Int): Int {
  return yyyyMM % 100
}

fun plusMonthToYYYYMM(yyyyMM: Int, month: Long): Int {
  return yearMonthToYYYYMM(yyyyMMToYearMonth(yyyyMM).plusMonths(month))
}

fun minusMonthToYYYYMM(yyyyMM: Int, month: Long): Int {
  return yearMonthToYYYYMM(yyyyMMToYearMonth(yyyyMM).minusMonths(month))
}

/**
 * 年度
 */
fun yyyyMMToFiscalYear(yyyyMM: Int): Int {
  return yyyyMMToyyyy(yyyyMM) + when (yyyyMMToMM(yyyyMM)) {
    in 1..3 -> -1
    else -> 0
  }
}

/**
 * 半期
 */
fun yyyyMMToHalfStartYYYYMM(yyyyMM: Int): Int {
  val year = yyyyMMToyyyy(yyyyMM) * 100
  return yyyyMMToMM(yyyyMM)
    .let {
      when(it) {
        in 4..9 -> year + 4
        in 10..12 -> year + 10
        in 1..3 -> (year - 100) + 10
        else -> throw IllegalArgumentException()
      }
    }
}

/**
 * 四半期
 */
fun yyyyMMToQuarterStartYYYYMM(yyyyMM: Int): Int {
  return (yyyyMMToyyyy(yyyyMM) * 100) +
    yyyyMMToMM(yyyyMM)
      .let {
        ((it - 1) / 3 * 3) + 1
      }
}


/******************************************************************************
 * 年月日
 *****************************************************************************/
fun dateToYYYYMMdd(date: LocalDate): Int {
  return date.year * 10000 + date.monthValue * 100 + date.dayOfMonth
}

fun yyyyMMddToDate(yyyyMMdd: Int): LocalDate {
  return LocalDate.of(yyyyMMdd / 10000, yyyyMMdd % 10000 / 100, yyyyMMdd % 100)
}

fun yyyyMMddToYYYYMM(yyyyMMdd: Int): Int {
  return yyyyMMdd / 100
}

fun yearMonthToYYYYMMdd(date: YearMonth, day: Int): Int {
  return yearMonthToYYYYMM(date) * 100 + day
}

fun plusDayToYYYYMMdd(yyyyMMdd: Int, day: Long): Int {
  return dateToYYYYMMdd(yyyyMMddToDate(yyyyMMdd).plusDays(day))
}

fun minusDayToYYYYMMdd(yyyyMMdd: Int, day: Long): Int {
  return dateToYYYYMMdd(yyyyMMddToDate(yyyyMMdd).minusDays(day))
}


/******************************************************************************
 * 年月日時分
 *****************************************************************************/
fun yyyyMMddHHmmToHHmm(yyyyMMddHHmm: Long): Int {
  return (yyyyMMddHHmm % 10000).toInt()
}

fun yyyyMMddHHmmToYYYYMMdd(yyyyMMddHHmm: Long): Int {
  return (yyyyMMddHHmm / 10000).toInt()
}

fun dateTimeToYYYYMMddHHmm(dateTime: LocalDateTime): Long {
  return (dateTime.year.toLong() * 100000000)
    .plus(dateTime.monthValue * 1000000)
    .plus(dateTime.dayOfMonth * 10000)
    .plus(dateTime.hour * 100)
    .plus(dateTime.minute)
}

fun yyyyMMddHHmmToDateTime(yyyyMMddHHmm: Long): LocalDateTime {
  return yyyyMMddToDate(yyyyMMddHHmmToYYYYMMdd(yyyyMMddHHmm))
    .atTime(hhmmToTime(yyyyMMddHHmmToHHmm(yyyyMMddHHmm)))
}

/******************************************************************************
 * 時分
 *****************************************************************************/
fun timeToHHmm(time: LocalTime): Int {
  return (time.hour * 100)
    .plus(time.minute)
}

fun hhmmToTime(hhmm: Int): LocalTime {
  return LocalTime.of(hhmm/ 100, hhmm % 100)
}

/******************************************************************************
 * 日時分
 *****************************************************************************/
fun ddHHmmToDuration(ddHHmm: Int): Duration {
  val ddHHmmL = ddHHmm.toLong()
  return Duration.ofDays(ddHHmmL / 10000)
    .plusHours((ddHHmmL % 10000) / 100)
    .plusMinutes(ddHHmmL % 100)
}