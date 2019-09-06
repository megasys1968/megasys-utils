package quo.vadis.megasys.utils

import java.time.*

fun dateTimeToYYYYMM(dateTime: LocalDateTime): Int {
  return dateTime.year * 100 + dateTime.monthValue
}

fun dateToYYYYMMdd(date: LocalDate): Int {
  return date.year * 10000 + date.monthValue * 100 + date.dayOfMonth
}

fun dateTimeToYYYYMMddHHmm(dateTime: LocalDateTime): Long {
  return (dateTime.year.toLong() * 100000000)
    .plus(dateTime.monthValue * 1000000)
    .plus(dateTime.dayOfMonth * 10000)
    .plus(dateTime.hour * 100)
    .plus(dateTime.minute)
}

fun timeToHHmm(time: LocalTime): Int {
  return (time.hour * 100)
    .plus(time.minute)
}

fun yyyyMMddHHmmToHHmm(yyyyMMddHHmm: Long): Int {
  return (yyyyMMddHHmm % 10000).toInt()
}




fun yearMonthToYYYYMM(date: YearMonth): Int {
  return date.year * 100 + date.monthValue
}

fun yearMonthToYYYYMMdd(date: YearMonth, day: Int): Int {
  return yearMonthToYYYYMM(date) * 100 + day
}

fun yyyyMMToYearMonth(yyyyMM: Int): YearMonth {
  return YearMonth.of(yyyyMM / 100, yyyyMM % 100)
}

fun ddHHmmToDuration(ddHHmm: Int): Duration {
  return Duration.ofDays(ddHHmm.toLong() / 10000)
    .plusHours(ddHHmm.toLong() / 100)
    .plusMinutes(ddHHmm.toLong() % 100)
}