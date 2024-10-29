package com.cerbon.bosses_of_mass_destruction.entity.spawn;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class MobEntitySpawnPredicate implements ISpawnPredicate {
    private final LevelReader levelReader;

    public MobEntitySpawnPredicate(LevelReader worldView) {
        this.levelReader = worldView;
    }

    @Override
    public boolean canSpawn(Vec3 pos, Entity entity) {
        BlockPos blockPos = BlockPos.containing(pos);
        if (!levelReader.hasChunkAt(blockPos)) return false;

        BlockState blockState = levelReader.getBlockState(blockPos);
        FluidState fluidState = levelReader.getFluidState(blockPos);
        AABB prospectiveBoundingBox = entity.getType().getSpawnAABB(pos.x, pos.y, pos.z);

        return (!levelReader.containsAnyLiquid(prospectiveBoundingBox)
                && levelReader.noCollision(prospectiveBoundingBox)
                && NaturalSpawner.isValidEmptySpawnBlock(levelReader, blockPos, blockState, fluidState, entity.getType()));
    }
}

