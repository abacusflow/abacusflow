package org.abacusflow.product

enum class ProductUnit(
    val displayName: String,
) {
    // 数量单位
    PIECE("件"),
    BOX("箱"),
    PACK("包"),
    DOZEN("打"),
    PAIR("对"),

    // 重量单位
    GRAM("克"),
    KILOGRAM("千克"),

    // 容积单位
    LITER("升"),
    MILLILITER("毫升"),

    // 长度单位
    METER("米"),
    CENTIMETER("厘米"),

    // 其他
    BOTTLE("瓶"),
    BARREL("桶"),
    BAG("袋"),
    SHEET("张"),
    ROLL("卷"),

    // 通用
    ITEM("个"),
}
