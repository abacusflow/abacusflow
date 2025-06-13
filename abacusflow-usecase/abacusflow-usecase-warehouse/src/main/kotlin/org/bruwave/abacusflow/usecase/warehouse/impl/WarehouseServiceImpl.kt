package org.bruwave.abacusflow.usecase.warehouse.impl

import org.bruwave.abacusflow.db.WarehouseRepository
import org.bruwave.abacusflow.warehouse.Warehouse
import org.bruwave.abacusflow.usecase.warehouse.WarehouseService
import org.bruwave.abacusflow.usecase.warehouse.WarehouseTO
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class WarehouseServiceImpl(
    private val warehouseRepository: WarehouseRepository,
) : WarehouseService {
    override fun createWarehouse(warehouse: WarehouseTO): WarehouseTO {
        val newWarehouse = Warehouse(name = warehouse.name)
        newWarehouse.updateWarehouseInfo(
            newName = null,
            newLocation = warehouse.location,
            newCapacity = warehouse.capacity
        )
        return warehouseRepository.save(newWarehouse).toWarehouseTO()
    }

    override fun updateWarehouse(warehouseTO: WarehouseTO): WarehouseTO {
        val warehouse = warehouseRepository.findById(warehouseTO.id)
            .orElseThrow { NoSuchElementException("Warehouse not found") }
        warehouse.updateWarehouseInfo(
            newName = warehouseTO.name,
            newLocation = warehouseTO.location,
            newCapacity = warehouseTO.capacity
        )
        return warehouseRepository.save(warehouse).toWarehouseTO()
    }

    override fun deleteWarehouse(warehouseTO: WarehouseTO): WarehouseTO {
        val warehouse = warehouseRepository.findById(warehouseTO.id)
            .orElseThrow { NoSuchElementException("Warehouse not found") }
        warehouseRepository.delete(warehouse)
        return warehouseTO
    }

    override fun getWarehouse(id: Long): WarehouseTO {
        return warehouseRepository.findById(id)
            .orElseThrow { NoSuchElementException("Warehouse not found") }
            .toWarehouseTO()
    }

    override fun getWarehouse(name: String): WarehouseTO {
        return warehouseRepository.findByName(name)
            ?.toWarehouseTO()
            ?: throw NoSuchElementException("Warehouse not found")
    }

    override fun listWarehouses(): List<WarehouseTO> {
        return warehouseRepository.findAll().map { it.toWarehouseTO() }
    }

    private fun Warehouse.toWarehouseTO() = WarehouseTO(
        id = id,
        name = name,
        location = location,
        capacity = capacity,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
} 