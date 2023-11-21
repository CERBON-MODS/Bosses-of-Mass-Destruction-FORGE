package com.cerbon.bosses_of_mass_destruction.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class VanillaCopiesServer {
    public static DamageSource create(Level level, ResourceKey<DamageType> key, Entity attacker) {
        Holder<DamageType> damageType = level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(key);
        return new DamageSource(damageType, attacker);
    }

    public static void travel(Vec3 relative, LivingEntity entity, float baseFrictionCoefficient) {
        if (entity.isInWater()) {
            entity.moveRelative(0.02F, relative);
            entity.move(MoverType.SELF, entity.getDeltaMovement());
            entity.setDeltaMovement(entity.getDeltaMovement().multiply(0.800000011920929, 0.800000011920929, 0.800000011920929));

        } else if (entity.isInLava()) {
            entity.moveRelative(0.02F, relative);
            entity.move(MoverType.SELF, entity.getDeltaMovement());
            entity.setDeltaMovement(entity.getDeltaMovement().multiply(0.5, 0.5, 0.5));

        } else {
            float friction = entity.onGround() ? entity.level().getBlockState(BlockPos.containing(entity.getX(), entity.getY() - 1.0, entity.getZ())).getBlock()
                    .getFriction() * baseFrictionCoefficient : baseFrictionCoefficient;
            float g = 0.16277137F / (friction * friction * friction);

            entity.moveRelative(entity.onGround() ? 0.1F * g : 0.02F, relative);
            entity.move(MoverType.SELF, entity.getDeltaMovement());
            entity.setDeltaMovement(entity.getDeltaMovement().multiply(friction, friction, friction));
        }
        entity.calculateEntityAnimation(false);
    }

    public static boolean hasDirectLineOfSight(Vec3 to, Vec3 from, BlockGetter level, Entity entity) {
        ClipContext context = new ClipContext(
                to,
                from,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                entity
        );
        return level.clip(context).getType() == HitResult.Type.MISS;
    }

    public static void awardExperience(int amount, Vec3 pos, Level level) {
        int amt = amount;
        while (amt > 0) {
            int i = ExperienceOrb.getExperienceValue(amt);
            amt -= i;
            level.addFreshEntity(new ExperienceOrb(level, pos.x, pos.y, pos.z, i));
        }
    }

    public static int getBlockLight(Entity entity, BlockPos blockPos) {
        if (entity.isOnFire())
            return 15;
        else
            return entity.level().getBrightness(LightLayer.BLOCK, blockPos);
    }

    public static boolean destroyBlocks(Entity entity, AABB aabb) {
        int i = Mth.floor(aabb.minX);
        int j = Mth.floor(aabb.minY);
        int k = Mth.floor(aabb.minZ);
        int l = Mth.floor(aabb.maxX);
        int m = Mth.floor(aabb.maxY);
        int n = Mth.floor(aabb.maxZ);
        boolean bl = false;
        boolean bl2 = false;
        for (int o = i; o <= l; o++)
            for (int p = j; p <= m; p++)
                for (int q = k; q <= n; q++) {
                    BlockPos blockPos = new BlockPos(o, p, q);
                    BlockState blockState = entity.level().getBlockState(blockPos);

                    if (!blockState.isAir() && blockState.getBlock() == Blocks.FIRE)
                        if (entity.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) && !blockState.is(BlockTags.WITHER_IMMUNE))
                            bl2 = entity.level().destroyBlock(blockPos, false) || bl2;
                        else
                            bl = true;
                }
        return bl;
    }

    public static void onBreakInCreative(Level level, BlockPos pos, BlockState state, Player player) {
        DoubleBlockHalf doubleBlockHalf = state.getValue(DoublePlantBlock.HALF);

        if (doubleBlockHalf == DoubleBlockHalf.UPPER) {
            BlockPos blockPos = pos.below();
            BlockState blockState = level.getBlockState(blockPos);

            if (blockState.getBlock() == state.getBlock() && blockState.getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.LOWER) {
                level.setBlock(blockPos, Blocks.AIR.defaultBlockState(), 35);
                level.levelEvent(player, 2001, blockPos, Block.getId(blockState));
            }
        }
    }
}