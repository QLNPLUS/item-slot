package com.itemslot.menu;

import com.itemslot.config.ItemSlotConfig;
import com.itemslot.inventory.CardInventory;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class CardSlot extends Slot {

    private final TagKey<Item> cardTag;

    public CardSlot(Container container, int index, int x, int y) {
        super(container, index, x, y);
        this.cardTag = TagKey.create(Registries.ITEM,
                new ResourceLocation(ItemSlotConfig.CARD_TAG.get()));
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        if (!stack.is(cardTag)) return false;
        if (container instanceof CardInventory ci) {
            if (!ci.isSlotUnlocked(this.getContainerSlot())) return false;
            return ci.isCardInsertionAllowed(stack, this.getContainerSlot());
        }
        return true;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
