package com.cerbon.bosses_of_mass_destruction.entity.custom.void_blossom.hitbox;

import com.cerbon.bosses_of_mass_destruction.api.multipart_entities.entity.EntityBounds;
import com.cerbon.bosses_of_mass_destruction.entity.util.BaseEntity;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;

import java.util.Map;

public class NetworkedHitboxManager implements ICompoundHitbox{
    private final BaseEntity entity;
    private final Map<Byte, ICompoundHitbox> hitboxMap;

    public static final DataParameter<Byte> hitbox = EntityDataManager.defineId(BaseEntity.class, DataSerializers.BYTE);

    public NetworkedHitboxManager(BaseEntity entity, Map<Byte, ICompoundHitbox> hitboxMap){
        this.entity = entity;
        this.hitboxMap = hitboxMap;

        entity.getEntityData().define(hitbox, hitboxMap.keySet().iterator().next());
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
