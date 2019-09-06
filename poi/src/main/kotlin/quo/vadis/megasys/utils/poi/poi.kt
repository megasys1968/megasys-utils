package quo.vadis.megasys.utils.poi

import org.apache.poi.ss.usermodel.BorderStyle
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.DataFormatter
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator
import org.apache.poi.xssf.usermodel.XSSFSheet

fun Sheet.cell(row: Int, col: Int): Cell? {
  if ((row < 0) || (col < 0)) {
    return null
  }
  return this.getRow(row)?.getCell(col)
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
fun XSSFSheet.formattedValue(row: Int, col: Int): String? {
  return this.cell(row, col)?.let {
    val evaluator = XSSFFormulaEvaluator(this.workbook)
    DataFormatter().formatCellValue(it, evaluator)
  }
}
