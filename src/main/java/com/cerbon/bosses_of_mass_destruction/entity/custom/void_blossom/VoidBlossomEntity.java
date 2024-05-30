package com.cerbon.bosses_of_mass_destruction.entity.custom.void_blossom;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.data.BooleanFlag;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.TimedEvent;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.MobUtils;
import com.cerbon.bosses_of_mass_destruction.api.multipart_entities.entity.EntityBounds;
import com.cerbon.bosses_of_mass_destruction.api.multipart_entities.entity.MultipartAwareEntity;
import com.cerbon.bosses_of_mass_destruction.api.multipart_entities.util.CompoundOrientedBox;
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
import com.cerbon.bosses_of_mass_destruction.util.BMDUtils;
import com.google.common.collect.Lists;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.BossInfo;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerBossInfo;
import net.minecraft.world.server.ServerWorld;
import software.bernie.geckolib3.core.manager.AnimationData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VoidBlossomEntity extends BaseEntity implements MultipartAwareEntity {
    private final AnimationHolder animationHolder;
    private final NetworkedHitboxManager hitboxHelper;

    public final VoidBlossomClientSpikeHandler clientSpikeHandler;
    public static final List<Float> hpMilestones = Lists.newArrayList(0.0f, 0.25f, 0.5f, 0.75f, 1.0f);

    public VoidBlossomEntity(EntityType<? extends CreatureEntity> entityType, World level, VoidBlossomConfig mobConfig) {
        super(entityType, level);
        noCulling = true;

        Map<Byte, AnimationHolder.Animation> animationStatusFlags = new HashMap<>();
        animationStatusFlags.put(VoidBlossomAttacks.spikeAttack, new AnimationHolder.Animation("spike", "idle"));
        animationStatusFlags.put(VoidBlossomAttacks.spikeWaveAttack, new AnimationHolder.Animation("spike_wave", "idle"));
        animationStatusFlags.put(VoidBlossomAttacks.sporeAttack, new AnimationHolder.Animation("spore", "idle"));
        animationStatusFlags.put(VoidBlossomAttacks.bladeAttack, new AnimationHolder.Animation("leaf_blade", "idle"));
        animationStatusFlags.put(VoidBlossomAttacks.blossomAction, new AnimationHolder.Animation("blossom", "idle"));
        animationStatusFlags.put(VoidBlossomAttacks.spawnAction, new AnimationHolder.Animation("spawn", "idle"));
        animationStatusFlags.put((byte) 3, new AnimationHolder.Animation("death", "idle"));
        this.animationHolder = new AnimationHolder(this, animationStatusFlags, VoidBlossomAttacks.stopAttackAnimation, 0);

        VoidBlossomHitboxes hitboxes = new VoidBlossomHitboxes(this);
        this.hitboxHelper = new NetworkedHitboxManager(this, hitboxes.getMap());
        entityEventHandler = new CompositeEntityEventHandler(animationHolder, new ClientSporeEffectHandler(this, preTickEvents), new ClientDeathEffectHandler(this, preTickEvents));
        DamageMemory damageMemory = new DamageMemory(10, this);
        TargetSwitcher targetSwitcher = new TargetSwitcher(this, damageMemory);

        BooleanFlag shouldSpawnBlossoms = new BooleanFlag();
        StagedDamageHandler hpDetector = new StagedDamageHandler(hpMilestones, shouldSpawnBlossoms::flag);

        bossBar = new ServerBossInfo(this.getDisplayName(), BossInfo.Color.GREEN, BossInfo.Overlay.NOTCHED_12);

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

        if (!level.isClientSide() && level instanceof ServerWorld){
            VoidBlossomAttacks attackHandler = new VoidBlossomAttacks(this, preTickEvents, shouldSpawnBlossoms::getAndReset, targetSwitcher);
            goalSelector.addGoal(2, new CompositeGoal());
            goalSelector.addGoal(1, new CompositeGoal(attackHandler.buildAttackGoal(), new ActionGoal(this::canContinueAttack, null, this::lookAtTarget, null, null)));

            targetSelector.addGoal(2, new FindTargetGoal<>(this, PlayerEntity.class, d -> getBoundingBox().inflate(d), 10, true, false, null));

            preTickEvents.addEvent(
                    new TimedEvent(
                            () -> playSound(BMDSounds.SPIKE_WAVE_INDICATOR.get(), 2.0f, 0.7f),
                            1
                    )
            );
        }else if (level.isClientSide())
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
    public void registerControllers(AnimationData data) {
        animationHolder.registerControllers(data);
    }

    @Override
    public void move(@Nonnull MoverType type, @Nonnull Vector3d movement) {
        super.move(type, new Vector3d(0.0, movement.y(), 0.0));
    }

    private boolean canContinueAttack(){
        return isAlive() && getTarget() != null;
    }

    @Override
    public boolean isOnFire() {
        return false;
    }

    @Override
    public CompoundOrientedBox getCompoundBoundingBox(AxisAlignedBB bounds) {
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
    protected SoundEvent getHurtSound(@Nonnull DamageSource damageSource) {
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
        BMDUtils.preventDespawnExceptPeaceful(this, level);
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
