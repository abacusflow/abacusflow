package org.bruwave.abacusflow.transaction

enum class TransactionInventoryUnitType {
    /**
     * 实例类库存单元（Instance）：
     * - 通常对应“资产类”商品（如设备、仪器等）
     * - 每个产品有唯一的序列号（SN）
     * - 采购时逐件入库，销售时一物一码
     * - 库存单元固定 quantity = 1
     */
    INSTANCE,

    /**
     * 批次类库存单元（Batch）：
     * - 通常对应“物料类”商品（如耗材、通用品等）
     * - 不区分个体，通过批次号管理
     * - 库存单元可以表示多个数量（quantity >= 1）
     */
    BATCH,
}
