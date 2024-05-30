package com.cerbon.bosses_of_mass_destruction.item;

import com.cerbon.bosses_of_mass_destruction.util.BMDConstants;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class BMDCreativeModeTabs {
    public static final ItemGroup BOSSES_OF_MASS_DESTRUCTION = new ItemGroup(BMDConstants.MOD_ID) {

        @Override
        public @Nonnull ItemStack makeIcon() {
            return new ItemStack(BMDItems.BLAZING_EYE.get());
        }
    };
}
