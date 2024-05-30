package com.cerbon.bosses_of_mass_destruction.item.custom;


import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ChargedEnderPearlItem extends Item {

    public ChargedEnderPearlItem(Properties properties) {
        super(properties);
    }

    @Override
    public @Nonnull ActionResult<ItemStack> use(World level, PlayerEntity player, @Nonnull Hand usedHand) {
        ItemStack itemStack = player.getItemInHand(usedHand);
        level.playSound(
                null,
                player.getX(),
                player.getY(),
                player.getZ(),
                SoundEvents.ENDER_PEARL_THROW,
                SoundCategory.NEUTRAL,
                0.5f,
                0.4f / (level.getRandom().nextFloat() * 0.4f + 0.8f)
        );

        int itemCooldown = 180;
        player.getCooldowns().addCooldown(this, itemCooldown);

        if (!level.isClientSide()){
            ProjectileItemEntity projectile = new ChargedEnderPearlEntity(level, player);
            projectile.setItem(itemStack);
            projectile.shootFromRotation(player, player.xRot, player.yRot, 0.0f, 1.5f, 1.0f);
            level.addFreshEntity(projectile);
        }
        player.awardStat(Stats.ITEM_USED.get(this));
        return ActionResult.sidedSuccess(itemStack, level.isClientSide());
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable World level, @Nonnull List<ITextComponent> tooltipComponents, @Nonnull ITooltipFlag isAdvanced) {
        tooltipComponents.add(new TranslationTextComponent("item.bosses_of_mass_destruction.charged_ender_pearl.tooltip").withStyle(TextFormatting.DARK_GRAY));
        tooltipComponents.add(new TranslationTextComponent("item.bosses_of_mass_destruction.charged_ender_pearl.tooltip2").withStyle(TextFormatting.DARK_GRAY));
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
    }
}
