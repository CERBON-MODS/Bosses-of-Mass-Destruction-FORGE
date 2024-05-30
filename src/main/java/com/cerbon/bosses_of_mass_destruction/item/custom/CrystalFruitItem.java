package com.cerbon.bosses_of_mass_destruction.item.custom;


import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class CrystalFruitItem extends Item {

    public CrystalFruitItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable World level, @Nonnull List<ITextComponent> tooltipComponents, @Nonnull ITooltipFlag isAdvanced) {
        tooltipComponents.add(new TranslationTextComponent("item.bosses_of_mass_destruction.crystal_fruit.tooltip").withStyle(TextFormatting.DARK_GRAY));
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
    }
}
