# Item Slot

A lightweight card-slot framework for Minecraft items. Add card slots to eligible weapons, tools, and armor, then manage the installed cards through an in-game GUI.

> **Important:** Item Slot does not add card items or apply card effects by itself. You’ll need registered card items from another mod, plus a compatible mod or script if you want those cards to change gameplay.

## Features

- Press **G** while holding an eligible item to open its card-slot GUI. The key can be changed in **Options → Controls**.
- Supports up to **8 card slots** per item. The default unlocked slot count is **0**; configure it globally or per item, or set the item’s root-level integer NBT tag `slotcount` (which takes priority over the config).
- Accepts items from a configurable item tag. The default is `item_slot:cards`. Add existing card item IDs to this tag with a datapack, or configure Item Slot to use a tag supplied by your card mod.
- Saves installed card item IDs and their item data on the host item.
- Prevents duplicate card IDs on the same item. Optionally define conflict groups in the config as comma-separated item IDs; conflicting cards are highlighted in the GUI.
- Provides Forge events for card insertion, removal, and insertion checks so other mods or scripts can add their own behavior.
- By default, the GUI can be opened for items in `#forge:tools` and `#forge:armors`. The whitelist and blacklist are configurable.

## Getting Started

1. Install a mod that provides the card items you want to use. Item Slot is the slot and storage framework; it does not create cards.
2. Make those existing items valid cards by adding them to `item_slot:cards` with a datapack, or set the `cardTag` config option to a tag those items already belong to.
3. Set the number of unlocked slots with `defaultSlots` or `slotOverrides`, or set `slotcount` on an individual item.
4. Hold the item and press **G**. Insert and remove cards in the GUI. Card effects, if desired, must be provided by other mods or scripts.

## Compatibility

- Minecraft **1.20.1**
- **Minecraft Forge**

---

# 物品插槽（Item Slot）

一个轻量的卡片插槽框架，可为符合条件的武器、工具和盔甲添加卡槽，并通过游戏内界面管理已装入的卡片。

> **注意：** Item Slot 本身不会添加卡片物品，也不会自动赋予卡片任何效果。你需要安装提供卡片物品的其他模组；若希望卡片改变游戏玩法，还需要兼容的模组或脚本实现相应效果。

## 功能特色

- 手持符合条件的物品时按 **G** 打开卡槽界面；按键可在 **选项 → 控制** 中修改。
- 每件物品最多支持 **8 个卡槽**。默认解锁数量为 **0**；可通过配置统一或按物品设置，也可在物品根级 NBT 中设置整数标签 `slotcount`（优先级高于配置）。
- 通过可配置的物品标签识别卡片，默认标签为 `item_slot:cards`。可用数据包将已有卡片物品加入该标签，或将 `cardTag` 配置为卡片模组提供的标签。
- 已装入卡片的物品 ID 和物品数据会保存在承载卡槽的物品上。
- 阻止同一物品中安装重复的卡片 ID；也可在配置中以逗号分隔的物品 ID 设置冲突组，并在界面中高亮冲突卡片。
- 提供卡片装入、取出和装入前检查的 Forge 事件，供其他模组或脚本扩展行为。
- 默认允许对 `#forge:tools` 和 `#forge:armors` 中的物品打开界面；白名单与黑名单均可配置。

## 快速开始

1. 安装提供卡片物品的模组。Item Slot 只提供卡槽与存储框架，不会创建卡片。
2. 使用数据包将已有卡片物品加入 `item_slot:cards`，或将 `cardTag` 配置为这些卡片已有的标签。
3. 通过 `defaultSlots`、`slotOverrides` 配置解锁数量，或在单件物品上设置 `slotcount`。
4. 手持物品并按 **G** 打开界面，随后即可装入或取出卡片。卡片效果需要由其他模组或脚本提供。

## 兼容性

- Minecraft **1.20.1**
- **Minecraft Forge**
