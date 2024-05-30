package com.cerbon.bosses_of_mass_destruction.entity.custom.gauntlet;

import com.cerbon.bosses_of_mass_destruction.api.multipart_entities.entity.EntityBounds;
import com.cerbon.bosses_of_mass_destruction.api.multipart_entities.entity.MultipartAwareEntity;
import com.cerbon.bosses_of_mass_destruction.api.multipart_entities.util.CompoundOrientedBox;
import com.cerbon.bosses_of_mass_destruction.capability.util.BMDCapabilities;
import com.cerbon.bosses_of_mass_destruction.config.mob.GauntletConfig;
import com.cerbon.bosses_of_mass_destruction.entity.damage.CompositeDamageHandler;
import com.cerbon.bosses_of_mass_destruction.entity.damage.DamageMemory;
import com.cerbon.bosses_of_mass_destruction.entity.util.BaseEntity;
import com.cerbon.bosses_of_mass_destruction.entity.util.CompositeDataAccessorHandler;
import com.cerbon.bosses_of_mass_destruction.entity.util.CompositeEntityEventHandler;
import com.cerbon.bosses_of_mass_destruction.entity.util.EffectsImmunity;
import com.cerbon.bosses_of_mass_destruction.sound.BMDSounds;
import com.cerbon.bosses_of_mass_destruction.util.BMDUtils;
import com.cerbon.bosses_of_mass_destruction.util.VanillaCopiesServer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Pose;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.BossInfo;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerBossInfo;
import software.bernie.geckolib3.core.manager.AnimationData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class GauntletEntity extends BaseEntity implements MultipartAwareEntity {
    public final GauntletHitboxes hitboxHelper = new GauntletHitboxes(this);
    public final GauntletClientLaserHandler laserHandler = new GauntletClientLaserHandler(this, postTickEvents);
    public final GauntletClientEnergyShieldHandler energyShieldHandler = new GauntletClientEnergyShieldHandler(this, postTickEvents);
    public final GauntletBlindnessIndicatorParticles clientBlindnessHandler = new GauntletBlindnessIndicatorParticles(this, preTickEvents);
    public final DamageMemory damageMemory = new DamageMemory(5, this);

    private final AnimationHolder animationHandler;

    public static final DataParameter<Integer> laserTarget = EntityDataManager.defineId(GauntletEntity.class, DataSerializers.INT);
    public static final DataParameter<Boolean> isEnergized = EntityDataManager.defineId(GauntletEntity.class, DataSerializers.BOOLEAN);

    public GauntletEntity(EntityType<? extends CreatureEntity> entityType, World level, GauntletConfig mobConfig) {
        super(entityType, level);

        Map<Byte, AnimationHolder.Animation> animationStatusFlags = new HashMap<>();
        animationStatusFlags.put(GauntletAttacks.punchAttack, new AnimationHolder.Animation("punch_start", "punch_loop"));
        animationStatusFlags.put(GauntletAttacks.stopPunchAnimation, new AnimationHolder.Animation("punch_stop", "idle"));
        animationStatusFlags.put(GauntletAttacks.stopPoundAnimation, new AnimationHolder.Animation("pound_stop", "idle"));
        animationStatusFlags.put(GauntletAttacks.laserAttack, new AnimationHolder.Animation("laser_eye_start", "laser_eye_loop"));
        animationStatusFlags.put(GauntletAttacks.laserAttackStop, new AnimationHolder.Animation("laser_eye_stop", "idle"));
        animationStatusFlags.put(GauntletAttacks.swirlPunchAttack, new AnimationHolder.Animation("swirl_punch", "idle"));
        animationStatusFlags.put(GauntletAttacks.blindnessAttack, new AnimationHolder.Animation("cast", "idle"));
        animationStatusFlags.put((byte) 3, new AnimationHolder.Animation("death", "idle"));

        GauntletGoalHandler gauntletGoalHandler = new GauntletGoalHandler(this, goalSelector, targetSelector, postTickEvents, mobConfig);
        animationHandler = new AnimationHolder(this, animationStatusFlags, GauntletAttacks.stopAttackAnimation, 5);

        noCulling = true;
        laserHandler.initDataTracker();
        energyShieldHandler.initDataTracker();
        damageHandler = new CompositeDamageHandler(hitboxHelper, gauntletGoalHandler, damageMemory);
        entityEventHandler = new CompositeEntityEventHandler(animationHandler, laserHandler, clientBlindnessHandler);
        dataAccessorHandler = new CompositeDataAccessorHandler(laserHandler, energyShieldHandler);
        clientTick = laserHandler;
        serverTick = serverLevel -> {if (getTarget() == null) heal(mobConfig.idleHealingPerTick);};
        bossBar = new ServerBossInfo(getDisplayName(), BossInfo.Color.RED, BossInfo.Overlay.NOTCHED_6);
        mobEffectHandler = new EffectsImmunity(Effects.WITHER, Effects.POISON);
        moveHandler = gauntletGoalHandler;
        nbtHandler = gauntletGoalHandler;
        deathClientTick = new ClientGauntletDeathHandler(this);
        deathServerTick = new ServerGauntletDeathHandler(this, BMDCapabilities.getLevelEventScheduler(level), mobConfig);
    }

    @Override
    public void registerControllers(AnimationData data) {
        animationHandler.registerControllers(data);
    }

    @Override
    protected void checkFallDamage(double y, boolean onGround, @Nonnull BlockState state, @Nonnull BlockPos pos) {}

    @Override
    public void travel(@Nonnull Vector3d travelVector) {
        VanillaCopiesServer.travel(travelVector, this, 0.85f);
    }

    @Override
    public void setNextDamagedPart(@Nullable String part) {
        hitboxHelper.setNextDamagedPart(part);
    }

    @Override
    public void onSetPos(double x, double y, double z) {
        if (hitboxHelper != null) hitboxHelper.updatePosition();
    }

    @Override
    public boolean onClimbable() {
        return false;
    }

    @Override
    public boolean causeFallDamage(float pFallDistance, float pDamageMultiplier) {
        return false;
    }

    @Override
    public int getMaxHeadXRot() {
        return 90;
    }

    @Override
    public CompoundOrientedBox getCompoundBoundingBox(AxisAlignedBB bounds) {
        return hitboxHelper.getHitbox().getBox(bounds);
    }

    @Override
    public EntityBounds getBounds() {
        return hitboxHelper.getHitbox();
    }

    @Override
    protected float getStandingEyeHeight(@Nonnull Pose pose, EntitySize dimensions) {
        return dimensions.height * 0.4f;
    }

    @Override
    public boolean isInWall() {
        return false;
    }

    @Override
    public int getArmorValue() {
        return getTarget() != null ? super.getArmorValue() : 24;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return BMDSounds.GAUNTLET_IDLE.get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(@Nonnull DamageSource damageSource) {
        return BMDSounds.GAUNTLET_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return BMDSounds.GAUNTLET_DEATH.get();
    }

    @Override
    protected float getSoundVolume() {
        return 2.0f;
    }

    @Override
    public void checkDespawn() {
        BMDUtils.preventDespawnExceptPeaceful(this, level);
    }
}
