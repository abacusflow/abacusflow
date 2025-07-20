# AbacusFlow

**AbacusFlow** is a domain-driven design (DDD) based Purchase, Sales, and Inventory (PSI) management platform.

---

## 📘 Platform Overview

AbacusFlow applies domain-driven design principles and divides the system into five core domains: Product, Inventory,
Transaction, Partner, and Storage.

---

## 🗺️ Domain Relationship Diagram

```
[Product Domain] ← contains → [Inventory Domain]
       ↑                      ↑
       |                      |
[Partner Domain] → supplies/purchases → [Transaction Domain]
                                 ↓
                          [Storage Domain]
```

---

## ✅ Design Principles

* **High Cohesion**: Each domain focuses on a single responsibility, encapsulating its own data and logic.
* **Low Coupling**: Domains interact only through well-defined interfaces, minimizing interdependencies.
* **Extensibility**: New features can be added by extending the relevant domain without modifying others.
* **Maintainability**: Issues can be isolated within a domain, making them easier to locate and fix without side
  effects.

---

## 🧩 Built-in Features

AbacusFlow provides a range of built-in modules covering common PSI scenarios:

* **Dashboard**: Provides an at-a-glance overview of key business metrics and system status.
* **User Management**: Manage system user accounts and roles to control access.
* **Inventory Management**: Track product stock levels, adjustments, and alerts across warehouses.
* **Transaction Management**: Handle purchase and sales processes (purchase orders and sales orders) from creation to
  completion.
* **Product Center**: Manage product information, including product catalog, categories, and specifications.
* **Partner Management**: Manage business partner information, including customers and suppliers, and link them to
  transaction records.
* **Storage Point Management**: Manage storage locations (warehouses/depots), including location allocation and capacity
  planning.

## demo image

<table>
    <tr>
        <td><img src="./static/demo/dashboard.png"/></td>
        <td></td>
    </tr>
        <td><img src="./static/demo/inventory.png"/></td>
        <td><img src="./static/demo/product.png"/></td>
    </tr>
    <tr>
        <td><img src="./static/demo/purchseorder.png"/></td>
        <td><img src="./static/demo/saleorder.png"/></td>
    </tr>
        <td><img src="./static/demo/customer.png"/></td>
        <td><img src="./static/demo/supplier.png"/></td>
    </tr>
</table>