package com.cerbon.bosses_of_mass_destruction.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.function.Function;

public class BMDUtils {
    public static final ResourceKey<DamageType> SHIELD_PIERCING = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(BMDConstants.MOD_ID, "shield_piercing"));

    public static DamageSource shieldPiercing(Level level, Entity attacker) {
        return VanillaCopiesServer.create(level, SHIELD_PIERCING, attacker);
    }

    public static BlockPos findGroundBelow(Level level, BlockPos pos, Function<BlockPos, Boolean> isOpenBlock) {
        int bottomY = level.getMinBuildHeight();
        for (int i = pos.getY(); i >= bottomY + 1; i--) {
            BlockPos tempPos = new BlockPos(pos.getX(), i, pos.getZ());

            if (level.getBlockState(tempPos).isFaceSturdy(level, tempPos, Direction.UP, SupportType.FULL) && isOpenBlock.apply(tempPos.above()))
                return tempPos;
        }
        return new BlockPos(pos.getX(), bottomY, pos.getZ());
    }

    public static List<Entity> findEntitiesInLine(Level level, Vec3 start, Vec3 end, Entity toExclude) {
        AABB aabb = new AABB(start, end);
        return level.getEntities(toExclude, aabb, entity -> entity.getBoundingBox().clip(start, end).isPresent());
    }

    public static ConfiguredFeature<?, ?> getConfiguredFeature(LevelReader levelReader, ResourceKey<ConfiguredFeature<?, ?>> key) {
        return levelReader.registryAccess().registryOrThrow(Registries.CONFIGURED_FEATURE).getOrThrow(key);
    }

}
