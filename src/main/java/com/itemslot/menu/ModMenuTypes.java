package com.itemslot.menu;

import com.itemslot.ItemSlotMod;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModMenuTypes {

    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, ItemSlotMod.MODID);

    public static final RegistryObject<MenuType<ItemSlotMenu>> ITEM_SLOT_MENU =
            MENUS.register("item_slot",
                    () -> IForgeMenuType.create((windowId, inv, buf) -> {
                        int heldSlotIndex = buf.readInt();
                        ItemStack heldItem = buf.readItem();
                        return new ItemSlotMenu(windowId, inv, heldItem, heldSlotIndex);
                    }));

    private ModMenuTypes() {}
}
