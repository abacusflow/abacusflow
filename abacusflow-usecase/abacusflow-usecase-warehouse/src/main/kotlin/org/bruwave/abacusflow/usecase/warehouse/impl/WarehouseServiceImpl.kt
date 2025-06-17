package org.bruwave.abacusflow.usecase.warehouse.impl

import org.bruwave.abacusflow.db.warehouse.WarehouseRepository
import org.bruwave.abacusflow.usecase.warehouse.BasicWarehouseTO
import org.bruwave.abacusflow.usecase.warehouse.CreateWarehouseInputTO
import org.bruwave.abacusflow.usecase.warehouse.UpdateWarehouseInputTO
import org.bruwave.abacusflow.usecase.warehouse.WarehouseService
import org.bruwave.abacusflow.usecase.warehouse.WarehouseTO
import org.bruwave.abacusflow.usecase.warehouse.toBasicTO
import org.bruwave.abacusflow.usecase.warehouse.toTO
import org.bruwave.abacusflow.warehouse.Warehouse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class WarehouseServiceImpl(
    private val warehouseRepository: WarehouseRepository,
) : WarehouseService {
    override fun createWarehouse(input: CreateWarehouseInputTO): WarehouseTO {
        val newWarehouse =
            Warehouse(
                name = input.name,
                location = input.location,
                capacity = input.capacity,
            )

        return warehouseRepository.save(newWarehouse).toTO()
    }

    override fun updateWarehouse(
        id: Long,
        input: UpdateWarehouseInputTO,
    ): WarehouseTO {
        val warehouse =
            warehouseRepository
                .findById(id)
                .orElseThrow { NoSuchElementException("Warehouse not found with id: $id") }

        warehouse.updateWarehouseInfo(
            newName = input.name,
            newLocation = input.location,
            newCapacity = input.capacity,
        )

        return warehouseRepository.save(warehouse).toTO()
    }

    override fun deleteWarehouse(id: Long): WarehouseTO {
        val warehouse =
            warehouseRepository
                .findById(id)
                .orElseThrow { NoSuchElementException("Warehouse not found with id: $id") }

        warehouseRepository.delete(warehouse)
        return warehouse.toTO()
    }

    override fun getWarehouse(id: Long): WarehouseTO =
        warehouseRepository
            .findById(id)
            .orElseThrow { NoSuchElementException("Warehouse not found with id: $id") }
            .toTO()

    override fun listWarehouses(): List<BasicWarehouseTO> = warehouseRepository.findAll().map { it.toBasicTO() }
}
