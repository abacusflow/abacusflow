package org.bruwave.abacusflow.usecase.inventory.service

interface InventoryReportService {
    fun exportInventoryAsPdf(): ByteArray
    fun exportInventoryAsExcel(): ByteArray
}
