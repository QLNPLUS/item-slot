package com.itemslot.server;

import com.itemslot.ItemSlotMod;
import com.itemslot.network.PacketHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ItemSlotMod.MODID)
public final class ServerEventHandler {

    private ServerEventHandler() {}

    /** Sync runtime conflict groups and filter entries to the player when they log in. */
    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            PacketHandler.syncConflictGroupsTo(player);
            PacketHandler.syncFilterTo(player);
        }
    }
}
