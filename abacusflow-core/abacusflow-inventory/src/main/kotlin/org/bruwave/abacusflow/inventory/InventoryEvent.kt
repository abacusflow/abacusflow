package org.bruwave.abacusflow.inventory

class InventoryIncreasedEvent(val inventory: Inventory, val amount: Int)
class InventoryDecreasedEvent(val inventory: Inventory, val amount: Int)