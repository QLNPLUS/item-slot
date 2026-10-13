package com.itemslot.screen;

import com.itemslot.ItemSlotMod;
import com.itemslot.inventory.CardInventory;
import com.itemslot.menu.ItemSlotMenu;
import com.itemslot.util.CardConflictChecker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ItemSlotScreen extends AbstractContainerScreen<ItemSlotMenu> {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation(ItemSlotMod.MODID, "textures/gui/item_slot.png");

    private static final ResourceLocation LOCK_OVERLAY =
            new ResourceLocation(ItemSlotMod.MODID, "textures/gui/slot_locked.png");

    private static final ResourceLocation CONFLICT_OVERLAY =
            new ResourceLocation(ItemSlotMod.MODID, "textures/gui/slot_conflict.png");

    private static final int LABEL_COLOR = 0xFFFFFF;

    private final int cardRows;

    public ItemSlotScreen(ItemSlotMenu menu, Inventory playerInv, Component title) {
        super(menu, playerInv, title);
        int cardCount = menu.getCardSlotCount();
        this.cardRows = Math.max(1, (cardCount + 7) / 8);
        this.imageWidth = 176;
        this.imageHeight = 44 + cardRows * 18 + 14 + 54 + 4 + 18 + 7;
        this.inventoryLabelY = 44 + cardRows * 18 + 4;
        this.titleLabelY = -1000;
    }

    public CardInventory getCardInventory() {
        return menu.getCardInventory();
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;

        graphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight, 256, 256);

        CardInventory inv = menu.getCardInventory();
        // Read weapon from the synced container slot so KJS NBT changes show in real time
        int weaponSlot = menu.getCardSlotCount() + 27 + menu.getHeldSlotIndex();
        ItemStack weapon = weaponSlot < menu.slots.size() ? menu.slots.get(weaponSlot).getItem() : inv.getWeaponStack();
        graphics.renderItem(weapon, x + 8, y + 8);
        graphics.renderItemDecorations(font, weapon, x + 8, y + 8);
        graphics.drawString(font, weapon.getHoverName(), x + 28, y + 12, LABEL_COLOR, true);

        // Render lock overlay on locked slots
        for (Slot slot : this.menu.slots) {
            if (slot instanceof com.itemslot.menu.CardSlot && !inv.isSlotUnlocked(slot.getContainerSlot())) {
                int sx = x + slot.x;
                int sy = y + slot.y;
                graphics.blit(LOCK_OVERLAY, sx, sy, 0, 0, 16, 16, 16, 16);
            }
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 0x404040, false);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // Dim the world background like vanilla inventory
        if (this.minecraft != null && this.minecraft.level != null) {
            graphics.fillGradient(0, 0, this.width, this.height, -1072689136, -804253680);
        }
        super.render(graphics, mouseX, mouseY, partialTick);

        CardInventory inv = menu.getCardInventory();
        ItemStack carried = this.menu.getCarried();

        // Render conflict overlay on slots conflicting with carried card
        if (!carried.isEmpty()) {
            for (int i = 0; i < inv.getContainerSize(); i++) {
                if (!inv.isSlotUnlocked(i)) continue;
                ItemStack existing = inv.getItem(i);
                if (existing.isEmpty()) continue;
                if (CardConflictChecker.areCardsConflicting(carried, existing)) {
                    Slot slot = this.menu.slots.get(i);
                    int sx = this.leftPos + slot.x;
                    int sy = this.topPos + slot.y;
                    graphics.pose().pushPose();
                    graphics.pose().translate(0, 0, 300);
                    graphics.blit(CONFLICT_OVERLAY, sx, sy, 0, 0, 16, 16, 16, 16);
                    graphics.pose().popPose();
                }
            }
        }

        // Render tooltips
        if (carried.isEmpty()) {
            // Slots
            for (Slot slot : this.menu.slots) {
                if (slot.isActive() && slot.hasItem()) {
                    int sx = this.leftPos + slot.x;
                    int sy = this.topPos + slot.y;
                    if (mouseX >= sx - 1 && mouseX < sx + 17 && mouseY >= sy - 1 && mouseY < sy + 17) {
                        graphics.renderTooltip(this.font, slot.getItem(), mouseX, mouseY);
                        break;
                    }
                }
            }
            // Weapon icon — read from synced slot so KJS NBT changes show immediately
            if (mouseX >= this.leftPos + 8 && mouseX <= this.leftPos + 24
                    && mouseY >= this.topPos + 8 && mouseY <= this.topPos + 24) {
                int weaponSlotIdx = menu.getCardSlotCount() + 27 + menu.getHeldSlotIndex();
                ItemStack weapon = weaponSlotIdx < menu.slots.size() ? menu.slots.get(weaponSlotIdx).getItem() : inv.getWeaponStack();
                if (!weapon.isEmpty()) {
                    graphics.renderTooltip(this.font, weapon, mouseX, mouseY);
                }
            }
        }
    }
}
