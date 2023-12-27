package com.cerbon.bosses_of_mass_destruction.entity.custom.gauntlet;

import com.cerbon.bosses_of_mass_destruction.config.mob.GauntletConfig;
import com.cerbon.bosses_of_mass_destruction.entity.damage.CompositeDamageHandler;
import com.cerbon.bosses_of_mass_destruction.entity.damage.DamageMemory;
import com.cerbon.bosses_of_mass_destruction.entity.util.BaseEntity;
import com.cerbon.bosses_of_mass_destruction.entity.util.CompositeDataAccessorHandler;
import com.cerbon.bosses_of_mass_destruction.entity.util.CompositeEntityEventHandler;
import com.cerbon.bosses_of_mass_destruction.entity.util.EffectsImmunity;
import com.cerbon.bosses_of_mass_destruction.sound.BMDSounds;
import com.cerbon.bosses_of_mass_destruction.util.VanillaCopiesServer;
import com.cerbon.cerbons_api.api.multipart_entities.entity.EntityBounds;
import com.cerbon.cerbons_api.api.multipart_entities.entity.MultipartAwareEntity;
import com.cerbon.cerbons_api.api.multipart_entities.util.CompoundOrientedBox;
import com.cerbon.cerbons_api.api.static_utilities.MobUtils;
import com.cerbon.cerbons_api.capability.CerbonsApiCapabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.animation.AnimatableManager;

import java.util.Map;

public class GauntletEntity extends BaseEntity implements MultipartAwareEntity {
    public final GauntletHitboxes hitboxHelper = new GauntletHitboxes(this);
    public final GauntletClientLaserHandler laserHandler = new GauntletClientLaserHandler(this, postTickEvents);
    public final GauntletClientEnergyShieldHandler energyShieldHandler = new GauntletClientEnergyShieldHandler(this, postTickEvents);
    public final GauntletBlindnessIndicatorParticles clientBlindnessHandler = new GauntletBlindnessIndicatorParticles(this, preTickEvents);
    public final DamageMemory damageMemory = new DamageMemory(5, this);

    private final AnimationHolder animationHandler;

    public static final EntityDataAccessor<Integer> laserTarget = SynchedEntityData.defineId(GauntletEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Boolean> isEnergized = SynchedEntityData.defineId(GauntletEntity.class, EntityDataSerializers.BOOLEAN);

    public GauntletEntity(EntityType<? extends PathfinderMob> entityType, Level level, GauntletConfig mobConfig) {
        super(entityType, level);

        GauntletGoalHandler gauntletGoalHandler = new GauntletGoalHandler(this, goalSelector, targetSelector, postTickEvents, mobConfig);
        animationHandler = new AnimationHolder(
                this, Map.of(
                GauntletAttacks.punchAttack, new AnimationHolder.Animation("punch_start", "punch_loop"),
                GauntletAttacks.stopPunchAnimation, new AnimationHolder.Animation("punch_stop", "idle"),
                GauntletAttacks.stopPoundAnimation, new AnimationHolder.Animation("pound_stop", "idle"),
                GauntletAttacks.laserAttack, new AnimationHolder.Animation("laser_eye_start", "laser_eye_loop"),
                GauntletAttacks.laserAttackStop, new AnimationHolder.Animation("laser_eye_stop", "idle"),
                GauntletAttacks.swirlPunchAttack, new AnimationHolder.Animation("swirl_punch", "idle"),
                GauntletAttacks.blindnessAttack, new AnimationHolder.Animation("cast", "idle"),
                (byte) 3, new AnimationHolder.Animation("death", "idle")
        ), GauntletAttacks.stopAttackAnimation, 5);

        noCulling = true;
        laserHandler.initDataTracker();
        energyShieldHandler.initDataTracker();
        damageHandler = new CompositeDamageHandler(hitboxHelper, gauntletGoalHandler, damageMemory);
        entityEventHandler = new CompositeEntityEventHandler(animationHandler, laserHandler, clientBlindnessHandler);
        dataAccessorHandler = new CompositeDataAccessorHandler(laserHandler, energyShieldHandler);
        clientTick = laserHandler;
        serverTick = serverLevel -> {if (getTarget() == null) heal(mobConfig.idleHealingPerTick);};
        bossBar = new ServerBossEvent(getDisplayName(), BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.NOTCHED_6);
        mobEffectHandler = new EffectsImmunity(MobEffects.WITHER, MobEffects.POISON);
        moveHandler = gauntletGoalHandler;
        nbtHandler = gauntletGoalHandler;
        deathClientTick = new ClientGauntletDeathHandler(this);
        deathServerTick = new ServerGauntletDeathHandler(this, CerbonsApiCapabilities.getLevelEventScheduler(level), mobConfig);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        animationHandler.registerControllers(data);
    }

    @Override
    protected void checkFallDamage(double y, boolean onGround, @NotNull BlockState state, @NotNull BlockPos pos) {}

    @Override
    public void travel(@NotNull Vec3 travelVector) {
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
    public boolean causeFallDamage(float fallDistance, float multiplier, @NotNull DamageSource source) {
        return false;
    }

    @Override
    public int getMaxHeadXRot() {
        return 90;
    }

    @Override
    public CompoundOrientedBox getCompoundBoundingBox(AABB bounds) {
        return hitboxHelper.getHitbox().getBox(bounds);
    }

    @Override
    public EntityBounds getBounds() {
        return hitboxHelper.getHitbox();
    }

    @Override
    protected float getStandingEyeHeight(@NotNull Pose pose, EntityDimensions dimensions) {
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
    protected SoundEvent getHurtSound(@NotNull DamageSource damageSource) {
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
        MobUtils.preventDespawnExceptPeaceful(this, level());
    }
}
