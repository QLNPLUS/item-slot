package com.itemslot;

import com.itemslot.config.ItemSlotConfig;
import com.itemslot.menu.ModMenuTypes;
import com.itemslot.network.PacketHandler;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ItemSlotMod.MODID)
public class ItemSlotMod {

    public static final String MODID = "item_slot";

    public ItemSlotMod() {
        var bus = FMLJavaModLoadingContext.get().getModEventBus();

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ItemSlotConfig.COMMON_SPEC);

        ModMenuTypes.MENUS.register(bus);

        PacketHandler.init();
    }
}
