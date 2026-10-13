package com.itemslot.client;

import com.itemslot.ItemSlotMod;
import com.itemslot.config.ItemSlotFilter;
import com.itemslot.network.PacketHandler;
import com.itemslot.network.ServerboundOpenSlotPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ItemSlotMod.MODID, value = Dist.CLIENT)
public final class ClientTickHandler {

    private ClientTickHandler() {}

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.screen != null) return;

        if (ClientKeybindHandler.OPEN_SLOT_KEY.consumeClick()) {
            ItemStack heldItem = mc.player.getMainHandItem();
            if (heldItem.isEmpty()) return;

            if (!ItemSlotFilter.isItemAllowed(heldItem)) return;

            PacketHandler.sendToServer(new ServerboundOpenSlotPacket());
        }
    }
}
