package com.itemslot.network;

import com.itemslot.config.ItemSlotFilter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public final class ClientboundSyncFilterPacket {

    private final List<String> whitelist;
    private final List<String> blacklist;

    public ClientboundSyncFilterPacket(List<String> whitelist, List<String> blacklist) {
        this.whitelist = whitelist;
        this.blacklist = blacklist;
    }

    public static void encode(ClientboundSyncFilterPacket msg, FriendlyByteBuf buf) {
        buf.writeVarInt(msg.whitelist.size());
        for (String entry : msg.whitelist) {
            buf.writeUtf(entry);
        }
        buf.writeVarInt(msg.blacklist.size());
        for (String entry : msg.blacklist) {
            buf.writeUtf(entry);
        }
    }

    public static ClientboundSyncFilterPacket decode(FriendlyByteBuf buf) {
        int wlSize = buf.readVarInt();
        List<String> whitelist = new ArrayList<>(wlSize);
        for (int i = 0; i < wlSize; i++) {
            whitelist.add(buf.readUtf());
        }
        int blSize = buf.readVarInt();
        List<String> blacklist = new ArrayList<>(blSize);
        for (int i = 0; i < blSize; i++) {
            blacklist.add(buf.readUtf());
        }
        return new ClientboundSyncFilterPacket(whitelist, blacklist);
    }

    public static void handle(ClientboundSyncFilterPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> ItemSlotFilter.setRuntimeEntries(msg.whitelist, msg.blacklist));
        ctx.get().setPacketHandled(true);
    }
}
