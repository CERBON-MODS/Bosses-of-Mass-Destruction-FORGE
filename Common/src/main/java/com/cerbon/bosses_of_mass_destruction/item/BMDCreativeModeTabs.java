package com.cerbon.bosses_of_mass_destruction.item;

import com.cerbon.bosses_of_mass_destruction.util.BMDConstants;
import com.cerbon.cerbons_api.api.registry.RegistryEntry;
import com.cerbon.cerbons_api.api.registry.ResourcefulRegistries;
import com.cerbon.cerbons_api.api.registry.ResourcefulRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class BMDCreativeModeTabs {
    public static final ResourcefulRegistry<CreativeModeTab> CREATIVE_MODE_TABS = ResourcefulRegistries.create(BuiltInRegistries.CREATIVE_MODE_TAB, BMDConstants.MOD_ID);

    public static final RegistryEntry<CreativeModeTab> BOSSES_OF_MASS_DESTRUCTION = CREATIVE_MODE_TABS.register("bosses_of_mass_destruction",
            ()-> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 6)
                    .icon(()-> new ItemStack(BMDItems.BLAZING_EYE.get()))
                    .title(Component.translatable("itemGroup.bosses_of_mass_destruction.items"))
                    .build());

    public static void register() {
        CREATIVE_MODE_TABS.register();
    }
}
