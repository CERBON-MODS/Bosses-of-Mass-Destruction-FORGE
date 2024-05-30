package com.cerbon.bosses_of_mass_destruction.entity.custom.gauntlet;

import com.cerbon.bosses_of_mass_destruction.entity.util.IEntityTick;
import net.minecraft.world.World;

public class ClientGauntletDeathHandler implements IEntityTick<World> {
    private final GauntletEntity entity;

    public ClientGauntletDeathHandler(GauntletEntity entity) {
        this.entity = entity;
    }

    @Override
    public void tick(World level) {
        entity.deathTime++;
        if (entity.deathTime == ServerGauntletDeathHandler.deathAnimationTime)
            entity.remove();
    }
}
