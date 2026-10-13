package com.itemslot.config;

import com.itemslot.inventory.CardInventory;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeConfigSpec;

public final class ItemSlotConfig {

    public static final ForgeConfigSpec COMMON_SPEC;

    // General
    public static final ForgeConfigSpec.ConfigValue<String> NBT_KEY;
    public static final ForgeConfigSpec.ConfigValue<String> CARD_TAG;

    // Filter
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> WHITELIST_ITEMS;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> BLACKLIST_ITEMS;

    // Conflicts
    public static final ForgeConfigSpec.BooleanValue ENABLE_CONFLICT_CHECK;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> CONFLICT_GROUPS;

    // Slots
    public static final ForgeConfigSpec.IntValue DEFAULT_SLOTS;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> SLOT_OVERRIDES;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.comment("Item Slot Configuration").push("general");

        NBT_KEY = builder
                .comment("NBT key under which card data is stored on an item.",
                        "Changing it hides cards already stored under the old key. Default: \"cardinf\".")
                .define("nbtKey", "cardinf");

        CARD_TAG = builder
                .comment("Item tag identifying what counts as a card. Only items carrying this tag can be",
                        "placed in a slot.",
                        "An empty \"item_slot:cards\" tag ships with the mod: add your card items to it",
                        "from a datapack, or point this at a tag your card mod already provides",
                        "(e.g. \"minecards:cards\").")
                .define("cardTag", "item_slot:cards");

        builder.pop().push("filter");

        WHITELIST_ITEMS = builder
                .comment("Whitelist: only these items can open the card slot GUI.",
                        "If empty, all items are allowed (subject to blacklist).",
                        "Supports: exact ID (\"minecraft:diamond_sword\"),",
                        "  namespace wildcard (\"minecraft:*\"), and tag (\"#forge:armors\").")
                .defineListAllowEmpty("whitelist", List.of(
                        "#forge:armors",
                        "#forge:tools"
                ), s -> s instanceof String str && str.contains(":"));

        BLACKLIST_ITEMS = builder
                .comment("Blacklist: these items can NEVER open the card slot GUI.",
                        "Blacklist takes priority over whitelist.",
                        "Supports the same patterns as whitelist.")
                .defineListAllowEmpty("blacklist", List.of(), s -> s instanceof String str && str.contains(":"));

        builder.pop().push("conflicts");

        ENABLE_CONFLICT_CHECK = builder
                .comment("Enable conflict group checking. Cards in the same conflict group cannot be installed together on the same item.")
                .define("enableConflictCheck", false);

        CONFLICT_GROUPS = builder
                .comment("Conflict groups. Each entry is a comma-separated list of card IDs that conflict with each other.",
                        "Example: \"mymod:card_fire,mymod:card_ice\"")
                .defineListAllowEmpty("groups", List.of(), s -> s instanceof String);

        builder.pop().push("slots");

        DEFAULT_SLOTS = builder
                .comment("Slots unlocked for items that match no entry in slotOverrides. 0 means no slots.",
                        "An item's own \"slotcount\" NBT tag always wins over this and over slotOverrides.")
                .defineInRange("defaultSlots", 0, 0, CardInventory.TOTAL_SLOTS);

        SLOT_OVERRIDES = builder
                .comment("Initial unlocked slots for matching items. Accepts the same patterns as the filter:",
                        "  exact ID           \"minecraft:diamond_sword=4\"",
                        "  namespace wildcard \"somemod:*=6\"",
                        "  item tag           \"#forge:armors=2\"",
                        "The first matching entry wins, so list specific IDs before broad tags.",
                        "Values are clamped to 0.." + CardInventory.TOTAL_SLOTS + ".",
                        "An item's own \"slotcount\" NBT tag always wins over these values, so KJS can still",
                        "raise or lower a single item at runtime.")
                .defineListAllowEmpty("slotOverrides", List.of(), s -> s instanceof String str && str.contains("="));

        builder.pop();

        COMMON_SPEC = builder.build();
    }

    /**
     * Resolves the configured initial slot count for an item: the first matching
     * {@link #SLOT_OVERRIDES} entry, otherwise {@link #DEFAULT_SLOTS}.
     *
     * <p>Only consulted when the item has no {@code slotcount} NBT tag, which takes priority.
     */
    public static int resolveInitialSlots(ItemStack stack) {
        for (String entry : SLOT_OVERRIDES.get()) {
            int separator = entry.lastIndexOf('=');
            if (separator <= 0 || separator == entry.length() - 1) continue;

            int count;
            try {
                count = Integer.parseInt(entry.substring(separator + 1).trim());
            } catch (NumberFormatException malformed) {
                continue;
            }

            if (ItemSlotFilter.matches(stack, entry.substring(0, separator).trim())) {
                return count;
            }
        }
        return DEFAULT_SLOTS.get();
    }

    private ItemSlotConfig() {}
}
