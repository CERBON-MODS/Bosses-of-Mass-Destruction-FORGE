package com.cerbon.bosses_of_mass_destruction.entity.spawn;

import com.cerbon.bosses_of_mass_destruction.BossesOfMassDestruction;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;


public class CompoundTagEntityProvider implements IEntityProvider {
    private final CompoundNBT tag;
    private final World world;

    public CompoundTagEntityProvider(CompoundNBT tag, World world) {
        this.tag = tag;
        this.world = world;
    }

    @Override
    public Entity getEntity() {
        Entity entity = EntityType.loadEntityRecursive(tag, world, it -> it);
        if (entity == null) {
            BossesOfMassDestruction.LOGGER.warn("Failed to create entity from tag: " + tag);
            return null;
        }
        return entity;
    }
}
