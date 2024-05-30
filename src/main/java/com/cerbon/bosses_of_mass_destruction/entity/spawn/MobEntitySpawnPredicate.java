package com.cerbon.bosses_of_mass_destruction.entity.spawn;

import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.Entity;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.spawner.WorldEntitySpawner;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;

public class MobEntitySpawnPredicate implements ISpawnPredicate {
    private final IWorldReader levelReader;

    public MobEntitySpawnPredicate(IWorldReader worldView) {
        this.levelReader = worldView;
    }

    @Override
    public boolean canSpawn(Vector3d pos, Entity entity) {
        BlockPos blockPos = new BlockPos(pos);
        if (!levelReader.hasChunkAt(blockPos)) return false;

        BlockState blockState = levelReader.getBlockState(blockPos);
        FluidState fluidState = levelReader.getFluidState(blockPos);
        AxisAlignedBB prospectiveBoundingBox = entity.getType().getAABB(pos.x, pos.y, pos.z);

        return (!levelReader.containsAnyLiquid(prospectiveBoundingBox)
                && levelReader.noCollision(prospectiveBoundingBox)
                && WorldEntitySpawner.isValidEmptySpawnBlock(levelReader, blockPos, blockState, fluidState, entity.getType()));
    }
}

