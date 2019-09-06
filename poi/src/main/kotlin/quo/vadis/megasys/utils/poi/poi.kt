package quo.vadis.megasys.utils.poi

import org.apache.poi.ss.usermodel.*
import org.apache.poi.ss.util.CellAddress
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.xssf.usermodel.XSSFCell
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator
import org.apache.poi.xssf.usermodel.XSSFSheet
import java.lang.Exception
import java.lang.RuntimeException

fun Sheet.cell(row: Int, col: Int): Cell? {
  if ((row < 0) || (col < 0)) {
    return null
  }
  return this.getRow(row)?.getCell(col)
}

fun Sheet.cellForced(row: Int, col: Int): Cell {
  val excelRow = this.getRow(row)
    ?: run {
      this.createRow(row)
    }
  return excelRow.getCell(col, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
}

fun Sheet.isExistTopLine(row: Int, col: Int): Boolean {
  val borderStyle1 = this.cell(row, col)?.cellStyle?.borderTop ?: BorderStyle.NONE
  val borderStyle2 = this.cell(row - 1, col)?.cellStyle?.borderBottom ?: BorderStyle.NONE
  return (borderStyle1 != BorderStyle.NONE)
    || (borderStyle2 != BorderStyle.NONE)
}

fun Sheet.isExistBottomLine(row: Int, col: Int): Boolean {
  val borderStyle1 = this.cell(row, col)?.cellStyle?.borderBottom ?: BorderStyle.NONE
  val borderStyle2 = this.cell(row + 1, col)?.cellStyle?.borderTop ?: BorderStyle.NONE
  return (borderStyle1 != BorderStyle.NONE)
    || (borderStyle2 != BorderStyle.NONE)
}

fun Sheet.isExistLeftLine(row: Int, col: Int): Boolean {
  val borderStyle1 = this.cell(row, col)?.cellStyle?.borderLeft ?: BorderStyle.NONE
  val borderStyle2 = this.cell(row, col - 1)?.cellStyle?.borderRight ?: BorderStyle.NONE
  return (borderStyle1 != BorderStyle.NONE)
    || (borderStyle2 != BorderStyle.NONE)
}

fun Sheet.isExistRightLine(row: Int, col: Int): Boolean {
  val borderStyle1 = this.cell(row, col)?.cellStyle?.borderRight ?: BorderStyle.NONE
  val borderStyle2 = this.cell(row, col + 1)?.cellStyle?.borderLeft ?: BorderStyle.NONE
  return (borderStyle1 != BorderStyle.NONE)
    || (borderStyle2 != BorderStyle.NONE)
}

/**
 * XSSFSheet用
 */
class FormattedValueException(val row: Int, val col: Int, val reference: String?, throwable: Throwable): RuntimeException(throwable)

fun XSSFSheet.formattedValue(row: Int, col: Int,
                             evaluator: XSSFFormulaEvaluator = XSSFFormulaEvaluator(this.workbook),
                             formatter: DataFormatter = DataFormatter()): String? {
  return this.cell(row, col)?.let {it ->
    try {
      formatter.formatCellValue(it, evaluator)
    } catch (e: Exception) {
      throw FormattedValueException(row, col, CellAddress(it).formatAsString(), e)
    }
  }
}

fun XSSFSheet.findCellRangeAddress(row: Int, col: Int): CellRangeAddress? {
  return mergedRegions
    .firstOrNull { it.isInRange(row, col) }
}
