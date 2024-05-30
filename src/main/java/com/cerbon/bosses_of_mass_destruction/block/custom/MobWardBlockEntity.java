package com.cerbon.bosses_of_mass_destruction.block.custom;

import com.cerbon.bosses_of_mass_destruction.block.BMDBlockEntities;
import com.cerbon.bosses_of_mass_destruction.block.BMDBlocks;

public class MobWardBlockEntity extends ChunkCacheBlockEntity {

    public MobWardBlockEntity() {
        super(BMDBlocks.MOB_WARD.get(), BMDBlockEntities.MOB_WARD.get());
    }
}
