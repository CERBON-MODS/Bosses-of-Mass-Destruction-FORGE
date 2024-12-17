package com.cerbon.bosses_of_mass_destruction.entity.custom.void_blossom.hitbox;

import com.cerbon.bosses_of_mass_destruction.entity.util.BaseEntity;
import com.cerbon.cerbons_api.api.multipart_entities.entity.EntityBounds;
import net.minecraft.network.syncher.EntityDataAccessor;

import java.util.Map;

public class NetworkedHitboxManager implements ICompoundHitbox{
    private final BaseEntity entity;
    private final Map<Byte, ICompoundHitbox> hitboxMap;
    public final EntityDataAccessor<Byte> hitbox;

    public NetworkedHitboxManager(BaseEntity entity, Map<Byte, ICompoundHitbox> hitboxMap, EntityDataAccessor<Byte> hitbox) {
        this.entity = entity;
        this.hitboxMap = hitboxMap;
        this.hitbox = hitbox;
    }

    @Override
    public void updatePosition() {
        for (ICompoundHitbox hitbox1 : hitboxMap.values())
            hitbox1.updatePosition();
    }

    @Override
    public EntityBounds getBounds() {
        return hitboxMap.get(entity.getEntityData().get(hitbox)).getBounds();
    }

    @Override
    public void setNextDamagedPart(String part) {
        hitboxMap.get(entity.getEntityData().get(hitbox)).setNextDamagedPart(part);
    }
}
