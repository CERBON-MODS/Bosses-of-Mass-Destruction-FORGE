package com.cerbon.bosses_of_mass_destruction.item.custom;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.EventScheduler;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.TimedEvent;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.random.ModRandom;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.MathUtils;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.RandomUtils;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.VecUtils;
import com.cerbon.bosses_of_mass_destruction.block.BMDBlocks;
import com.cerbon.bosses_of_mass_destruction.block.custom.ChiseledStoneAltarBlock;
import com.cerbon.bosses_of_mass_destruction.capability.util.BMDCapabilities;
import com.cerbon.bosses_of_mass_destruction.entity.BMDEntities;
import com.cerbon.bosses_of_mass_destruction.entity.custom.lich.LichEntity;
import com.cerbon.bosses_of_mass_destruction.entity.spawn.*;
import com.cerbon.bosses_of_mass_destruction.sound.BMDSounds;
import com.cerbon.bosses_of_mass_destruction.util.BMDConstants;
import com.cerbon.bosses_of_mass_destruction.util.BMDStructures;
import com.cerbon.bosses_of_mass_destruction.util.BMDUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class SoulStarItem extends Item {

    public SoulStarItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, @NotNull TooltipFlag isAdvanced) {
        tooltipComponents.add(Component.translatable("item.bosses_of_mass_destruction.soul_star.tooltip").withStyle(ChatFormatting.DARK_GRAY));
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos blockPos = context.getClickedPos();
        BlockState blockState = level.getBlockState(blockPos);

        if (blockState.is(BMDBlocks.CHISELED_STONE_ALTAR.get()) && !blockState.getValue(BlockStateProperties.LIT)){
            if (level.isClientSide()){
                clientSoulStartPlace(blockPos);
                return InteractionResult.SUCCESS;
            }else {
                serverSoulStarPlace(blockState, level, blockPos, context);
                level.playSound(
                        null,
                        context.getClickLocation().x,
                        context.getClickLocation().y,
                        context.getClickLocation().z,
                        BMDSounds.SOUL_STAR.get(),
                        SoundSource.NEUTRAL,
                        0.5f,
                        BMDUtils.randomPitch(level.random)
                );
                return InteractionResult.PASS;
            }
        }
        return InteractionResult.PASS;
    }

    private void serverSoulStarPlace(BlockState blockState, Level level, BlockPos blockPos, UseOnContext context){
        BlockState blockState2 = blockState.setValue(BlockStateProperties.LIT, true);
        level.setBlock(blockPos, blockState2, 2);
        context.getItemInHand().shrink(1);
        List<BlockPos> quarterAltarPosition = List.of(new BlockPos(12, 0, 0), new BlockPos(6, 0, 6));
        List<BlockPos> allPotentialAltarPositions = Arrays.stream(Rotation.values()).flatMap(rot -> quarterAltarPosition.stream().map(blockPos1 -> blockPos1.rotate(rot))).toList();
        int numberOfAltarsFilled = (int) allPotentialAltarPositions.stream().filter(pos -> {
                    BlockState state = level.getBlockState(blockPos.offset(pos));
                    return state.hasProperty(BlockStateProperties.LIT) && state.getValue(BlockStateProperties.LIT);
                }).count();

        if (numberOfAltarsFilled == 3){
            EventScheduler eventScheduler = BMDCapabilities.getLevelEventScheduler(level);
            eventScheduler.addEvent(
                    new TimedEvent(
                            () -> {
                                allPotentialAltarPositions.forEach(blockPos1 -> {
                                    if (level.getBlockState(blockPos.offset(blockPos1)).hasProperty(BlockStateProperties.LIT))
                                        level.destroyBlock(blockPos.offset(blockPos1), false);
                                });

                                level.destroyBlock(blockPos, false);

                                spawnLich(blockPos, level);
                            },
                            20
                    )
            );
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void clientSoulStartPlace(BlockPos blockPos){
        Vec3 centralPos = VecUtils.asVec3(blockPos).add(new Vec3(0.5, 1.2, 0.5));
        MathUtils.circleCallback(0.5, 15, VecUtils.yAxis, vec3 -> {
            Vec3 particleVel = VecUtils.yAxis.multiply(0.03 + RandomUtils.randomDouble(0.01), 0.03 + RandomUtils.randomDouble(0.01), 0.03 + RandomUtils.randomDouble(0.01));
            Vec3 particlePos = centralPos.add(vec3);
            ChiseledStoneAltarBlock.Particles.blueFireParticleFactory.build(particlePos, particleVel);
        });
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand usedHand) {
        ItemStack itemStack = player.getItemInHand(usedHand);
        BlockHitResult hitResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE);

        if (hitResult.getType() == HitResult.Type.BLOCK && level.getBlockState(hitResult.getBlockPos()).is(BMDBlocks.CHISELED_STONE_ALTAR.get())){
            return InteractionResultHolder.pass(itemStack);

        }else {
            player.startUsingItem(usedHand);
            if (level instanceof ServerLevel serverLevel){
                BlockPos blockPos = serverLevel.findNearestMapStructure(BMDStructures.SOUL_STAR_STRUCTURE_KEY, player.blockPosition(), 100, false);
                if (blockPos != null){
                    SoulStarEntity entity = new SoulStarEntity(level, player.getX(), player.getEyeY(), player.getZ());
                    entity.setItem(itemStack);
                    entity.initTargetPos(blockPos);
                    level.addFreshEntity(entity);
                    level.playSound(
                            null,
                            player.getX(),
                            player.getY(),
                            player.getZ(),
                            SoundEvents.ENDER_EYE_LAUNCH,
                            SoundSource.NEUTRAL,
                            0.5f,
                            0.4f / level.getRandom().nextFloat() * 0.4f + 0.8f
                    );

                    if (!player.getAbilities().instabuild)
                        itemStack.shrink(1);

                    player.awardStat(Stats.ITEM_USED.get(this));
                    player.swing(usedHand, true);
                    return InteractionResultHolder.success(itemStack);
                }
                return InteractionResultHolder.pass(itemStack);
            }
            return InteractionResultHolder.consume(itemStack);
        }
    }

    public void spawnLich(BlockPos blockPos, Level level){
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putString("id", new ResourceLocation(BMDConstants.MOD_ID, "lich").toString());

        Vec3 spawnPos = VecUtils.asVec3(blockPos);
        boolean spawned = new MobPlacementLogic(
                new HorizontalRangedSpawnPosition(spawnPos, 15.0, 30.0, new ModRandom()),
                new CompoundTagEntityProvider(compoundTag, level),
                new MobEntitySpawnPredicate(level),
                new SimpleMobSpawner((ServerLevel) level)
        ).tryPlacement(200);

        if (!spawned){
            LichEntity entity = BMDEntities.LICH.get().create(level);
            if (entity != null){
                Vec3 defaultSpawnPos = spawnPos.add(VecUtils.yAxis.multiply(5.0, 5.0, 5.0));
                entity.syncPacketPositionCodec(defaultSpawnPos.x, defaultSpawnPos.y, defaultSpawnPos.z);
                entity.absMoveTo(defaultSpawnPos.x, defaultSpawnPos.y, defaultSpawnPos.z);
                level.addFreshEntity(entity);
            }
        }
    }
}
