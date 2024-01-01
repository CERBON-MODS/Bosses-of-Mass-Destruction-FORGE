package com.cerbon.bosses_of_mass_destruction.entity.custom.void_blossom;

import com.cerbon.bosses_of_mass_destruction.config.mob.VoidBlossomConfig;
import com.cerbon.bosses_of_mass_destruction.entity.ai.TargetSwitcher;
import com.cerbon.bosses_of_mass_destruction.entity.ai.goals.ActionGoal;
import com.cerbon.bosses_of_mass_destruction.entity.ai.goals.CompositeGoal;
import com.cerbon.bosses_of_mass_destruction.entity.ai.goals.FindTargetGoal;
import com.cerbon.bosses_of_mass_destruction.entity.custom.gauntlet.AnimationHolder;
import com.cerbon.bosses_of_mass_destruction.entity.custom.void_blossom.hitbox.NetworkedHitboxManager;
import com.cerbon.bosses_of_mass_destruction.entity.custom.void_blossom.hitbox.VoidBlossomHitboxes;
import com.cerbon.bosses_of_mass_destruction.entity.damage.CompositeDamageHandler;
import com.cerbon.bosses_of_mass_destruction.entity.damage.DamageMemory;
import com.cerbon.bosses_of_mass_destruction.entity.damage.StagedDamageHandler;
import com.cerbon.bosses_of_mass_destruction.entity.util.BaseEntity;
import com.cerbon.bosses_of_mass_destruction.entity.util.CompositeEntityEventHandler;
import com.cerbon.bosses_of_mass_destruction.entity.util.CompositeEntityTick;
import com.cerbon.bosses_of_mass_destruction.sound.BMDSounds;
import com.cerbon.cerbons_api.api.general.data.BooleanFlag;
import com.cerbon.cerbons_api.api.general.event.TimedEvent;
import com.cerbon.cerbons_api.api.multipart_entities.entity.EntityBounds;
import com.cerbon.cerbons_api.api.multipart_entities.entity.MultipartAwareEntity;
import com.cerbon.cerbons_api.api.multipart_entities.util.CompoundOrientedBox;
import com.cerbon.cerbons_api.api.static_utilities.MobUtils;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.animation.AnimatableManager;

import java.util.List;
import java.util.Map;

public class VoidBlossomEntity extends BaseEntity implements MultipartAwareEntity {
    private final AnimationHolder animationHolder;
    private final NetworkedHitboxManager hitboxHelper;

    public final VoidBlossomClientSpikeHandler clientSpikeHandler;
    public static final List<Float> hpMilestones = List.of(0.0f, 0.25f, 0.5f, 0.75f, 1.0f);

    public VoidBlossomEntity(EntityType<? extends PathfinderMob> entityType, Level level, VoidBlossomConfig mobConfig) {
        super(entityType, level);
        noCulling = true;

        this.animationHolder = new AnimationHolder(
                this, Map.of(
                VoidBlossomAttacks.spikeAttack, new AnimationHolder.Animation("spike", "idle"),
                VoidBlossomAttacks.spikeWaveAttack, new AnimationHolder.Animation("spike_wave", "idle"),
                VoidBlossomAttacks.sporeAttack, new AnimationHolder.Animation("spore", "idle"),
                VoidBlossomAttacks.bladeAttack, new AnimationHolder.Animation("leaf_blade", "idle"),
                VoidBlossomAttacks.blossomAction, new AnimationHolder.Animation("blossom", "idle"),
                VoidBlossomAttacks.spawnAction, new AnimationHolder.Animation("spawn", "idle"),
                (byte) 3, new AnimationHolder.Animation("death", "idle")),
                VoidBlossomAttacks.stopAttackAnimation, 0);

        VoidBlossomHitboxes hitboxes = new VoidBlossomHitboxes(this);
        this.hitboxHelper = new NetworkedHitboxManager(this, hitboxes.getMap());
        entityEventHandler = new CompositeEntityEventHandler(animationHolder, new ClientSporeEffectHandler(this, preTickEvents), new ClientDeathEffectHandler(this, preTickEvents));
        DamageMemory damageMemory = new DamageMemory(10, this);
        TargetSwitcher targetSwitcher = new TargetSwitcher(this, damageMemory);

        BooleanFlag shouldSpawnBlossoms = new BooleanFlag();
        StagedDamageHandler hpDetector = new StagedDamageHandler(hpMilestones, shouldSpawnBlossoms::flag);

        bossBar = new ServerBossEvent(this.getDisplayName(), BossEvent.BossBarColor.GREEN, BossEvent.BossBarOverlay.NOTCHED_12);

        this.clientSpikeHandler = new VoidBlossomClientSpikeHandler();
        clientTick = clientSpikeHandler;

        serverTick = new CompositeEntityTick<>(
                new LightBlockPlacer(this),
                new VoidBlossomSpikeTick(this),
                hitboxes.getTickers(),
                new CappedHeal(this, hpMilestones, mobConfig.idleHealingPerTick)
        );
        deathServerTick = new CompositeEntityTick<>(
                new LightBlockRemover(this),
                new VoidBlossomDropExpDeathTick(this, preTickEvents, mobConfig.experienceDrop)
        );

        damageHandler = new CompositeDamageHandler(hpDetector, hitboxes.getDamageHandlers(), damageMemory);

        if (!level.isClientSide() && level instanceof ServerLevel){
            VoidBlossomAttacks attackHandler = new VoidBlossomAttacks(this, preTickEvents, shouldSpawnBlossoms::getAndReset, targetSwitcher);
            goalSelector.addGoal(2, new CompositeGoal());
            goalSelector.addGoal(1, new CompositeGoal(attackHandler.buildAttackGoal(), new ActionGoal(this::canContinueAttack, null, this::lookAtTarget, null, null)));

            targetSelector.addGoal(2, new FindTargetGoal<>(this, Player.class, d -> getBoundingBox().inflate(d), 10, true, false, null));

            preTickEvents.addEvent(
                    new TimedEvent(
                            () -> playSound(BMDSounds.SPIKE_WAVE_INDICATOR.get(), 2.0f, 0.7f),
                            1
                    )
            );
        }else if (level().isClientSide())
            animationHolder.handleEntityEvent(VoidBlossomAttacks.spawnAction);
    }

    private void lookAtTarget(){
        LivingEntity target = getTarget();
        if (target != null) {
            lookControl.setLookAt(MobUtils.eyePos(target));
            lookAt(target, getMaxHeadYRot(), getMaxHeadXRot());
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        animationHolder.registerControllers(data);
    }

    @Override
    public void move(@NotNull MoverType type, @NotNull Vec3 movement) {
        super.move(type, new Vec3(0.0, movement.y(), 0.0));
    }

    private boolean canContinueAttack(){
        return isAlive() && getTarget() != null;
    }

    @Override
    public boolean isOnFire() {
        return false;
    }

    @Override
    public CompoundOrientedBox getCompoundBoundingBox(AABB bounds) {
        return hitboxHelper.getBounds().getBox(bounds);
    }

    @Override
    public EntityBounds getBounds() {
        return hitboxHelper.getBounds();
    }

    @Override
    public boolean isInWall() {
        return false;
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(@NotNull DamageSource damageSource) {
        return BMDSounds.VOID_BLOSSOM_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return BMDSounds.VOID_BLOSSOM_HURT.get();
    }

    @Override
    protected float getSoundVolume() {
        return 1.5f;
    }

    @Override
    public void checkDespawn() {
        MobUtils.preventDespawnExceptPeaceful(this, level());
    }

    @Override
    public int getArmorValue() {
        return getTarget() != null ? super.getArmorValue() : 20;
    }

    @Override
    public void onSetPos(double x, double y, double z) {
        if (hitboxHelper != null) hitboxHelper.updatePosition();
    }

    @Override
    public void setNextDamagedPart(@Nullable String part) {
        hitboxHelper.setNextDamagedPart(part);
    }
}
