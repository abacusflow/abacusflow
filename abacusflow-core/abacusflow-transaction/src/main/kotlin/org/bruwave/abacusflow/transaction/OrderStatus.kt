package org.bruwave.abacusflow.transaction

enum class OrderStatus {
    //进行中
    PENDING,

    // 已完成
    COMPLETED,

    // 已取消
    CANCELED,

    // 已撤回
    REVERSED,
}
