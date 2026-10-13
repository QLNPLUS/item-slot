package com.itemslot.client;

import com.itemslot.ItemSlotMod;
import com.itemslot.menu.ModMenuTypes;
import com.itemslot.screen.ItemSlotScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = ItemSlotMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientScreenRegisterHandler {

    private ClientScreenRegisterHandler() {}

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() ->
                MenuScreens.register(ModMenuTypes.ITEM_SLOT_MENU.get(), ItemSlotScreen::new));
    }
}
