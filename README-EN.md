# AbacusFlow

**AbacusFlow** is a domain-driven design (DDD) based Purchase, Sales, and Inventory (PSI) management platform.

---

## 📘 Platform Overview

AbacusFlow applies domain-driven design principles and divides the system into five core domains: Product, Inventory, Transaction, Partner, and Storage.

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
* **Maintainability**: Issues can be isolated within a domain, making them easier to locate and fix without side effects.

---

## 🧩 Built-in Features

AbacusFlow provides a range of built-in modules covering common PSI scenarios:

* **Dashboard**: Provides an at-a-glance overview of key business metrics and system status.
* **User Management**: Manage system user accounts and roles to control access.
* **Inventory Management**: Track product stock levels, adjustments, and alerts across warehouses.
* **Transaction Management**: Handle purchase and sales processes (purchase orders and sales orders) from creation to completion.
* **Product Center**: Manage product information, including product catalog, categories, and specifications.
* **Partner Management**: Manage business partner information, including customers and suppliers, and link them to transaction records.
* **Storage Point Management**: Manage storage locations (warehouses/depots), including location allocation and capacity planning.


## demo image
<table>
    <tr>
        <td><img src="https://private-user-images.githubusercontent.com/198292660/458647189-98272286-b03e-45e3-b957-6adbd8c7ead1.png?jwt=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3NTA4MTcxMDMsIm5iZiI6MTc1MDgxNjgwMywicGF0aCI6Ii8xOTgyOTI2NjAvNDU4NjQ3MTg5LTk4MjcyMjg2LWIwM2UtNDVlMy1iOTU3LTZhZGJkOGM3ZWFkMS5wbmc_WC1BbXotQWxnb3JpdGhtPUFXUzQtSE1BQy1TSEEyNTYmWC1BbXotQ3JlZGVudGlhbD1BS0lBVkNPRFlMU0E1M1BRSzRaQSUyRjIwMjUwNjI1JTJGdXMtZWFzdC0xJTJGczMlMkZhd3M0X3JlcXVlc3QmWC1BbXotRGF0ZT0yMDI1MDYyNVQwMjAwMDNaJlgtQW16LUV4cGlyZXM9MzAwJlgtQW16LVNpZ25hdHVyZT05ZmUyZmJkZTdmMWUzN2U4ZmViNTMzZDRjNjRmZmZiYjkxYWYzM2YxYTcwYmNhN2YwZWY0Y2U4MjBjNTVhOTZmJlgtQW16LVNpZ25lZEhlYWRlcnM9aG9zdCJ9.tKTDMLBBvvXU-Bft1YAqiK-0xuayNFnCJ9l_UOYaTdk"/></td>
        <td><img src="https://private-user-images.githubusercontent.com/198292660/458647191-4e6ecd20-31a9-424d-a075-317da567b2e7.png?jwt=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3NTA4MTcxMDMsIm5iZiI6MTc1MDgxNjgwMywicGF0aCI6Ii8xOTgyOTI2NjAvNDU4NjQ3MTkxLTRlNmVjZDIwLTMxYTktNDI0ZC1hMDc1LTMxN2RhNTY3YjJlNy5wbmc_WC1BbXotQWxnb3JpdGhtPUFXUzQtSE1BQy1TSEEyNTYmWC1BbXotQ3JlZGVudGlhbD1BS0lBVkNPRFlMU0E1M1BRSzRaQSUyRjIwMjUwNjI1JTJGdXMtZWFzdC0xJTJGczMlMkZhd3M0X3JlcXVlc3QmWC1BbXotRGF0ZT0yMDI1MDYyNVQwMjAwMDNaJlgtQW16LUV4cGlyZXM9MzAwJlgtQW16LVNpZ25hdHVyZT0xM2UxNTZiNTI4Mjg1ZGJmZjA0ZTcxN2I1NWJiZmNkOWY1ZWFkZjdkN2Y5ZmFhYTZhNmE2MDgwODRmZjNiMjMzJlgtQW16LVNpZ25lZEhlYWRlcnM9aG9zdCJ9.V1H4YEU51HbNsAqv_F8Ynht831UFgwQyQYUv5I5PEyI"/></td>
    </tr>
    <tr>
        <td><img src="https://private-user-images.githubusercontent.com/198292660/458647188-9122213f-eac5-4271-b5a3-b24bf8b50082.png?jwt=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3NTA4MTcxMDMsIm5iZiI6MTc1MDgxNjgwMywicGF0aCI6Ii8xOTgyOTI2NjAvNDU4NjQ3MTg4LTkxMjIyMTNmLWVhYzUtNDI3MS1iNWEzLWIyNGJmOGI1MDA4Mi5wbmc_WC1BbXotQWxnb3JpdGhtPUFXUzQtSE1BQy1TSEEyNTYmWC1BbXotQ3JlZGVudGlhbD1BS0lBVkNPRFlMU0E1M1BRSzRaQSUyRjIwMjUwNjI1JTJGdXMtZWFzdC0xJTJGczMlMkZhd3M0X3JlcXVlc3QmWC1BbXotRGF0ZT0yMDI1MDYyNVQwMjAwMDNaJlgtQW16LUV4cGlyZXM9MzAwJlgtQW16LVNpZ25hdHVyZT00ZThkNjgxMWNmM2U4MmEzZGFlYmVhMGQzNzk1Nzc2MWRiMDM4NzBlM2E4NTIyMDU4ZjllMWM3MTM5MzhlNzQwJlgtQW16LVNpZ25lZEhlYWRlcnM9aG9zdCJ9.rYwWhL1j2Bld6S1rSnxDaQ3tj-HR0ArN6DlXmSggULU"/></td>
        <td><img src="https://private-user-images.githubusercontent.com/198292660/458647190-36431ed3-fd0a-47ac-9642-2affef0c8a42.png?jwt=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3NTA4MTcxMDMsIm5iZiI6MTc1MDgxNjgwMywicGF0aCI6Ii8xOTgyOTI2NjAvNDU4NjQ3MTkwLTM2NDMxZWQzLWZkMGEtNDdhYy05NjQyLTJhZmZlZjBjOGE0Mi5wbmc_WC1BbXotQWxnb3JpdGhtPUFXUzQtSE1BQy1TSEEyNTYmWC1BbXotQ3JlZGVudGlhbD1BS0lBVkNPRFlMU0E1M1BRSzRaQSUyRjIwMjUwNjI1JTJGdXMtZWFzdC0xJTJGczMlMkZhd3M0X3JlcXVlc3QmWC1BbXotRGF0ZT0yMDI1MDYyNVQwMjAwMDNaJlgtQW16LUV4cGlyZXM9MzAwJlgtQW16LVNpZ25hdHVyZT1mZmZmNGU3OGI3NzgyMWJiOTg2OTRlZTU4Yzg5ZGQ3MzMyZjg0NjEzNmY0MjFjYTZlZjI2MTlkZDBlZThmY2ViJlgtQW16LVNpZ25lZEhlYWRlcnM9aG9zdCJ9.7jOABkaSC9nc2LEEynbpvWh3nlCYRjYkfjBTB-anhjA"/></td>
    </tr>
</table>