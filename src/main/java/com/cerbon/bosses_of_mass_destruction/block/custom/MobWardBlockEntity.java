package com.cerbon.bosses_of_mass_destruction.block.custom;

import com.cerbon.bosses_of_mass_destruction.block.BMDBlockEntities;
import com.cerbon.bosses_of_mass_destruction.block.BMDBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class MobWardBlockEntity extends ChunkCacheBlockEntity {

    public MobWardBlockEntity(BlockPos pos, BlockState blockState) {
        super(BMDBlocks.MOB_WARD.get(), BMDBlockEntities.MOB_WARD.get(), pos, blockState);
    }
}
