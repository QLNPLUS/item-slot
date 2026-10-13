package com.itemslot.network;

import com.itemslot.config.ItemSlotFilter;
import com.itemslot.menu.ItemSlotMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkHooks;

import java.util.function.Supplier;

public final class ServerboundOpenSlotPacket {

    public ServerboundOpenSlotPacket() {}

    public static void handle(ServerboundOpenSlotPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            ItemStack heldItem = player.getMainHandItem();
            if (heldItem.isEmpty()) return;

            if (!ItemSlotFilter.isItemAllowed(heldItem)) return;

            // Sync latest runtime conflict groups and filter entries before opening the GUI
            PacketHandler.syncConflictGroupsTo(player);
            PacketHandler.syncFilterTo(player);

            int heldSlotIndex = player.getInventory().selected;

            MenuProvider provider = new MenuProvider() {
                @Override
                public Component getDisplayName() {
                    return Component.translatable("container.item_slot");
                }

                @Override
                public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player p) {
                    return new ItemSlotMenu(windowId, inv, heldItem, heldSlotIndex);
                }
            };

            NetworkHooks.openScreen(player, provider, buf -> {
                buf.writeInt(heldSlotIndex);
                buf.writeItem(heldItem);
            });
        });
        ctx.get().setPacketHandled(true);
    }
}
