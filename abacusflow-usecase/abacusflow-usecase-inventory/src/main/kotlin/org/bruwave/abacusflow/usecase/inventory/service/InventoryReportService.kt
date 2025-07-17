package org.bruwave.abacusflow.usecase.inventory.service

interface InventoryReportService {
    fun exportInventoryAsPdf(productCategoryId: Long?): ByteArray

    fun exportInventoryAsExcel(productCategoryId: Long?): ByteArray
}
