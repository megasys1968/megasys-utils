package quo.vadis.megasys.utils.jooq

import com.google.common.base.CaseFormat
import cz.jirutka.rsql.parser.RSQLParser
import cz.jirutka.rsql.parser.ast.*
import org.apache.commons.lang3.StringUtils
import org.jooq.Condition
import org.jooq.Field
import org.jooq.Record
import org.jooq.Table
import java.lang.reflect.Method

val RSQL_PARSER = RSQLParser()

fun <T : Table<R>, R : Record> rsqlQueryToJooqCondition(rsql: String?, tableSpec: T): Condition? {
  return rsql?.let {
    if (StringUtils.isNoneBlank(it)) {
      RSQL_PARSER.parse(it).accept(JooqRsqlVisitor, tableSpec)
    } else null
  }
}

internal object JooqRsqlVisitor : RSQLVisitor<Condition, Table<out Record>> {

  override fun visit(node: AndNode, tableSpec: Table<out Record>): Condition? {
    return JooqRsqlSpecBuilder.handleLogicalNode(node, tableSpec)
  }

  override fun visit(node: OrNode, tableSpec: Table<out Record>): Condition? {
    return JooqRsqlSpecBuilder.handleLogicalNode(node, tableSpec)
  }

  override fun visit(node: ComparisonNode, tableSpec: Table<out Record>): Condition? {
    return JooqRsqlSpecBuilder.handleComparisonNode(node, tableSpec)
  }
}

internal object JooqRsqlSpecBuilder {
  val equal: Method = targetMethod("equal")
  val notEqual: Method = targetMethod("notEqual")
  val greaterThan: Method = targetMethod("greaterThan")
  val greaterOrEqual: Method = targetMethod("greaterOrEqual")
  val lessThan: Method = targetMethod("lessThan")
  val lessOrEqual: Method = targetMethod("lessOrEqual")

  private fun handleComparisonNode(node: Node, tableSpec: Table<out Record>): Condition? {
    if (node is LogicalNode) {
      return handleLogicalNode(node, tableSpec)
    }
    return if (node is ComparisonNode) {
      handleComparisonNode(node, tableSpec)
    } else null
  }

  fun handleLogicalNode(logicalNode: LogicalNode, tableSpec: Table<out Record>): Condition {
    val specs = logicalNode.children.mapNotNull {
      this.handleComparisonNode(it, tableSpec)
    }.toList()

    var result = specs[0]
    if (logicalNode.operator == LogicalOperator.AND) {
      for (i in 1 until specs.size) {
        result = result.and(specs[i])
      }
    } else if (logicalNode.operator == LogicalOperator.OR) {
      for (i in 1 until specs.size) {
        result = result.or(specs[i])
      }
    }
    return result
  }

  fun handleComparisonNode(comparisonNode: ComparisonNode, tableSpec: Table<out Record>): Condition? {
    val property: String = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, comparisonNode.selector)

    val fieldSpec = tableSpec.fields().first { it.name.equals(property, ignoreCase = true) }
    val operator: ComparisonOperator = comparisonNode.operator
    val arguments: List<Any> = castArgument(comparisonNode.arguments, fieldSpec)

    var argument = arguments.get(0)

    if (StringUtils.equals(operator.symbol, RSQLOperators.EQUAL.symbol)) {


      if (argument is String) {
        if (argument == "null") {
          return fieldSpec.isNull()
        } else {
          argument = argument.toString().replace('*', '%')
          return fieldSpec.like(argument)
        }
      }
      return equal.invoke(fieldSpec, argument) as? Condition
    } else if (StringUtils
        .equals(operator.symbol, RSQLOperators.NOT_EQUAL.symbol)) {

      if (argument is String) {
        if (argument == "null") {
          return fieldSpec.isNotNull()
        } else {
          argument = argument.toString().replace('*', '%')
          return fieldSpec.notLike(argument)
        }
      }
      return notEqual.invoke(fieldSpec, argument) as? Condition
    } else if (StringUtils
        .equals(operator.symbol, RSQLOperators.GREATER_THAN.symbol)) {
      return greaterThan.invoke(fieldSpec, argument) as? Condition
    } else if (StringUtils
        .equals(operator.symbol, RSQLOperators.GREATER_THAN_OR_EQUAL.symbol)) {
      return greaterOrEqual.invoke(fieldSpec, argument) as? Condition
    } else if (StringUtils
        .equals(operator.symbol, RSQLOperators.LESS_THAN.symbol)) {
      return lessThan.invoke(fieldSpec, argument) as? Condition
    } else if (StringUtils
        .equals(operator.symbol, RSQLOperators.LESS_THAN_OR_EQUAL.symbol)) {
      return lessOrEqual.invoke(fieldSpec, argument) as? Condition
    } else if (StringUtils.equals(operator.symbol, RSQLOperators.IN.symbol)) {
      return fieldSpec.`in`(arguments)
    } else if (StringUtils.equals(operator.symbol, RSQLOperators.NOT_IN.symbol)) {
      return fieldSpec.notIn(arguments)
    }
    return null
  }

  private fun targetMethod(methodName: String): Method {
    val method = Field::class.java.methods.filter { it.name == methodName }
      .filter {
        it.parameterTypes[0].name == "java.lang.Object"
      }
      .first()
    method.trySetAccessible()
    return method
  }

  private fun castArgument(arguments: List<String>?, fieldSpec: Field<*>): List<Any> {
    return arguments?.map {
      if (it != "null")
        when (fieldSpec.type) {
          Int::class.javaObjectType -> it.toInt()
          Long::class.javaObjectType -> it.toLong()
          Boolean::class.javaObjectType -> it.toBoolean()
          else -> it
      } else it
    }?.toList() ?: emptyList()
  }
}
