package com.cerbon.bosses_of_mass_destruction.entity.custom.void_blossom.hitbox;

import com.cerbon.bosses_of_mass_destruction.api.multipart_entities.entity.EntityBounds;
import com.cerbon.bosses_of_mass_destruction.api.multipart_entities.entity.EntityPart;
import com.cerbon.bosses_of_mass_destruction.api.multipart_entities.entity.MutableBox;
import com.cerbon.bosses_of_mass_destruction.entity.custom.void_blossom.VoidBlossomEntity;
import com.cerbon.bosses_of_mass_destruction.entity.damage.IDamageHandler;
import com.cerbon.bosses_of_mass_destruction.entity.util.IEntityStats;
import com.cerbon.bosses_of_mass_destruction.entity.util.IEntityTick;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class VoidBlossomCompoundHitbox implements ICompoundHitbox, IDamageHandler, IEntityTick<ServerLevel> {
    private final VoidBlossomEntity entity;
    private final EntityBounds hitboxes;
    private final String root;
    private final AABB collisionHitbox;
    private final List<String> spikedBoxes;

    private String nextDamagedPart;

    public VoidBlossomCompoundHitbox(VoidBlossomEntity entity, EntityBounds hitboxes, String root, AABB collisionHitbox, List<String> spikedBoxes) {
        this.entity = entity;
        this.hitboxes = hitboxes;
        this.root = root;
        this.collisionHitbox = collisionHitbox;
        this.spikedBoxes = spikedBoxes;
    }

    @Override
    public void updatePosition() {
        EntityPart rootYaw = hitboxes.getPart(root);
        rootYaw.setRotation(0.0, -entity.getYRot(), 0.0, true);

        rootYaw.setX(entity.getX());
        rootYaw.setY(entity.getY());
        rootYaw.setZ(entity.getZ());

        MutableBox overrideBox = hitboxes.getOverrideBox();
        if (overrideBox != null) overrideBox.setBox(collisionHitbox.move(entity.position()).move(-1.0, 0.0, -1.0));
    }

    @Override
    public EntityBounds getBounds() {
        return hitboxes;
    }

    @Override
    public void setNextDamagedPart(String part) {
        nextDamagedPart = part;
    }

    @Override
    public void beforeDamage(IEntityStats stats, DamageSource damageSource, float amount) {}

    @Override
    public void afterDamage(IEntityStats stats, DamageSource damageSource, float amount, boolean result) {
        String part = nextDamagedPart;
        nextDamagedPart = null;

        if (result)
            if (spikedBoxes.contains(part) && !damageSource.isProjectile()){
                float damage = (float) entity.getAttributeValue(Attributes.ATTACK_DAMAGE);
                if (damageSource.getEntity() != null)
                    damageSource.getEntity().hurt(DamageSource.thorns(entity), damage);
            }
    }

    @Override
    public boolean shouldDamage(LivingEntity actor, DamageSource damageSource, float amount) {
        return true;
    }

    @Override
    public void tick(ServerLevel level) {
        EntityPart rootYaw = hitboxes.getPart(root);
        rootYaw.setRotation(0.0, -entity.getYRot(), 0.0, true);
    }
}
