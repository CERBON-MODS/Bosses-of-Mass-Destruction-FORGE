package com.cerbon.bosses_of_mass_destruction.entity.spawn;

import com.cerbon.bosses_of_mass_destruction.BossesOfMassDestruction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;


public class CompoundTagEntityProvider implements IEntityProvider {
    private final CompoundTag tag;
    private final Level world;

    public CompoundTagEntityProvider(CompoundTag tag, Level world) {
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
