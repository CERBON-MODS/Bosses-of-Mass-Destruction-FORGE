package com.cerbon.bosses_of_mass_destruction.block.custom;

import com.cerbon.bosses_of_mass_destruction.block.BMDBlockEntities;
import com.cerbon.bosses_of_mass_destruction.block.BMDBlocks;

public class MonolithBlockEntity extends ChunkCacheBlockEntity {

    public MonolithBlockEntity() {
        super(BMDBlocks.MONOLITH_BLOCK.get(), BMDBlockEntities.MONOLITH_BLOCK_ENTITY.get());
    }
}
