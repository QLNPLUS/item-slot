package com.itemslot.inventory;

import com.itemslot.config.ItemSlotConfig;
import com.itemslot.event.CardConflictCheckEvent;
import com.itemslot.event.CardInsertedEvent;
import com.itemslot.event.CardRemovedEvent;
import com.itemslot.util.CardConflictChecker;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public class CardInventory implements Container {

    /** Number of card slots the GUI lays out (the grid is 8 columns wide). */
    public static final int TOTAL_SLOTS = 8;

    /**
     * Root-level NBT tag holding how many slots are unlocked on an item. When present it is
     * authoritative; when absent the configured initial slot count applies (see
     * {@link ItemSlotConfig#resolveInitialSlots}). It lives on the item root, not inside
     * {@link ItemSlotConfig#NBT_KEY}.
     */
    public static final String SLOT_COUNT_TAG = "slotcount";

    private final ItemStack itemStack;
    private final int unlockedSlots;
    private Level level;
    private Runnable onCardChanged;

    public CardInventory(ItemStack stack) {
        this.itemStack = stack;
        // An explicit slotcount tag is the authoritative runtime value; otherwise the configured
        // initial slot count applies. Read-only: never create the root tag here, so opening the
        // GUI leaves clean items untouched.
        CompoundTag tag = stack.getTag();
        int slotCount = tag != null && tag.contains(SLOT_COUNT_TAG, Tag.TAG_INT)
                ? tag.getInt(SLOT_COUNT_TAG)
                : ItemSlotConfig.resolveInitialSlots(stack);
        this.unlockedSlots = Math.min(TOTAL_SLOTS, Math.max(0, slotCount));
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    /** Called after a card is inserted or removed, so the container can sync NBT changes to the client. */
    public void setOnCardChanged(Runnable callback) {
        this.onCardChanged = callback;
    }

    public ItemStack getWeaponStack() {
        return itemStack;
    }

    public int getUnlockedSlots() {
        return unlockedSlots;
    }

    public boolean isSlotUnlocked(int slot) {
        return slot >= 0 && slot < unlockedSlots;
    }

    /**
     * Checks whether a card can be inserted into the given slot.
     * Fires {@link CardConflictCheckEvent} on the server so KJS can cancel conflicts,
     * then falls back to {@link CardConflictChecker} for config-based groups.
     */
    public boolean isCardInsertionAllowed(ItemStack card, int slot) {
        if (level != null && !level.isClientSide()) {
            CardConflictCheckEvent event = new CardConflictCheckEvent(itemStack, card, slot, this);
            MinecraftForge.EVENT_BUS.post(event);
            if (event.isCanceled()) return false;
        }
        return !CardConflictChecker.hasConflictExcept(card, this, slot);
    }

    // ---- Container ----

    @Override
    public int getContainerSize() {
        return TOTAL_SLOTS;
    }

    @Override
    public boolean isEmpty() {
        CompoundTag data = getData();
        for (int i = 0; i < unlockedSlots; i++) {
            if (data.contains(cardKey(i), Tag.TAG_COMPOUND)) return false;
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        if (slot < 0 || slot >= unlockedSlots) return ItemStack.EMPTY;
        CompoundTag data = getData();
        String key = cardKey(slot);
        if (!data.contains(key, Tag.TAG_COMPOUND)) return ItemStack.EMPTY;

        CompoundTag entry = data.getCompound(key);
        String id = entry.getString("id");
        if (id.isEmpty()) return ItemStack.EMPTY;

        ResourceLocation rl = ResourceLocation.tryParse(id);
        if (rl == null) return ItemStack.EMPTY;

        var item = ForgeRegistries.ITEMS.getValue(rl);
        if (item == null) return ItemStack.EMPTY;

        ItemStack card = new ItemStack(item, 1);
        if (entry.contains("nbt", Tag.TAG_COMPOUND)) {
            card.setTag(entry.getCompound("nbt").copy());
        }
        return card;
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        if (amount <= 0 || !isSlotUnlocked(slot)) return ItemStack.EMPTY;
        ItemStack stack = getItem(slot);
        if (stack.isEmpty()) return ItemStack.EMPTY;

        CompoundTag data = getData();
        data.remove(cardKey(slot));
        compact(data);
        fireCardRemoved(slot, stack);
        setChanged();
        return stack;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return removeItem(slot, 1);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (!isSlotUnlocked(slot)) return;
        CompoundTag data = getData();
        if (stack.isEmpty()) {
            ItemStack oldStack = getItem(slot);
            data.remove(cardKey(slot));
            compact(data);
            if (!oldStack.isEmpty()) {
                fireCardRemoved(slot, oldStack);
            }
        } else {
            data.put(cardKey(slot), serializeCard(stack));
            fireCardInserted(slot, stack);
        }
        setChanged();
    }

    @Override
    public void setChanged() {}

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        CompoundTag data = getData();
        for (int i = 0; i < unlockedSlots; i++) {
            data.remove(cardKey(i));
        }
        setChanged();
    }

    // ---- Helpers ----

    private CompoundTag getData() {
        CompoundTag root = itemStack.getOrCreateTag();
        String nbtKey = ItemSlotConfig.NBT_KEY.get();
        if (!root.contains(nbtKey, Tag.TAG_COMPOUND)) {
            root.put(nbtKey, new CompoundTag());
        }
        return root.getCompound(nbtKey);
    }

    private static String cardKey(int slot) {
        int num = slot + 1;
        return num < 10 ? "card_0" + num : "card_" + num;
    }

    private static CompoundTag serializeCard(ItemStack card) {
        CompoundTag entry = new CompoundTag();
        ResourceLocation id = ForgeRegistries.ITEMS.getKey(card.getItem());
        entry.putString("id", id != null ? id.toString() : "minecraft:air");
        if (card.hasTag()) {
            entry.put("nbt", card.getTag().copy());
        }
        return entry;
    }

    private void compact(CompoundTag data) {
        List<CompoundTag> cards = new ArrayList<>();
        for (int i = 0; i < unlockedSlots; i++) {
            String key = cardKey(i);
            if (data.contains(key, Tag.TAG_COMPOUND)) {
                cards.add(data.getCompound(key));
                data.remove(key);
            }
        }
        for (int i = 0; i < cards.size(); i++) {
            data.put(cardKey(i), cards.get(i));
        }
    }

    private void fireCardInserted(int slot, ItemStack cardStack) {
        if (level != null && !level.isClientSide()) {
            MinecraftForge.EVENT_BUS.post(new CardInsertedEvent(itemStack, cardStack, slot, this));
            if (onCardChanged != null) onCardChanged.run();
        }
    }

    private void fireCardRemoved(int slot, ItemStack cardStack) {
        if (level != null && !level.isClientSide()) {
            MinecraftForge.EVENT_BUS.post(new CardRemovedEvent(itemStack, cardStack, slot, this));
            if (onCardChanged != null) onCardChanged.run();
        }
    }
}
