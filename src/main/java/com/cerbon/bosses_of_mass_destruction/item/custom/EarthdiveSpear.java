package com.cerbon.bosses_of_mass_destruction.item.custom;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.RandomUtils;
import com.cerbon.bosses_of_mass_destruction.particle.BMDParticles;
import com.cerbon.bosses_of_mass_destruction.sound.BMDSounds;
import com.cerbon.bosses_of_mass_destruction.util.BMDUtils;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public class EarthdiveSpear extends Item {
    private final Multimap<Attribute, AttributeModifier> attributeModifiers;

    public EarthdiveSpear(Properties properties) {
        super(properties);
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(
                BASE_ATTACK_DAMAGE_UUID,
                "Tool modifier",
                8.0,
                AttributeModifier.Operation.ADDITION
        ));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(
                BASE_ATTACK_SPEED_UUID,
                "Tool modifier",
                -2.9000000953674316,
                AttributeModifier.Operation.ADDITION
        ));
        this.attributeModifiers = builder.build();
    }

    @Override
    public void appendHoverText(
            @Nonnull ItemStack stack,
            @Nullable World level,
            List<ITextComponent> tooltipComponents,
            @Nonnull ITooltipFlag isAdvanced
    ) {
        tooltipComponents.add(new TranslationTextComponent("item.bosses_of_mass_destruction.earthdive_spear.tooltip").withStyle(TextFormatting.DARK_GRAY));
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
    }

    @Override
    public void releaseUsing(@Nonnull ItemStack stack, @Nonnull World level, @Nonnull LivingEntity user, int timeCharged) {
        if (user instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) user;

            if (isCharged(stack, timeCharged))
                if (!level.isClientSide() && level instanceof ServerWorld) {
                    ServerWorld serverLevel = (ServerWorld) level;

                    if (new WallTeleport(serverLevel, player).tryTeleport(player.getLookAngle(), player.getEyePosition(1.0f)) ||
                            new WallTeleport(serverLevel, player).tryTeleport(player.getLookAngle(), player.getEyePosition(1.0f).add(0.0, -1.0, 0.0))) {
                        stack.hurtAndBreak(1, player, it -> it.broadcastBreakEvent(EquipmentSlotType.MAINHAND));
                        player.awardStat(Stats.ITEM_USED.get(this));
                        serverLevel.playSound(
                                null,
                                player,
                                BMDSounds.EARTHDIVE_SPEAR_THROW.get(),
                                SoundCategory.PLAYERS,
                                1.0F,
                                BMDUtils.randomPitch(player.getRandom())
                        );
                    }
                }
        }
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, @Nonnull LivingEntity target, @Nonnull LivingEntity attacker) {
        stack.hurtAndBreak(1, attacker, it ->
                it.broadcastBreakEvent(EquipmentSlotType.MAINHAND));
        return true;
    }

    @Override
    public boolean mineBlock(
            @Nonnull ItemStack stack,
            @Nonnull World level,
            BlockState state,
            @Nonnull BlockPos pos,
            @Nonnull LivingEntity miningEntity
    ) {
        if (state.getDestroySpeed(level, pos) != 0.0){
            stack.hurtAndBreak(2, miningEntity,
                    miner -> miner.broadcastBreakEvent(EquipmentSlotType.MAINHAND));
        }
        return true;
    }

    @Override
    public @Nonnull ActionResult<ItemStack> use(@Nonnull World level, PlayerEntity user, @Nonnull Hand usedHand) {
        ItemStack itemStack = user.getItemInHand(usedHand);
        if (itemStack.getDamageValue() >= itemStack.getMaxDamage() - 1)
            return ActionResult.fail(itemStack);
        else {
            user.startUsingItem(usedHand);
            return ActionResult.consume(itemStack);
        }
    }

    @Override
    public void onUseTick(@Nonnull World level, @Nonnull LivingEntity user, @Nonnull ItemStack stack, int remainingUseDuration) {
        super.onUseTick(level, user, stack, remainingUseDuration);
        if (level instanceof ServerWorld) {
            ServerWorld serverLevel = (ServerWorld) level;

            if (isCharged(stack, remainingUseDuration)){
                Consumer<BlockPos> teleportAction = pos -> spawnTeleportParticles(serverLevel, user);
                WallTeleport wallTeleport = new WallTeleport(serverLevel, user);
                if (wallTeleport.tryTeleport(user.getLookAngle(), user.getEyePosition(1.0f), teleportAction))
                    wallTeleport.tryTeleport(user.getLookAngle(), user.getEyePosition(1.0f).add(0.0, -1.0, 0.0), teleportAction);
            }
        }
    }

    private void spawnTeleportParticles(ServerWorld level, LivingEntity user){
        Vector3d pos = user.getEyePosition(1.0f).add(user.getLookAngle().multiply(0.15, 0.15, 0.15)).add(RandomUtils.randVec());
        Vector3d vel = user.getEyePosition(1.0f).add(user.getLookAngle().multiply(4.0, 4.0, 4.0)).subtract(pos);
        BMDUtils.spawnParticle(level, BMDParticles.EARTHDIVE_INDICATOR.get(), pos, vel, 0, 0.07);
    }

    private boolean isCharged(ItemStack stack, int remainingUseTicks){
        return getUseDuration(stack) - remainingUseTicks >= 10;
    }

    @Override
    public @Nonnull Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(@Nonnull EquipmentSlotType slot) {
        return slot == EquipmentSlotType.MAINHAND ? attributeModifiers : super.getDefaultAttributeModifiers(slot);
    }

    @Override
    public int getUseDuration(@Nonnull ItemStack stack) {
        return 72000;
    }

    @Override
    public @Nonnull UseAction getUseAnimation(@Nonnull ItemStack stack) {
        return UseAction.SPEAR;
    }


    @Override
    public int getEnchantmentValue() {
        return 1;
    }
}

