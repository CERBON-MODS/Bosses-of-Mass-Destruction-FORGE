package com.cerbon.bosses_of_mass_destruction.item;

import com.cerbon.bosses_of_mass_destruction.util.BMDConstants;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class BMDCreativeModeTabs {
    public static final CreativeModeTab BOSSES_OF_MASS_DESTRUCTION = new CreativeModeTab(BMDConstants.MOD_ID) {

        @Override
        public @NotNull ItemStack makeIcon() {
            return new ItemStack(BMDItems.BLAZING_EYE.get());
        }
    };
}
