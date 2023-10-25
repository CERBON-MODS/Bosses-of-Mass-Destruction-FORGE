package com.cerbon.bosses_of_mass_destruction.item.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MaterialItem extends Item {

    public MaterialItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag isAdvanced) {
        tooltipComponents.add(Component.translatable("item.bosses_of_mass_destruction.crafting_material.tooltip").withStyle(ChatFormatting.DARK_GRAY));
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
    }
}
