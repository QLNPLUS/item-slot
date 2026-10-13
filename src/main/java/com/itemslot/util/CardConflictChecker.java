package com.itemslot.util;

import com.itemslot.config.ItemSlotConfig;
import com.itemslot.inventory.CardInventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public final class CardConflictChecker {

    private CardConflictChecker() {}

    /** Runtime-conflict groups registered by KJS. Synced to client via packet. */
    private static final List<List<String>> runtimeGroups = new CopyOnWriteArrayList<>();

    /** Register a conflict group at runtime (called from KubeJS). Groups with < 2 entries are ignored. */
    public static void registerGroup(List<String> group) {
        if (group.size() >= 2) {
            runtimeGroups.add(List.copyOf(group));
        }
    }

    /** Clear all runtime groups (called on /kubejs reload or server stop). */
    public static void clearRuntimeGroups() {
        runtimeGroups.clear();
    }

    /** Returns a snapshot of current runtime groups (for packet serialization). */
    public static List<List<String>> getRuntimeGroups() {
        return new ArrayList<>(runtimeGroups);
    }

    /** Replace all runtime groups (called on client when receiving sync packet). */
    public static void setRuntimeGroups(List<List<String>> groups) {
        runtimeGroups.clear();
        runtimeGroups.addAll(groups);
    }

    // ---- Conflict checks ----

    public static int findConflictSlot(ItemStack newCard, CardInventory inventory) {
        return findConflictSlotExcept(newCard, inventory, -1);
    }

    public static int findConflictSlotExcept(ItemStack newCard, CardInventory inventory, int exceptSlot) {
        ResourceLocation newCardId = ForgeRegistries.ITEMS.getKey(newCard.getItem());
        if (newCardId == null) return -1;
        String newId = newCardId.toString();

        List<List<String>> groups = getAllGroups();

        for (int i = 0; i < inventory.getContainerSize(); i++) {
            if (i == exceptSlot) continue;
            if (!inventory.isSlotUnlocked(i)) continue;
            ItemStack existing = inventory.getItem(i);
            if (existing.isEmpty()) continue;
            ResourceLocation existId = ForgeRegistries.ITEMS.getKey(existing.getItem());
            if (existId == null) continue;
            String existIdStr = existId.toString();

            if (newId.equals(existIdStr)) return i;

            for (List<String> group : groups) {
                if (group.contains(newId) && group.contains(existIdStr)) {
                    return i;
                }
            }
        }
        return -1;
    }

    public static boolean hasConflict(ItemStack newCard, CardInventory inventory) {
        return findConflictSlot(newCard, inventory) >= 0;
    }

    public static boolean hasConflictExcept(ItemStack newCard, CardInventory inventory, int exceptSlot) {
        return findConflictSlotExcept(newCard, inventory, exceptSlot) >= 0;
    }

    /** Checks two specific cards for conflict (same ID or same group). Used for GUI overlay on both sides. */
    public static boolean areCardsConflicting(ItemStack a, ItemStack b) {
        ResourceLocation idA = ForgeRegistries.ITEMS.getKey(a.getItem());
        ResourceLocation idB = ForgeRegistries.ITEMS.getKey(b.getItem());
        if (idA == null || idB == null) return false;
        String strA = idA.toString();
        String strB = idB.toString();

        if (strA.equals(strB)) return true;

        for (List<String> group : getAllGroups()) {
            if (group.contains(strA) && group.contains(strB)) return true;
        }
        return false;
    }

    /** Returns all conflict groups (config + runtime). */
    private static List<List<String>> getAllGroups() {
        List<List<String>> all = new ArrayList<>(runtimeGroups);
        if (ItemSlotConfig.ENABLE_CONFLICT_CHECK.get()) {
            all.addAll(parseConfigGroups());
        }
        return all;
    }

    private static List<List<String>> parseConfigGroups() {
        List<? extends String> raw = ItemSlotConfig.CONFLICT_GROUPS.get();
        List<List<String>> groups = new ArrayList<>();
        for (String line : raw) {
            List<String> group = new ArrayList<>();
            for (String id : line.split(",")) {
                String trimmed = id.trim();
                if (!trimmed.isEmpty()) group.add(trimmed);
            }
            if (!group.isEmpty()) groups.add(group);
        }
        return groups;
    }
}
