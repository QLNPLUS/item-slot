# Item Slot

A configurable item-slot framework for Minecraft. Add up to **8 NBT-backed slots** to eligible items and manage their contents through an in-game GUI.

> **Item Slot manages slots and item storage; it does not add new items or apply gameplay effects.** Slot contents are not limited to cards: any registered Minecraft item can be used when it matches the configured item tag.

## Features

- Press **G** while holding an eligible host item to open its slots. Change the key in **Options → Controls**.
- Supports up to **8 slots** per host item. The default unlocked count is **0**; configure it globally or per item, or set the host item's root-level integer NBT tag `slotcount` (this overrides the config).
- Choose slot contents with a configurable item tag. The default tag is `item_slot:cards`; despite its name, it can contain any items. Add existing item IDs to the tag with a datapack, or point `cardTag` to a tag already provided by another mod.
- Stores inserted item IDs and their item data on the host item.
- Prevents duplicate item IDs on the same host item. Optionally configure conflict groups as comma-separated item IDs; conflicting items are highlighted in the GUI.
- Provides Forge events for insertion, removal, and insertion checks so other mods or scripts can add behavior.
- The host-item filter is configurable by exact item ID, namespace wildcard, or item tag. By default, items in `#forge:tools` and `#forge:armors` can open the GUI.

## Getting Started

1. Choose any existing registered items to use as slot contents. Add their IDs to `item_slot:cards` with a datapack, or set `cardTag` to a tag those items already belong to.
2. Configure which items can host slots with the whitelist/blacklist settings. The defaults allow Forge tools and armor.
3. Set the unlocked slot count with `defaultSlots` or `slotOverrides`, or set `slotcount` on an individual host item.
4. Hold the host item and press **G**. Inserted items are stored by Item Slot; their gameplay effects, if any, must be supplied by another mod or script.

## Compatibility

- Minecraft **1.20.1**
- **Minecraft Forge**

---

# 物品插槽（Item Slot）

一个可配置的 Minecraft 物品插槽框架。可为符合条件的物品添加最多 **8 个由 NBT 保存的插槽**，并通过游戏内界面管理槽内物品。

> **Item Slot 负责管理插槽和物品存储，不会添加新物品，也不会自动赋予槽内物品任何效果。** 槽内物品不限于卡片：只要符合配置的物品标签，任何已注册的 Minecraft 物品都可以放入。

## 功能特色

- 手持符合条件的宿主物品时按 **G** 打开插槽界面；按键可在 **选项 → 控制** 中修改。
- 每件宿主物品最多支持 **8 个插槽**。默认解锁数量为 **0**；可通过配置统一或按物品设置，也可在宿主物品根级 NBT 中设置整数标签 `slotcount`（优先级高于配置）。
- 通过可配置的物品标签选择可放入插槽的物品。默认标签为 `item_slot:cards`，但标签名称不限制内容类型；可用数据包将已有物品 ID 加入该标签，或将 `cardTag` 指向其他模组提供的标签。
- 槽内物品的 ID 和物品数据会保存在宿主物品上。
- 阻止同一宿主物品中放入重复的物品 ID；也可用逗号分隔的物品 ID 配置冲突组，并在界面中高亮冲突物品。
- 提供物品放入、取出和放入前检查的 Forge 事件，供其他模组或脚本扩展行为。
- 宿主物品筛选支持精确物品 ID、命名空间通配符和物品标签。默认允许 `#forge:tools` 与 `#forge:armors` 中的物品打开界面。

## 快速开始

1. 选择任意已注册物品作为槽内物品。使用数据包将其 ID 加入 `item_slot:cards`，或将 `cardTag` 配置为这些物品已有的标签。
2. 通过白名单/黑名单配置允许承载插槽的物品；默认允许 Forge 工具和盔甲。
3. 通过 `defaultSlots`、`slotOverrides` 配置解锁数量，或在单件宿主物品上设置 `slotcount`。
4. 手持宿主物品并按 **G** 打开界面。Item Slot 负责存储槽内物品；其游戏效果（如有）需要由其他模组或脚本提供。

## 兼容性

- Minecraft **1.20.1**
- **Minecraft Forge**
