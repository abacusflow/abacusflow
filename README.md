# AbacusFlow

**AbacusFlow** 是一个采用领域驱动设计清洁架构方案的进销存管理平台，聚焦于模块化、高可维护性、可扩展性。

---

## 📘 平台简介

AbacusFlow 采用了\*\*领域驱动设计（DDD）\*\*的思想，将业务模型划分为产品、库存、交易、合作伙伴、储存点五大核心领域，便于开发者按业务边界开发与维护。

---

## 🗺️ 领域关系示意图

```
[产品领域] ← 包含 → [库存领域]
     ↑                    ↑
     |                    |
[合作伙伴领域] → 供应/购买 → [交易领域]
                             ↓
                      [储存点领域]
```

---

## ✅ 设计原则

* **高内聚**（High Cohesion）：每个领域专注单一职责，封装自身的数据和逻辑。
* **低耦合**（Low Coupling）：各领域仅通过明确定义的接口进行交互，尽可能减少相互依赖。
* **可扩展**（Extensibility）：可以通过扩展相关领域来添加新功能，而无需修改其他领域。
* **易维护**（Maintainability）：问题能够被局限在某一领域内，更容易定位和修复，不会对其他领域造成影响。

---

## 🧩 内置功能模块

AbacusFlow 内置模块涵盖典型的进销存场景：

* **仪表盘（Dashboard）**：提供关键业务指标和系统状态的概览。
* **用户管理（User Management）**：管理系统用户账户和角色，以控制访问权限。
* **库存管理（Inventory Management）**：管理产品库存数量，支持库存调整和库存预警（支持多仓库）。
* **交易管理（Transaction Management）**：处理采购与销售业务流程（采购单和销售单的创建、执行与完成）。
* **产品中心（Product Center）**：管理产品基本信息，包括产品目录、分类和规格。
* **合作伙伴管理（Partner Management）**：管理业务合作伙伴信息，包括客户和供应商，并关联相关交易记录。
* **储存点管理（Storage Point Management）**：管理库存存储地点（仓库/储存点），包括库位分配和容量规划。

## 演示图

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
