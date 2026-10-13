package com.itemslot.event;

import com.itemslot.inventory.CardInventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Event;

/**
 * Fired on {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS} after a card is
 * removed from a weapon's card slot. KubeJS scripts can listen via
 * {@code NativeEvents.onEvent()} to clean up NBT effects that were applied on insert.
 */
public class CardRemovedEvent extends Event {

    private final ItemStack weaponStack;
    private final ItemStack cardStack;
    private final int slot;
    private final CardInventory inventory;

    public CardRemovedEvent(ItemStack weaponStack, ItemStack cardStack, int slot, CardInventory inventory) {
        this.weaponStack = weaponStack;
        this.cardStack = cardStack;
        this.slot = slot;
        this.inventory = inventory;
    }

    /** The weapon/armor/tool the card was removed from. Modifiable via NBT. */
    public ItemStack getWeaponStack() {
        return weaponStack;
    }

    /** The card being removed. */
    public ItemStack getCardStack() {
        return cardStack;
    }

    /** The 0-based slot index it was in. */
    public int getSlot() {
        return slot;
    }

    /** The full card inventory, for querying remaining cards. */
    public CardInventory getInventory() {
        return inventory;
    }
}
