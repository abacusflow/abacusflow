package org.abacusflow.usecase.transaction.scheduler

import org.abacusflow.db.transaction.SaleOrderRepository
import org.abacusflow.transaction.OrderStatus
import org.abacusflow.transaction.SaleOrder
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Component
@Transactional
class SaleOrderStatusScheduler(
    private val saleOrderRepository: SaleOrderRepository,
) {
    @Scheduled(cron = "0 0 2 * * ?") // 每天凌晨2点
    fun runAutoComplete() {
        val completed = autoCompleteEligibleOrderStatus()
        println("自动完成订单数：${completed.size}")
    }

    private fun autoCompleteEligibleOrderStatus(): List<SaleOrder> {
        val now = LocalDate.now()
        val sevenDaysAgo = now.minusDays(7)

        val candidates =
            saleOrderRepository.findByStatusAndOrderDateBefore(
                OrderStatus.PENDING,
                sevenDaysAgo,
            )

        candidates.forEach { it.completeOrder() }

        saleOrderRepository.saveAll(candidates)

        return candidates
    }
}
