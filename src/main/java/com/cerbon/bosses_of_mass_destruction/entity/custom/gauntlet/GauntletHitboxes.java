package com.cerbon.bosses_of_mass_destruction.entity.custom.gauntlet;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.MathUtils;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.MobUtils;
import com.cerbon.bosses_of_mass_destruction.api.multipart_entities.entity.EntityBounds;
import com.cerbon.bosses_of_mass_destruction.api.multipart_entities.entity.EntityPart;
import com.cerbon.bosses_of_mass_destruction.api.multipart_entities.entity.MutableBox;
import com.cerbon.bosses_of_mass_destruction.entity.damage.IDamageHandler;
import com.cerbon.bosses_of_mass_destruction.entity.util.IEntityStats;
import com.cerbon.bosses_of_mass_destruction.packet.BMDPacketHandler;
import com.cerbon.bosses_of_mass_destruction.packet.custom.ChangeHitboxS2CPacket;
import com.cerbon.bosses_of_mass_destruction.sound.BMDSounds;
import com.cerbon.bosses_of_mass_destruction.util.BMDUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.ModList;

public class GauntletHitboxes implements IDamageHandler {
    private final GauntletEntity entity;
    private final AABB collisionHitbox = new AABB(Vec3.ZERO, new Vec3(2.0, 4.0, 2.0));
    private final AABB clampedCollisionHitbox = new AABB(Vec3.ZERO, new Vec3(2.0, 2.0, 2.0));
    private final String rootBoxPitch = "rootPitch";
    private final String rootBoxYaw = "rootYaw";
    private String nextDamagedPart = null;
    private final String eyeBox = "eye";
    private final String fingersBox = "fingers";
    private final String thumbBox = "thumb";
    private final String pinkyBox = "pinky";
    private final EntityBounds hitboxes = EntityBounds.builder()
            .add(rootBoxYaw).setBounds(0.0, 0.0, 0.0).build()
            .add(rootBoxPitch).setBounds(2.0, 2.6, 0.6).setOffset(0.0, 1.3, 0.0).setPivot(0.0, -0.2, 0.0).setParent(rootBoxYaw).build()
            .add(eyeBox).setBounds(1.1, 1.2, 0.2).setOffset(-0.025, 0.35, 0.4).setParent(rootBoxPitch).build()
            .add(fingersBox).setBounds(1.5, 2.0, 0.5).setOffset(0.0, 1.8, 0.5).setParent(rootBoxPitch).build()
            .add(thumbBox).setBounds(0.3, 1.6, 0.3).setOffset(1.0, 0.6, 0.7).setParent(rootBoxPitch).build()
            .add(pinkyBox).setBounds(0.25, 1.0, 0.25).setOffset(-0.9, 1.7, 0.5).setParent(rootBoxPitch).build()
            .overrideCollisionBox(collisionHitbox)
            .getFactory().create();
    private final String rootFistBox = "rootFist";
    private final String rootFistBoxYaw = "rootFistYaw";
    private final EntityBounds clampedHitboxes = EntityBounds.builder()
            .add(rootFistBoxYaw).setBounds(0.0, 0.0, 0.0).build()
        .add(rootFistBox).setBounds(2.0, 1.5, 2.0).setOffset(0.0, 1.0, 0.0).setParent(rootFistBoxYaw).build()
        .overrideCollisionBox(clampedCollisionHitbox)
        .getFactory().create();
    private EntityBounds currentHitbox = hitboxes;

    private final boolean disableHitboxesForCompatibility = ModList.get().isLoaded("bettercombat");

    public GauntletHitboxes(GauntletEntity entity) {
        this.entity = entity;

        hitboxes.getPart(fingersBox).setRotation(35.0, 0.0, 0.0, true);
        hitboxes.getPart(thumbBox).setRotation(30.0, 0.0, 0.0, true);
        hitboxes.getPart(pinkyBox).setRotation(35.0, 0.0, 0.0, true);
    }

    public void setOpenHandHitbox(){
        if (!entity.level().isClientSide() && currentHitbox != hitboxes){
            BMDPacketHandler.sendToAllPlayersTrackingChunk(new ChangeHitboxS2CPacket(entity.getId(), true), (ServerLevel) entity.level(), entity.position());
        }
         currentHitbox = hitboxes;
    }

    public void setClosedFistHitbox() {
        if (!entity.level().isClientSide() && currentHitbox != clampedHitboxes){
            BMDPacketHandler.sendToAllPlayersTrackingChunk(new ChangeHitboxS2CPacket(entity.getId(), false), (ServerLevel) entity.level(), entity.position());
        }
        currentHitbox = clampedHitboxes;
    }

    public EntityBounds getHitbox(){
        return currentHitbox;
    }

    public void updatePosition(){
        EntityPart rootPitch = hitboxes.getPart(rootBoxPitch);
        EntityPart rootYaw = hitboxes.getPart(rootBoxYaw);

        rootYaw.setRotation(0.0, -entity.getYRot(), 0.0, true);
        rootPitch.setRotation(entity.getXRot(), 0.0, 0.0, true);

        rootYaw.setX(entity.getX());
        rootYaw.setY(entity.getY());
        rootYaw.setZ(entity.getZ());

        EntityPart fistYaw = clampedHitboxes.getPart(rootFistBoxYaw);
        EntityPart fist = clampedHitboxes.getPart(rootFistBox);

        fistYaw.setRotation(0.0, -entity.getYRot(), 0.0, true);
        fist.setRotation(entity.getXRot(), 0.0, 0.0, true);

        fistYaw.setX(entity.getX());
        fistYaw.setY(entity.getY());
        fistYaw.setZ(entity.getZ());

        MutableBox overrideBox = hitboxes.getOverrideBox();
        if (overrideBox != null) overrideBox.setBox(
                collisionHitbox.move(entity.position()).move(-1.0, 0.0, -1.0));

        MutableBox overrideClampedHitbox = clampedHitboxes.getOverrideBox();
        if (overrideClampedHitbox != null) overrideClampedHitbox.setBox(
                clampedCollisionHitbox.move(entity.position()).move(-1.0, 0.0, -1.0));
    }

    public void setNextDamagedPart(String part){
        nextDamagedPart = part;
    }

    @Override
    public void beforeDamage(IEntityStats stats, DamageSource damageSource, float amount) {}

    @Override
    public void afterDamage(IEntityStats stats, DamageSource damageSource, float amount, boolean result) {}

    @Override
    public boolean shouldDamage(LivingEntity actor, DamageSource damageSource, float amount) {
        String part = nextDamagedPart;
        nextDamagedPart = null;

        if (disableHitboxesForCompatibility) return true;
        if (part != null && part.equals(eyeBox) || damageSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) return true;

        if (damageSource.is(DamageTypeTags.IS_EXPLOSION)){
            Vec3 pos = damageSource.getSourcePosition();
            if (pos != null){
                Vec3 explosionDirection = MathUtils.unNormedDirection(pos, MobUtils.eyePos(entity));
                if (!MathUtils.facingSameDirection(explosionDirection, entity.getLookAngle()))
                    return true;
            }
        }

        if (!damageSource.is(DamageTypeTags.IS_PROJECTILE)){
            Entity entity1 = damageSource.getEntity();
            if (entity1 instanceof LivingEntity livingEntity){
                livingEntity.knockback(0.5, actor.getX() - livingEntity.getX(), actor.getZ() - livingEntity.getZ());
            }
        }

        if (!damageSource.is(DamageTypeTags.IS_FIRE))
            entity.playSound(BMDSounds.GAUNTLET_CLINK.get(), 1.0f, BMDUtils.randomPitch(actor.getRandom()));

        return false;
    }
}
