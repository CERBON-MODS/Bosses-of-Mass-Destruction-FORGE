package com.cerbon.bosses_of_mass_destruction.entity.custom.void_blossom;

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
//        if (!level.getBlockState(entity.blockPosition()).is(Blocks.LIGHT))
//            level.setBlockAndUpdate(entity.blockPosition(), Blocks.LIGHT.defaultBlockState());
    }
}
