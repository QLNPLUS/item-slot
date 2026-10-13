package com.itemslot.network;

import com.itemslot.ItemSlotMod;
import com.itemslot.config.ItemSlotFilter;
import com.itemslot.util.CardConflictChecker;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public final class PacketHandler {

    private static final String PROTOCOL_VERSION = "1";
    private static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(ItemSlotMod.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int id = 0;

    private PacketHandler() {}

    public static void init() {
        CHANNEL.registerMessage(id++, ServerboundOpenSlotPacket.class,
                (msg, buf) -> {},
                buf -> new ServerboundOpenSlotPacket(),
                ServerboundOpenSlotPacket::handle);

        CHANNEL.registerMessage(id++, ClientboundSyncConflictGroupsPacket.class,
                ClientboundSyncConflictGroupsPacket::encode,
                ClientboundSyncConflictGroupsPacket::decode,
                ClientboundSyncConflictGroupsPacket::handle);

        CHANNEL.registerMessage(id++, ClientboundSyncFilterPacket.class,
                ClientboundSyncFilterPacket::encode,
                ClientboundSyncFilterPacket::decode,
                ClientboundSyncFilterPacket::handle);
    }

    public static void sendToServer(Object msg) {
        CHANNEL.sendToServer(msg);
    }

    /** Sync runtime conflict groups from server to a specific player. */
    public static void syncConflictGroupsTo(ServerPlayer player) {
        CHANNEL.sendTo(
                new ClientboundSyncConflictGroupsPacket(CardConflictChecker.getRuntimeGroups()),
                player.connection.connection,
                NetworkDirection.PLAY_TO_CLIENT
        );
    }

    /** Sync runtime filter entries from server to a specific player. */
    public static void syncFilterTo(ServerPlayer player) {
        CHANNEL.sendTo(
                new ClientboundSyncFilterPacket(ItemSlotFilter.getRuntimeWhitelist(), ItemSlotFilter.getRuntimeBlacklist()),
                player.connection.connection,
                NetworkDirection.PLAY_TO_CLIENT
        );
    }
}
