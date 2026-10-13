package com.itemslot.config;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Matches items against whitelist and blacklist rules.
 * Supports exact ID, namespace wildcard (mod:*), and tag (#mod:tag) patterns.
 *
 * <p>Logic: blacklist is checked first and always denies.
 * If whitelist is non-empty, the item must match whitelist.
 * If whitelist is empty, all items are allowed (unless blacklisted).
 */
public final class ItemSlotFilter {

    private ItemSlotFilter() {}

    /** Runtime whitelist patterns registered by KubeJS. Synced to client via packet. */
    private static final List<String> runtimeWhitelist = new CopyOnWriteArrayList<>();

    /** Runtime blacklist patterns registered by KubeJS. Synced to client via packet. */
    private static final List<String> runtimeBlacklist = new CopyOnWriteArrayList<>();

    // ---- KubeJS API ----

    /** Add a pattern to the runtime whitelist (called from KubeJS). */
    public static void addWhitelist(String pattern) {
        runtimeWhitelist.add(pattern);
    }

    /** Add a pattern to the runtime blacklist (called from KubeJS). */
    public static void addBlacklist(String pattern) {
        runtimeBlacklist.add(pattern);
    }

    /** Clear all runtime whitelist entries (called on /kubejs reload or server stop). */
    public static void clearRuntimeWhitelist() {
        runtimeWhitelist.clear();
    }

    /** Clear all runtime blacklist entries (called on /kubejs reload or server stop). */
    public static void clearRuntimeBlacklist() {
        runtimeBlacklist.clear();
    }

    /** Returns a snapshot of current runtime whitelist (for packet serialization). */
    public static List<String> getRuntimeWhitelist() {
        return List.copyOf(runtimeWhitelist);
    }

    /** Returns a snapshot of current runtime blacklist (for packet serialization). */
    public static List<String> getRuntimeBlacklist() {
        return List.copyOf(runtimeBlacklist);
    }

    /** Replace all runtime entries (called on client when receiving sync packet). */
    public static void setRuntimeEntries(List<String> whitelist, List<String> blacklist) {
        runtimeWhitelist.clear();
        runtimeWhitelist.addAll(whitelist);
        runtimeBlacklist.clear();
        runtimeBlacklist.addAll(blacklist);
    }

    // ---- Filter logic ----

    /**
     * Returns true if the item is allowed to open the card slot GUI.
     * Blacklist takes priority over whitelist.
     */
    public static boolean isItemAllowed(ItemStack stack) {
        if (stack.isEmpty()) return false;

        List<? extends String> configWhitelist = ItemSlotConfig.WHITELIST_ITEMS.get();
        List<? extends String> configBlacklist = ItemSlotConfig.BLACKLIST_ITEMS.get();

        // Check blacklist first — config + runtime
        if (!configBlacklist.isEmpty() || !runtimeBlacklist.isEmpty()) {
            if (configBlacklist.stream().anyMatch(p -> matches(stack, p))) return false;
            if (runtimeBlacklist.stream().anyMatch(p -> matches(stack, p))) return false;
        }

        // If whitelist is configured (config or runtime), item must match
        boolean hasWhitelist = !configWhitelist.isEmpty() || !runtimeWhitelist.isEmpty();
        if (hasWhitelist) {
            if (configWhitelist.stream().anyMatch(p -> matches(stack, p))) return true;
            if (runtimeWhitelist.stream().anyMatch(p -> matches(stack, p))) return true;
            return false;
        }

        // No whitelist configured: allow all
        return true;
    }

    /**
     * Matches an item against a single pattern. Shared by the filter and by
     * {@link ItemSlotConfig#resolveInitialSlots}.
     *
     * <p>Supported forms: exact ID ("minecraft:diamond_sword"), namespace wildcard
     * ("minecraft:*"), and item tag ("#forge:armors"). A malformed pattern simply
     * never matches instead of throwing, so a typo cannot break GUI opening.
     */
    public static boolean matches(ItemStack stack, String pattern) {
        if (pattern == null || pattern.isEmpty()) return false;
        try {
            if (pattern.startsWith("#")) {
                String tagId = pattern.substring(1);
                TagKey<Item> tagKey = TagKey.create(Registries.ITEM, new ResourceLocation(tagId));
                return stack.is(tagKey);
            }

            if (pattern.endsWith(":*")) {
                String namespace = pattern.substring(0, pattern.length() - 2);
                ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(stack.getItem());
                return itemId != null && itemId.getNamespace().equals(namespace);
            }

            ResourceLocation targetId = new ResourceLocation(pattern);
            ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(stack.getItem());
            return targetId.equals(itemId);
        } catch (RuntimeException malformed) {
            return false;
        }
    }
}
