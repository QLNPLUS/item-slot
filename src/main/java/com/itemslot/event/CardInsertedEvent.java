package com.itemslot.event;

import com.itemslot.inventory.CardInventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Event;

/**
 * Fired on {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS} after a card is
 * inserted into a weapon's card slot. KubeJS scripts can listen via
 * {@code NativeEvents.onEvent()} to apply NBT effects based on the card ID.
 */
public class CardInsertedEvent extends Event {

    private final ItemStack weaponStack;
    private final ItemStack cardStack;
    private final int slot;
    private final CardInventory inventory;

    public CardInsertedEvent(ItemStack weaponStack, ItemStack cardStack, int slot, CardInventory inventory) {
        this.weaponStack = weaponStack;
        this.cardStack = cardStack;
        this.slot = slot;
        this.inventory = inventory;
    }

    /** The weapon/armor/tool receiving the card. Modifiable via NBT. */
    public ItemStack getWeaponStack() {
        return weaponStack;
    }

    /** The card being inserted. */
    public ItemStack getCardStack() {
        return cardStack;
    }

    /** The 0-based slot index. */
    public int getSlot() {
        return slot;
    }

    /** The full card inventory, for querying other installed cards. */
    public CardInventory getInventory() {
        return inventory;
    }
}
