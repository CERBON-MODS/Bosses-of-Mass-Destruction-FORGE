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
import com.cerbon.bosses_of_mass_destruction.structure.BMDStructures;
import com.cerbon.bosses_of_mass_destruction.util.BMDConstants;
import com.cerbon.bosses_of_mass_destruction.util.BMDUtils;
import com.google.common.collect.Lists;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.stats.Stats;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SoulStarItem extends Item {

    public SoulStarItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable World level, List<ITextComponent> tooltipComponents, @Nonnull ITooltipFlag isAdvanced) {
        tooltipComponents.add(new TranslationTextComponent("item.bosses_of_mass_destruction.soul_star.tooltip").withStyle(TextFormatting.DARK_GRAY));
    }

    @Override
    public @Nonnull ActionResultType useOn(ItemUseContext context) {
        World level = context.getLevel();
        BlockPos blockPos = context.getClickedPos();
        BlockState blockState = level.getBlockState(blockPos);

        if (blockState.is(BMDBlocks.CHISELED_STONE_ALTAR.get()) && !blockState.getValue(ChiseledStoneAltarBlock.lit)){
            if (level.isClientSide()){
                clientSoulStartPlace(blockPos);
                return ActionResultType.SUCCESS;
            }else {
                serverSoulStarPlace(blockState, level, blockPos, context);
                level.playSound(
                        null,
                        context.getClickLocation().x,
                        context.getClickLocation().y,
                        context.getClickLocation().z,
                        BMDSounds.SOUL_STAR.get(),
                        SoundCategory.NEUTRAL,
                        0.5f,
                        BMDUtils.randomPitch(level.random)
                );
                return ActionResultType.PASS;
            }
        }
        return ActionResultType.PASS;
    }

    private void serverSoulStarPlace(BlockState blockState, World level, BlockPos blockPos, ItemUseContext context){
        BlockState blockState2 = blockState.setValue(ChiseledStoneAltarBlock.lit, true);
        level.setBlock(blockPos, blockState2, 2);
        context.getItemInHand().shrink(1);
        List<BlockPos> quarterAltarPosition = Lists.newArrayList(new BlockPos(12, 0, 0), new BlockPos(6, 0, 6));
        List<BlockPos> allPotentialAltarPositions = Arrays.stream(Rotation.values()).flatMap(rot -> quarterAltarPosition.stream().map(blockPos1 -> blockPos1.rotate(rot))).collect(Collectors.toList());
        int numberOfAltarsFilled = (int) allPotentialAltarPositions.stream().filter(pos -> {
                    BlockState state = level.getBlockState(blockPos.offset(pos));
                    return state.hasProperty(ChiseledStoneAltarBlock.lit) && state.getValue(ChiseledStoneAltarBlock.lit);
                }).count();

        if (numberOfAltarsFilled == 3){
            EventScheduler eventScheduler = BMDCapabilities.getLevelEventScheduler(level);
            eventScheduler.addEvent(
                    new TimedEvent(
                            () -> {
                                allPotentialAltarPositions.forEach(blockPos1 -> {
                                    if (level.getBlockState(blockPos.offset(blockPos1)).hasProperty(ChiseledStoneAltarBlock.lit))
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
        Vector3d centralPos = VecUtils.asVec3(blockPos).add(new Vector3d(0.5, 1.2, 0.5));
        MathUtils.circleCallback(0.5, 15, VecUtils.yAxis, vec3 -> {
            Vector3d particleVel = VecUtils.yAxis.scale(0.03 + RandomUtils.randomDouble(0.01));
            Vector3d particlePos = centralPos.add(vec3);
            ChiseledStoneAltarBlock.Particles.blueFireParticleFactory.build(particlePos, particleVel);
        });
    }

    @Override
    public @Nonnull ActionResult<ItemStack> use(@Nonnull World level, PlayerEntity player, @Nonnull Hand usedHand) {
        ItemStack itemStack = player.getItemInHand(usedHand);
        BlockRayTraceResult hitResult = getPlayerPOVHitResult(level, player, RayTraceContext.FluidMode.NONE);

        if (hitResult.getType() == RayTraceResult.Type.BLOCK && level.getBlockState(hitResult.getBlockPos()).is(BMDBlocks.CHISELED_STONE_ALTAR.get())){
            return ActionResult.pass(itemStack);

        }else {
            player.startUsingItem(usedHand);
            if (level instanceof ServerWorld) {
                ServerWorld serverLevel = (ServerWorld) level;

                BlockPos blockPos = serverLevel.findNearestMapFeature(BMDStructures.LICH_TOWER_STRUCTURE.get(), player.blockPosition(), 100, false);
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
                            SoundCategory.NEUTRAL,
                            0.5f,
                            0.4f / level.getRandom().nextFloat() * 0.4f + 0.8f
                    );

                    if (!player.abilities.instabuild)
                        itemStack.shrink(1);

                    player.awardStat(Stats.ITEM_USED.get(this));
                    player.swing(usedHand, true);
                    return ActionResult.success(itemStack);
                }
                return ActionResult.pass(itemStack);
            }
            return ActionResult.consume(itemStack);
        }
    }

    public void spawnLich(BlockPos blockPos, World level){
        CompoundNBT compoundTag = new CompoundNBT();
        compoundTag.putString("id", new ResourceLocation(BMDConstants.MOD_ID, "lich").toString());

        Vector3d spawnPos = VecUtils.asVec3(blockPos);
        boolean spawned = new MobPlacementLogic(
                new HorizontalRangedSpawnPosition(spawnPos, 15.0, 30.0, new ModRandom()),
                new CompoundTagEntityProvider(compoundTag, level),
                new MobEntitySpawnPredicate(level),
                new SimpleMobSpawner((ServerWorld) level)
        ).tryPlacement(200);

        if (!spawned){
            LichEntity entity = BMDEntities.LICH.get().create(level);
            if (entity != null){
                Vector3d defaultSpawnPos = spawnPos.add(VecUtils.xAxis.scale(5.0));
                entity.setPacketCoordinates(defaultSpawnPos.x, defaultSpawnPos.y, defaultSpawnPos.z);
                entity.absMoveTo(defaultSpawnPos.x, defaultSpawnPos.y, defaultSpawnPos.z);
                level.addFreshEntity(entity);
            }
        }
    }
}
