package com.cerbon.bosses_of_mass_destruction.item.custom;

import com.cerbon.bosses_of_mass_destruction.particle.BMDParticles;
import com.cerbon.bosses_of_mass_destruction.sound.BMDSounds;
import com.cerbon.cerbons_api.api.static_utilities.ParticleUtils;
import com.cerbon.cerbons_api.api.static_utilities.RandomUtils;
import com.cerbon.cerbons_api.api.static_utilities.SoundUtils;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class EarthdiveSpear extends Item {
    private final ItemAttributeModifiers attributeModifiers;

    public EarthdiveSpear(Properties properties) {
        super(properties);


        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();
        builder.add(Attributes.ATTACK_DAMAGE, new AttributeModifier(
                BASE_ATTACK_DAMAGE_ID,
                8.0,
                AttributeModifier.Operation.ADD_VALUE
        ), EquipmentSlotGroup.MAINHAND);
        builder.add(Attributes.ATTACK_SPEED, new AttributeModifier(
                BASE_ATTACK_SPEED_ID,
                -2.9000000953674316,
                AttributeModifier.Operation.ADD_VALUE
        ), EquipmentSlotGroup.MAINHAND);
        this.attributeModifiers = builder.build();
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("item.bosses_of_mass_destruction.earthdive_spear.tooltip").withStyle(ChatFormatting.DARK_GRAY));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    @Override
    public void releaseUsing(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity user, int timeCharged) {
        if (user instanceof Player player)
            if (isCharged(stack, timeCharged))
                if (!level.isClientSide() && level instanceof ServerLevel serverLevel)
                    if (new WallTeleport(serverLevel, player).tryTeleport(player.getLookAngle(), player.getEyePosition()) ||
                        new WallTeleport(serverLevel, player).tryTeleport(player.getLookAngle(), player.getEyePosition().add(0.0, -1.0, 0.0)))
                    {
                        stack.hurtAndBreak(1 , player, EquipmentSlot.MAINHAND);
                        player.awardStat(Stats.ITEM_USED.get(this));
                        serverLevel.playSound(
                                null,
                                player,
                                BMDSounds.EARTHDIVE_SPEAR_THROW.get(),
                                SoundSource.PLAYERS,
                                1.0F,
                                SoundUtils.randomPitch(player.getRandom())
                        );
                    }
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, @NotNull LivingEntity target, @NotNull LivingEntity attacker) {
        stack.hurtAndBreak(1, attacker, EquipmentSlot.MAINHAND);
        return true;
    }

    @Override
    public boolean mineBlock(
            @NotNull ItemStack stack,
            @NotNull Level level,
            BlockState state,
            @NotNull BlockPos pos,
            @NotNull LivingEntity miningEntity
    ) {
        if (state.getDestroySpeed(level, pos) != 0.0){
            stack.hurtAndBreak(2, miningEntity, EquipmentSlot.MAINHAND);
        }
        return true;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player user, @NotNull InteractionHand usedHand) {
        ItemStack itemStack = user.getItemInHand(usedHand);
        if (itemStack.getDamageValue() >= itemStack.getMaxDamage() - 1)
            return InteractionResultHolder.fail(itemStack);
        else {
            user.startUsingItem(usedHand);
            return InteractionResultHolder.consume(itemStack);
        }
    }

    @Override
    public void onUseTick(@NotNull Level level, @NotNull LivingEntity user, @NotNull ItemStack stack, int remainingUseDuration) {
        super.onUseTick(level, user, stack, remainingUseDuration);
        if (level instanceof ServerLevel serverLevel){
            if (isCharged(stack, remainingUseDuration)){
                Consumer<BlockPos> teleportAction = pos -> spawnTeleportParticles(serverLevel, user);
                WallTeleport wallTeleport = new WallTeleport(serverLevel, user);
                if (wallTeleport.tryTeleport(user.getLookAngle(), user.getEyePosition(), teleportAction))
                    wallTeleport.tryTeleport(user.getLookAngle(), user.getEyePosition().add(0.0, -1.0, 0.0), teleportAction);
            }
        }
    }

    private void spawnTeleportParticles(ServerLevel level, LivingEntity user){
        Vec3 pos = user.getEyePosition().add(user.getLookAngle().multiply(0.15, 0.15, 0.15)).add(RandomUtils.randVec());
        Vec3 vel = user.getEyePosition().add(user.getLookAngle().multiply(4.0, 4.0, 4.0)).subtract(pos);
        ParticleUtils.spawnParticle(level, BMDParticles.EARTHDIVE_INDICATOR.get(), pos, vel, 0, 0.07);
    }

    private boolean isCharged(ItemStack stack, int remainingUseTicks){
        return 72000 - remainingUseTicks >= 10;
    }

    @Override
    public @NotNull ItemAttributeModifiers getDefaultAttributeModifiers() {
        return attributeModifiers;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return UseAnim.SPEAR;
    }

    @Override
    public int getEnchantmentValue() {
        return 1;
    }
}

