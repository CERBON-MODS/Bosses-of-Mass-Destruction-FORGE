package com.cerbon.bosses_of_mass_destruction.entity.custom.void_blossom;

import com.cerbon.bosses_of_mass_destruction.block.BMDBlocks;
import com.cerbon.bosses_of_mass_destruction.entity.util.IEntityTick;
import net.minecraft.entity.Entity;
import net.minecraft.world.server.ServerWorld;

public class LightBlockPlacer implements IEntityTick<ServerWorld> {
    private final Entity entity;

    public LightBlockPlacer(Entity entity) {
        this.entity = entity;
    }

    @Override
    public void tick(ServerWorld level) {
        if (!level.getBlockState(entity.blockPosition()).is(BMDBlocks.LIGHT.get()))
            level.setBlockAndUpdate(entity.blockPosition(), BMDBlocks.LIGHT.get().defaultBlockState());
    }
}
