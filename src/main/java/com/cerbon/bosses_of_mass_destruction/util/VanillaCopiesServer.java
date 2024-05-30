package com.cerbon.bosses_of_mass_destruction.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoublePlantBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.*;

public class VanillaCopiesServer {
    public static void travel(Vector3d relative, LivingEntity entity, float baseFrictionCoefficient) {
        if (entity.isInWater()) {
            entity.moveRelative(0.02F, relative);
            entity.move(MoverType.SELF, entity.getDeltaMovement());
            entity.setDeltaMovement(entity.getDeltaMovement().scale(0.800000011920929));

        } else if (entity.isInLava()) {
            entity.moveRelative(0.02F, relative);
            entity.move(MoverType.SELF, entity.getDeltaMovement());
            entity.setDeltaMovement(entity.getDeltaMovement().scale(0.5));

        } else {
            float friction = entity.isOnGround()
                    ? entity.level.getBlockState(new BlockPos(entity.getX(), entity.getY() - 1.0, entity.getZ())).getBlock().getFriction() * baseFrictionCoefficient
                    : baseFrictionCoefficient;

            float g = 0.16277137F / (friction * friction * friction);

            entity.moveRelative(entity.isOnGround() ? 0.1F * g : 0.02F, relative);
            entity.move(MoverType.SELF, entity.getDeltaMovement());
            entity.setDeltaMovement(entity.getDeltaMovement().scale(friction));
        }
        entity.calculateEntityAnimation(entity, false);
    }

    public static void lookAtTarget(MobEntity mobEntity, Vector3d target, float maxYawChange, float maxPitchChange) {
        double d = target.x - mobEntity.getX();
        double e = target.z - mobEntity.getZ();
        double g = target.y - mobEntity.getEyeY();

        double h = Math.sqrt(d * d + e * e);
        float i = (float) ((MathHelper.atan2(e, d) * 57.2957763671875) - 90.0f);
        float j = (float) (-(MathHelper.atan2(g, h) * 57.2957763671875));
        mobEntity.xRot = (changeAngle(mobEntity.xRot, j, maxPitchChange));
        mobEntity.yRot = (changeAngle(mobEntity.yRot, i, maxYawChange));
    }

    public static float changeAngle(float oldAngle, float newAngle, float maxChangeInAngle) {
        float f = MathHelper.wrapDegrees(newAngle - oldAngle);

        if (f > maxChangeInAngle)
            f = maxChangeInAngle;

        if (f < -maxChangeInAngle)
            f = -maxChangeInAngle;

        return oldAngle + f;
    }

    public static boolean hasDirectLineOfSight(Vector3d to, Vector3d from, IBlockReader level, Entity entity) {
        RayTraceContext context = new RayTraceContext(
                to,
                from,
                RayTraceContext.BlockMode.COLLIDER,
                RayTraceContext.FluidMode.NONE,
                entity
        );
        return level.clip(context).getType() == RayTraceResult.Type.MISS;
    }

    public static void awardExperience(int amount, Vector3d pos, World level) {
        int amt = amount;
        while (amt > 0) {
            int i = ExperienceOrbEntity.getExperienceValue(amt);
            amt -= i;
            level.addFreshEntity(new ExperienceOrbEntity(level, pos.x, pos.y, pos.z, i));
        }
    }

    public static int getBlockLight(Entity entity, BlockPos blockPos) {
        return entity.isOnFire() ? 15 : entity.level.getBrightness(LightType.BLOCK, blockPos);
    }

    public static Explosion.Mode getEntityDestructionType(World level){
        return level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) ? Explosion.Mode.DESTROY : Explosion.Mode.NONE;
    }

    public static void destroyBlocks(Entity entity, AxisAlignedBB aabb) {
        int i = MathHelper.floor(aabb.minX);
        int j = MathHelper.floor(aabb.minY);
        int k = MathHelper.floor(aabb.minZ);
        int l = MathHelper.floor(aabb.maxX);
        int m = MathHelper.floor(aabb.maxY);
        int n = MathHelper.floor(aabb.maxZ);
        boolean bl2 = false;
        for (int o = i; o <= l; o++)
            for (int p = j; p <= m; p++)
                for (int q = k; q <= n; q++) {
                    BlockPos blockPos = new BlockPos(o, p, q);
                    BlockState blockState = entity.level.getBlockState(blockPos);

                    if (!blockState.isAir() && blockState.getBlock() == Blocks.FIRE)
                        if (entity.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) && !blockState.is(BlockTags.WITHER_IMMUNE))
                            bl2 = entity.level.destroyBlock(blockPos, false) || bl2;
                }
    }

    public static void onBreakInCreative(World level, BlockPos pos, BlockState state, PlayerEntity player) {
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
