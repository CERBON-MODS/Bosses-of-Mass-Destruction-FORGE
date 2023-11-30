package com.cerbon.bosses_of_mass_destruction.util;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
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
    public static void travel(Vec3 relative, LivingEntity entity, float baseFrictionCoefficient) {
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

    public static void lookAtTarget(Mob mobEntity, Vec3 target, float maxYawChange, float maxPitchChange) {
        double d = target.x - mobEntity.getX();
        double e = target.z - mobEntity.getZ();
        double g = target.y - mobEntity.getEyeY();

        double h = Math.sqrt(d * d + e * e);
        float i = (float) ((Mth.atan2(e, d) * 57.2957763671875) - 90.0f);
        float j = (float) (-(Mth.atan2(g, h) * 57.2957763671875));
        mobEntity.setXRot(changeAngle(mobEntity.getXRot(), j, maxPitchChange));
        mobEntity.setYRot(changeAngle(mobEntity.getYRot(), i, maxYawChange));
    }

    public static float changeAngle(float oldAngle, float newAngle, float maxChangeInAngle) {
        float f = Mth.wrapDegrees(newAngle - oldAngle);

        if (f > maxChangeInAngle)
            f = maxChangeInAngle;

        if (f < -maxChangeInAngle)
            f = -maxChangeInAngle;

        return oldAngle + f;
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
        return entity.isOnFire() ? 15 : entity.level.getBrightness(LightLayer.BLOCK, blockPos);
    }

    public static Explosion.BlockInteraction getEntityDestructionType(Level level){
        return level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) ? Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.NONE;
    }

    public static void destroyBlocks(Entity entity, AABB aabb) {
        int i = Mth.floor(aabb.minX);
        int j = Mth.floor(aabb.minY);
        int k = Mth.floor(aabb.minZ);
        int l = Mth.floor(aabb.maxX);
        int m = Mth.floor(aabb.maxY);
        int n = Mth.floor(aabb.maxZ);
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
