package org.bruwave.abacusflow.usecase.inventory.service.impl

import com.lowagie.text.Document
import com.lowagie.text.Element
import com.lowagie.text.Font
import com.lowagie.text.FontFactory
import com.lowagie.text.PageSize
import com.lowagie.text.Paragraph
import com.lowagie.text.Phrase
import com.lowagie.text.pdf.BaseFont
import com.lowagie.text.pdf.PdfPTable
import com.lowagie.text.pdf.PdfWriter
import org.bruwave.abacusflow.usecase.inventory.InventoryUnitForExportTO
import org.bruwave.abacusflow.usecase.inventory.service.InventoryReportService
import org.bruwave.abacusflow.usecase.inventory.service.InventoryUnitQueryService
import org.springframework.stereotype.Service
import java.io.ByteArrayOutputStream
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.collections.joinToString

@Service
class InventoryReportServiceImpl(
    private val inventoryUnitQueryService: InventoryUnitQueryService
) : InventoryReportService {
    val formatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm:ss")
        .withZone(ZoneId.of("Asia/Shanghai"))

    override fun exportInventoryAsPdf(): ByteArray {
        //TODO 批量查询防止oom
        val units = inventoryUnitQueryService.listInventoryUnitsForExport()
        require(units.isNotEmpty()) { "导出数据为空，无法生成 PDF" }
        return generatePdf(units)
    }

    fun generatePdf(units: List<InventoryUnitForExportTO>): ByteArray {
        val document = Document(PageSize.A4.rotate())
        val outputStream = ByteArrayOutputStream()
        PdfWriter.getInstance(document, outputStream)
        document.open()

//        val font = FontFactory.getFont("Helvetica", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 10f)
        val fontPath = "fonts/SimSun.ttf"
        val baseFont = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED)
        val font = Font(baseFont, 10f)

        val title = Paragraph("库存单元清单 - ${LocalDate.now()}", font).apply {
            alignment = Element.ALIGN_CENTER
            spacingAfter = 10f
        }
        document.add(title)

        val table = PdfPTable(10).apply {
            widthPercentage = 100f
            setWidths(floatArrayOf(5f, 1.5f, 1.0f, 1.0f, 1.0f, 1.0f, 1.5f, 3f, 2.5f, 2.5f))
        }

        val headers = listOf(
            "库存名称", "类型", "状态", "当前数量", "可用数量", "单价(元)",
            "收货时间", "序列号", "批次号", "存储点"
        )

        // 检查表头与表格列数是否匹配
        check(headers.size == table.numberOfColumns) {
            "列数不匹配：headers.size=${headers.size} vs table.columns=${table.numberOfColumns}"
        }

        // 添加表头
        headers.forEach { header ->
            table.addCell(Phrase(header, font))
        }

        // 添加表格数据
        for (unit in units) {
            table.addCell(Phrase(unit.title, font))
            table.addCell(Phrase(mapInventoryUnitTypeToChinese(unit.type), font))
            table.addCell(Phrase(mapInventoryUnitStatusToChinese(unit.status), font))
            table.addCell(Phrase(unit.quantity.toString(), font))
            table.addCell(Phrase(unit.remainingQuantity.toString(), font))
            table.addCell(Phrase(unit.unitPrice.toPlainString(), font))
            table.addCell(Phrase(formatter.format(unit.receivedAt), font))
            table.addCell(Phrase(unit.serialNumber ?: "-", font))
            table.addCell(Phrase(unit.batchCode?.toString() ?: "-", font))
            table.addCell(Phrase(unit.depotName ?: "-", font))
        }

        document.add(table)
        document.close()
        return outputStream.toByteArray()
    }

    private fun mapInventoryUnitStatusToChinese(input: String): String {
        return when (input.uppercase()) {
            "NORMAL" -> "正常"
            "CONSUMED" -> "已消耗"
            "CANCELED" -> "已作废"
            "REVERSED" -> "已撤销"
            else -> "未知"
        }
    }

    private fun mapInventoryUnitTypeToChinese(input: String): String {
        return when (input.uppercase()) {
            "INSTANCE" -> "资产"
            "BATCH" -> "普通商品"
            else -> "未知"
        }
    }
}