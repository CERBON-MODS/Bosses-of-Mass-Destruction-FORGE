package com.cerbon.bosses_of_mass_destruction.entity.custom.void_blossom.hitbox;

import com.cerbon.bosses_of_mass_destruction.api.multipart_entities.entity.EntityBounds;

public interface ICompoundHitbox {
    void updatePosition();
    EntityBounds getBounds();
    void setNextDamagedPart(String part);
}
