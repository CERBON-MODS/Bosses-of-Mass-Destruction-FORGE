package com.cerbon.bosses_of_mass_destruction.entity.custom.gauntlet;

import com.cerbon.bosses_of_mass_destruction.entity.util.IEntityTick;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;

public class ClientGauntletDeathHandler implements IEntityTick<ClientLevel> {
    private final GauntletEntity entity;

    public ClientGauntletDeathHandler(GauntletEntity entity) {
        this.entity = entity;
    }

    @Override
    public void tick(ClientLevel level) {
        entity.deathTime++;
        if (entity.deathTime == ServerGauntletDeathHandler.deathAnimationTime)
            entity.remove(Entity.RemovalReason.KILLED);
    }
}
