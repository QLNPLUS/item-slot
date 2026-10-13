package com.itemslot.client;

import com.itemslot.ItemSlotMod;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = ItemSlotMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientKeybindHandler {

    public static final KeyMapping OPEN_SLOT_KEY = new KeyMapping(
            "key.item_slot.open",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_G,
            "key.item_slot.category"
    );

    private ClientKeybindHandler() {}

    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(OPEN_SLOT_KEY);
    }
}
