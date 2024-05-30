package com.cerbon.bosses_of_mass_destruction.entity.custom.lich;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.data.HistoricalData;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.TimedEvent;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.MathUtils;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.MobUtils;
import com.cerbon.bosses_of_mass_destruction.config.mob.LichConfig;
import com.cerbon.bosses_of_mass_destruction.entity.ai.action.IActionWithCooldown;
import com.cerbon.bosses_of_mass_destruction.entity.ai.goals.CompositeGoal;
import com.cerbon.bosses_of_mass_destruction.entity.ai.goals.FindTargetGoal;
import com.cerbon.bosses_of_mass_destruction.entity.custom.gauntlet.AnimationHolder;
import com.cerbon.bosses_of_mass_destruction.entity.custom.void_blossom.CappedHeal;
import com.cerbon.bosses_of_mass_destruction.entity.damage.CompositeDamageHandler;
import com.cerbon.bosses_of_mass_destruction.entity.damage.DamageMemory;
import com.cerbon.bosses_of_mass_destruction.entity.damage.DamagedAttackerNotSeen;
import com.cerbon.bosses_of_mass_destruction.entity.damage.StagedDamageHandler;
import com.cerbon.bosses_of_mass_destruction.entity.util.BaseEntity;
import com.cerbon.bosses_of_mass_destruction.entity.util.CompositeEntityEventHandler;
import com.cerbon.bosses_of_mass_destruction.entity.util.CompositeEntityTick;
import com.cerbon.bosses_of_mass_destruction.sound.BMDSounds;
import com.cerbon.bosses_of_mass_destruction.util.AnimationUtils;
import com.cerbon.bosses_of_mass_destruction.util.BMDUtils;
import com.cerbon.bosses_of_mass_destruction.util.VanillaCopiesServer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.monster.PhantomEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.BossInfo;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerBossInfo;
import net.minecraft.world.server.ServerWorld;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.manager.AnimationData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class LichEntity extends BaseEntity {
    private final LichConfig mobConfig;
    private final AnimationHolder animationHolder;
    private final TeleportAction teleportAction;
    public final HistoricalData<Vector3d> velocityHistory;
    public final boolean shouldSetToNighttime;
    public boolean collides;

    public LichEntity(EntityType<? extends LichEntity> entityType, World level, LichConfig mobConfig) {
        super(entityType, level);
        this.mobConfig = mobConfig;

        noCulling = true;

        Map<Byte, AnimationHolder.Animation> animationStatusFlags = new HashMap<>();
        animationStatusFlags.put(LichActions.endTeleport, new AnimationHolder.Animation("unteleport", "idle"));
        animationStatusFlags.put(LichActions.cometRageAttack, new AnimationHolder.Animation("rage_mode", "idle"));
        animationStatusFlags.put(LichActions.volleyRageAttack, new AnimationHolder.Animation("rage_mode", "idle"));
        animationStatusFlags.put(LichActions.cometAttack, new AnimationHolder.Animation("summon_fireball", "idle"));
        animationStatusFlags.put(LichActions.minionAttack, new AnimationHolder.Animation("summon_minions", "idle"));
        animationStatusFlags.put(LichActions.minionRageAttack, new AnimationHolder.Animation("rage_mode", "idle"));
        animationStatusFlags.put(LichActions.teleportAction, new AnimationHolder.Animation("teleport", "teleporting"));
        animationStatusFlags.put(LichActions.volleyAttack, new AnimationHolder.Animation("summon_missiles", "idle"));
        animationStatusFlags.put((byte) 3, new AnimationHolder.Animation("idle", "idle"));

        this.animationHolder = new AnimationHolder(this, animationStatusFlags, LichActions.stopAttackAnimation, 0);

        MinionAction minionAction = new MinionAction(this, preTickEvents, this::cancelAttackAction);
        this.teleportAction = new TeleportAction(this, preTickEvents, this::cancelAttackAction);
        Map<Byte, IActionWithCooldown> statusRegistry = new HashMap<>();
        statusRegistry.put(LichActions.cometAttack, new CometAction(this, preTickEvents, this::cancelAttackAction, mobConfig));
        statusRegistry.put(LichActions.volleyAttack, new VolleyAction(this, mobConfig, preTickEvents, this::cancelAttackAction));
        statusRegistry.put(LichActions.minionAttack, minionAction);
        statusRegistry.put(LichActions.minionRageAttack, new MinionRageAction(this, preTickEvents, this::cancelAttackAction, minionAction));
        statusRegistry.put(LichActions.teleportAction, teleportAction);
        statusRegistry.put(LichActions.cometRageAttack, new CometRageAction(this, preTickEvents, this::cancelAttackAction, mobConfig));
        statusRegistry.put(LichActions.volleyRageAttack, new VolleyRageAction(this, mobConfig, preTickEvents, this::cancelAttackAction));

        DamageMemory damageMemory = new DamageMemory(5, this);
        LichMoveLogic moveLogic = new LichMoveLogic(statusRegistry, this, damageMemory);
        LichParticleHandler lichParticles = new LichParticleHandler(this, preTickEvents);

        this.shouldSetToNighttime = mobConfig.eternalNighttime;
        this.velocityHistory = new HistoricalData<>(Vector3d.ZERO, 2);
        this.collides = true;

        CappedHeal cappedHeal = new CappedHeal(this, LichUtils.hpPercentRageModes, mobConfig.idleHealingPerTick);
        entityEventHandler = new CompositeEntityEventHandler(animationHolder, lichParticles);
        damageHandler = new CompositeDamageHandler(
                new StagedDamageHandler(LichUtils.hpPercentRageModes, () -> level.broadcastEntityEvent(this, LichActions.hpBelowThresholdStatus)),
                new DamagedAttackerNotSeen(this, livingEntity -> {if (livingEntity instanceof ServerPlayerEntity) teleportAction.performTeleport((ServerPlayerEntity) livingEntity);}),
                moveLogic, damageMemory
        );

        bossBar = new ServerBossInfo(this.getDisplayName(), BossInfo.Color.BLUE, BossInfo.Overlay.PROGRESS);
        serverTick = new CompositeEntityTick<>(cappedHeal, moveLogic);
        clientTick = lichParticles;

        if (!level.isClientSide){
            LichActions attackHelper = new LichActions(this, moveLogic);
            LichMovement moveHelper = new LichMovement(this);

            goalSelector.addGoal(1, new SwimGoal(this));
            goalSelector.addGoal(3, new CompositeGoal(moveHelper.buildAttackMovement(), attackHelper.buildAttackGoal()));
            goalSelector.addGoal(4, moveHelper.buildWanderGoal());

            targetSelector.addGoal(2, new FindTargetGoal<>(this, PlayerEntity.class, d -> this.getBoundingBox().inflate(d), 10, true, false, null));
        }
    }

    @Override
    public void registerControllers(AnimationData data) {
        animationHolder.registerControllers(data);
        data.addAnimationController(new AnimationController<>(this, "skull_float", 0, AnimationUtils.createIdlePredicate("skull_float")));
        data.addAnimationController(new AnimationController<>(this, "float", 0, AnimationUtils.createIdlePredicate("float")));
        data.addAnimationController(new AnimationController<>(this, "book_idle", 0, AnimationUtils.createIdlePredicate("book_idle")));
    }

    public boolean inLineOfSight(Entity target){
        boolean hasDirectLineOfSight = VanillaCopiesServer.hasDirectLineOfSight(getEyePosition(1.0f), MobUtils.eyePos(target), level, this);
        Vector3d directionToLich = MathUtils.unNormedDirection(MobUtils.eyePos(target), getEyePosition(1.0f));
        boolean facingSameDirection = MathUtils.facingSameDirection(target.getLookAngle(), directionToLich);
        return hasDirectLineOfSight && facingSameDirection;
    }

    @Override
    public void clientTick() {
        velocityHistory.set(getDeltaMovement());
    }

    @Override
    public void serverTick(ServerWorld serverLevel) {
        if (shouldSetToNighttime)
            serverLevel.setDayTime(LichUtils.timeToNighttime(serverLevel.dayTime()));
    }

    @Override
    public boolean canCollideWith(@Nonnull Entity entity) {
        return collides;
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier) {
        return false;
    }

    @Override
    public @Nonnull CreatureAttribute getMobType() {
        return CreatureAttribute.UNDEAD;
    }

    @Override
    public void checkDespawn() {
        BMDUtils.preventDespawnExceptPeaceful(this, level);
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(@Nonnull DamageSource damageSource) {
        return BMDSounds.LICH_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return BMDSounds.LICH_DEATH.get();
    }

    @Override
    protected float getSoundVolume() {
        return 5.0f;
    }

    @Override
    public boolean onClimbable() {
        return false;
    }

    private boolean cancelAttackAction(){
        return isDeadOrDying() || getTarget() == null;
    }

    @Override
    public void die(@Nonnull DamageSource damageSource) {
        int expTicks = 18;
        int expPerTick = (int) (mobConfig.experienceDrop / (float) expTicks);
        preTickEvents.addEvent(
                new TimedEvent(
                        () -> VanillaCopiesServer.awardExperience(expPerTick, MobUtils.eyePos(this), level), 0,
                        expTicks,
                        () -> false
                )
        );

         level.getEntitiesOfClass(PhantomEntity.class, new AxisAlignedBB(blockPosition()).inflate(100.0, 100.0, 100.0)).forEach(PhantomEntity::kill);

         super.die(damageSource);
    }

    @Override
    protected void checkFallDamage(double y, boolean onGround, @Nonnull BlockState state, @Nonnull BlockPos pos) {}

    @Override
    public void travel(@Nonnull Vector3d movementInput) {
        VanillaCopiesServer.travel(movementInput, this, 0.91f);
    }
}