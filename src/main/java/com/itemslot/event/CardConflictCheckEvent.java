package com.itemslot.event;

import com.itemslot.inventory.CardInventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

/**
 * Fired on {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS} when a card is about to
 * be inserted into a weapon slot. Cancel this event to prevent the insertion.
 */
@Cancelable
public class CardConflictCheckEvent extends Event {

    private final ItemStack weaponStack;
    private final ItemStack cardStack;
    private final int slot;
    private final CardInventory inventory;

    public CardConflictCheckEvent(ItemStack weaponStack, ItemStack cardStack, int slot, CardInventory inventory) {
        this.weaponStack = weaponStack;
        this.cardStack = cardStack;
        this.slot = slot;
        this.inventory = inventory;
    }

    /** The weapon/armor/tool that would receive the card. */
    public ItemStack getWeaponStack() {
        return weaponStack;
    }

    /** The card being inserted. */
    public ItemStack getCardStack() {
        return cardStack;
    }

    /** The 0-based target slot index. */
    public int getSlot() {
        return slot;
    }

    /** The full card inventory for checking existing cards. */
    public CardInventory getInventory() {
        return inventory;
    }
}
