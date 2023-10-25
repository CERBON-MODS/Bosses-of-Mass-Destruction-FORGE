package com.cerbon.bosses_of_mass_destruction.item;

import com.cerbon.bosses_of_mass_destruction.util.BMDConstants;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class BMDCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB,
            BMDConstants.MOD_ID);

    public static RegistryObject<CreativeModeTab> BOSSES_OF_MASS_DESTRUCTION = CREATIVE_MODE_TABS.register("bosses_of_mass_destruction",
            ()-> CreativeModeTab.builder()
                    .icon(()-> new ItemStack(BMDItems.BLAZING_EYE.get()))
                    .title(Component.translatable("itemGroup.bosses_of_mass_destruction.items"))
                    .build());

    public static void register(IEventBus eventBus){
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
