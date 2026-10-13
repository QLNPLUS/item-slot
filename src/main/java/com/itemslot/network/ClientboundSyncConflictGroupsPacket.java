package com.itemslot.network;

import com.itemslot.util.CardConflictChecker;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public final class ClientboundSyncConflictGroupsPacket {

    private final List<List<String>> groups;

    public ClientboundSyncConflictGroupsPacket(List<List<String>> groups) {
        this.groups = groups;
    }

    public static void encode(ClientboundSyncConflictGroupsPacket msg, FriendlyByteBuf buf) {
        buf.writeVarInt(msg.groups.size());
        for (List<String> group : msg.groups) {
            buf.writeVarInt(group.size());
            for (String id : group) {
                buf.writeUtf(id);
            }
        }
    }

    public static ClientboundSyncConflictGroupsPacket decode(FriendlyByteBuf buf) {
        int groupCount = buf.readVarInt();
        List<List<String>> groups = new ArrayList<>(groupCount);
        for (int i = 0; i < groupCount; i++) {
            int size = buf.readVarInt();
            List<String> group = new ArrayList<>(size);
            for (int j = 0; j < size; j++) {
                group.add(buf.readUtf());
            }
            groups.add(group);
        }
        return new ClientboundSyncConflictGroupsPacket(groups);
    }

    public static void handle(ClientboundSyncConflictGroupsPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> CardConflictChecker.setRuntimeGroups(msg.groups));
        ctx.get().setPacketHandled(true);
    }
}
