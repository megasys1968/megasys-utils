package quo.vadis.megasys.utils.graphql

import graphql.schema.Coercing
import graphql.schema.CoercingSerializeException
import graphql.schema.GraphQLScalarType
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.temporal.TemporalAccessor

class GraphQLScalars {
  companion object {
    val DATE_TIME_SCALAR: GraphQLScalarType = GraphQLScalarType.newScalar()
      .name("DateTime")
      .description("A date-time without a time-zone in the ISO-8601")
      .coercing(object : Coercing<LocalDateTime, String> {

        override fun parseValue(input: Any): LocalDateTime {
          if (input is LocalDateTime) {
            return input
          } else if (input is TemporalAccessor) {
            return LocalDateTime.from(input)
          } else if (input is String) {
            try {
              return LocalDateTime.parse(input)
            } catch (e: DateTimeParseException) {
              throw CoercingSerializeException(e)
            }
          }
          throw CoercingSerializeException("Expected something we can convert to '${LocalDateTime::class.qualifiedName}' but was '${input::class.qualifiedName}'")
        }

        override fun parseLiteral(input: Any): LocalDateTime = parseValue(input)

        override fun serialize(dataFetcherResult: Any): String {
          if (dataFetcherResult is TemporalAccessor) {
            return DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(dataFetcherResult)
          } else if (dataFetcherResult is String) {
            try {
              LocalDateTime.parse(dataFetcherResult)
              return dataFetcherResult
            } catch (e: DateTimeParseException) {
              throw CoercingSerializeException(e)
            }
          }
          throw CoercingSerializeException("Expected something we can convert to '${LocalDateTime::class.qualifiedName}' but was '${dataFetcherResult::class.qualifiedName}'")
        }
      })
      .build()


    val TIME_SCALAR: GraphQLScalarType = GraphQLScalarType.newScalar()
      .name("Time")
      .description("A time without a time-zone in the ISO-8601")
      .coercing(object : Coercing<LocalTime, String> {

        override fun parseValue(input: Any): LocalTime {
          if (input is LocalTime) {
            return input
          } else if (input is TemporalAccessor) {
            return LocalTime.from(input)
          } else if (input is String) {
            try {
              return LocalTime.parse(input)
            } catch (e: DateTimeParseException) {
              throw CoercingSerializeException(e)
            }
          }
          throw CoercingSerializeException("Expected something we can convert to '${LocalTime::class.qualifiedName}' but was '${input::class.qualifiedName}'")
        }

        override fun parseLiteral(input: Any): LocalTime = parseValue(input)

        override fun serialize(dataFetcherResult: Any): String {
          if (dataFetcherResult is TemporalAccessor) {
            return DateTimeFormatter.ISO_LOCAL_TIME.format(dataFetcherResult)
          } else if (dataFetcherResult is String) {
            try {
              LocalTime.parse(dataFetcherResult)
              return dataFetcherResult
            } catch (e: DateTimeParseException) {
              throw CoercingSerializeException(e)
            }
          }
          throw CoercingSerializeException("Expected something we can convert to '${LocalTime::class.qualifiedName}' but was '${dataFetcherResult::class.qualifiedName}'")
        }
      })
      .build()


  }

}
