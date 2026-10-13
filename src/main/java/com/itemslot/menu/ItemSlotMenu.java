package com.itemslot.menu;

import com.itemslot.inventory.CardInventory;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ItemSlotMenu extends AbstractContainerMenu {

    // Centered: inventory is 9*18=162 wide, cards are 8*18=144 wide → offset by (162-144)/2 = 9
    private static final int CARD_START_X = 17;
    private static final int CARD_START_Y = 44;
    private static final int PLAYER_INV_X = 8;

    private final CardInventory cardInventory;
    private final int heldSlotIndex;
    private final int cardSlotCount;
    private final ItemStack heldItem;

    // Server-side constructor
    public ItemSlotMenu(int windowId, Inventory playerInv, ItemStack heldItem, int heldSlotIndex) {
        super(ModMenuTypes.ITEM_SLOT_MENU.get(), windowId);
        this.heldSlotIndex = heldSlotIndex;
        this.heldItem = heldItem;
        this.cardInventory = new CardInventory(heldItem);
        this.cardInventory.setLevel(playerInv.player.level());
        this.cardSlotCount = cardInventory.getContainerSize();

        buildSlots(playerInv);
        initializeCardSlots();

        // After KJS modifies weapon NBT via CardInsertedEvent/CardRemovedEvent,
        // mark the held item's container slot dirty so it syncs to the client.
        int heldContainerSlot = cardSlotCount + 27 + heldSlotIndex;
        this.cardInventory.setOnCardChanged(() -> {
            if (heldContainerSlot < slots.size()) {
                slots.get(heldContainerSlot).setChanged();
            }
        });
    }

    public CardInventory getCardInventory() {
        return cardInventory;
    }

    public int getCardSlotCount() {
        return cardSlotCount;
    }

    public int getHeldSlotIndex() {
        return heldSlotIndex;
    }

    private void buildSlots(Inventory playerInv) {
        // Card slots
        for (int i = 0; i < cardSlotCount; i++) {
            int row = i / 8;
            int col = i % 8;
            this.addSlot(new CardSlot(cardInventory, i,
                    CARD_START_X + col * 18, CARD_START_Y + row * 18));
        }

        int invY = CARD_START_Y + ((cardSlotCount + 7) / 8) * 18 + 14;

        // Player inventory (3x9)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInv, col + row * 9 + 9,
                        PLAYER_INV_X + col * 18, invY + row * 18));
            }
        }

        // Hotbar (1x9)
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInv, col,
                    PLAYER_INV_X + col * 18, invY + 58));
        }
    }

    private void initializeCardSlots() {
        for (int i = 0; i < cardSlotCount; i++) {
            this.slots.get(i).set(cardInventory.getItem(i));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (!slot.hasItem()) return result;

        ItemStack stackInSlot = slot.getItem();
        result = stackInSlot.copy();

        if (index < cardSlotCount) {
            // Move from card slot to player inventory
            if (!this.moveItemStackTo(stackInSlot, cardSlotCount, this.slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else {
            // Move from player inventory to card slot
            if (!this.moveItemStackTo(stackInSlot, 0, cardSlotCount, false)) {
                return ItemStack.EMPTY;
            }
        }

        if (stackInSlot.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        return result;
    }

    @Override
    public boolean stillValid(Player player) {
        ItemStack held = player.getInventory().getItem(heldSlotIndex);
        return held == heldItem && !held.isEmpty();
    }
}
