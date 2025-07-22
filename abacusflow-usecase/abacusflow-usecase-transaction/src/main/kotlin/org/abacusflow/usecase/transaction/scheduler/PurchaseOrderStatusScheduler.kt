package org.abacusflow.usecase.transaction.scheduler

import org.abacusflow.db.transaction.PurchaseOrderRepository
import org.abacusflow.transaction.OrderStatus
import org.abacusflow.transaction.PurchaseOrder
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Component
@Transactional
class PurchaseOrderStatusScheduler(
    private val purchaseOrderRepository: PurchaseOrderRepository,
) {
    @Scheduled(cron = "0 0 2 * * ?") // 每天凌晨2点
    fun runAutoComplete() {
        val completed = autoCompleteEligibleOrderStatus()
        println("自动完成订单数：${completed.size}")
    }

    private fun autoCompleteEligibleOrderStatus(): List<PurchaseOrder> {
        val now = LocalDate.now()
        val sevenDaysAgo = now.minusDays(7)

        val candidates =
            purchaseOrderRepository.findByStatusAndOrderDateBefore(
                OrderStatus.PENDING,
                sevenDaysAgo,
            )

        candidates.forEach { it.completeOrder() }

        purchaseOrderRepository.saveAll(candidates)

        return candidates
    }
}
