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
import com.cerbon.bosses_of_mass_destruction.util.VanillaCopies;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;

import java.util.Map;

public class LichEntity extends BaseEntity {
    private final LichConfig mobConfig;
    private final AnimationHolder animationHolder;
    private final TeleportAction teleportAction;
    public HistoricalData<Vec3> velocityHistory;
    public boolean shouldSetToNighttime;
    public boolean collides;

    public LichEntity(EntityType<? extends LichEntity> entityType, Level level, LichConfig mobConfig) {
        super(entityType, level);
        this.mobConfig = mobConfig;

        noCulling = true;

        this.animationHolder = new AnimationHolder(this, Map.of(
                LichActions.endTeleport, new AnimationHolder.Animation("unteleport", "idle"),
                LichActions.cometRageAttack, new AnimationHolder.Animation("rage_mode", "idle"),
                LichActions.volleyRageAttack, new AnimationHolder.Animation("rage_mode", "idle"),
                LichActions.cometAttack, new AnimationHolder.Animation("summon_fireball", "idle"),
                LichActions.minionAttack, new AnimationHolder.Animation("summon_minions", "idle"),
                LichActions.minionRageAttack, new AnimationHolder.Animation("rage_mode", "idle"),
                LichActions.teleportAction, new AnimationHolder.Animation("teleport", "teleporting"),
                LichActions.volleyAttack, new AnimationHolder.Animation("summon_missiles", "idle"),
                (byte) 3, new AnimationHolder.Animation("idle", "idle")),
                LichActions.stopAttackAnimation, 0
        );

        MinionAction minionAction = new MinionAction(this, preTickEvents, this::cancelAttackAction);
        this.teleportAction = new TeleportAction(this, preTickEvents, this::cancelAttackAction);
        Map<Byte, IActionWithCooldown> statusRegistry = Map.of(
                LichActions.cometAttack, new CometAction(this, preTickEvents, this::cancelAttackAction, mobConfig),
                LichActions.volleyAttack, new VolleyAction(this, mobConfig, preTickEvents, this::cancelAttackAction),
                LichActions.minionAttack, minionAction,
                LichActions.minionRageAttack, new MinionRageAction(this, preTickEvents, this::cancelAttackAction, minionAction),
                LichActions.teleportAction, teleportAction,
                LichActions.cometRageAttack, new CometRageAction(this, preTickEvents, this::cancelAttackAction, mobConfig),
                LichActions.volleyRageAttack, new VolleyRageAction(this, mobConfig, preTickEvents, this::cancelAttackAction));
        DamageMemory damageMemory = new DamageMemory(5, this);
        LichMoveLogic moveLogic = new LichMoveLogic(statusRegistry, this, damageMemory);
        LichParticleHandler lichParticles = new LichParticleHandler(this, preTickEvents);

        this.shouldSetToNighttime = mobConfig.eternalNighttime;
        this.velocityHistory = new HistoricalData<>(Vec3.ZERO, 2);
        this.collides = true;

        CappedHeal cappedHeal = new CappedHeal(this, LichUtils.hpPercentRageModes, mobConfig.idleHealingPerTick);
        entityEventHandler = new CompositeEntityEventHandler(animationHolder, lichParticles);
        damageHandler = new CompositeDamageHandler(
                new StagedDamageHandler(LichUtils.hpPercentRageModes, () -> level.broadcastEntityEvent(this, LichActions.hpBelowThresholdStatus)),
                new DamagedAttackerNotSeen(this, livingEntity -> {if (livingEntity instanceof ServerPlayer) teleportAction.performTeleport((ServerPlayer) livingEntity);}),
                moveLogic, damageMemory
        );

        bossBar = new ServerBossEvent(this.getDisplayName(), BossEvent.BossBarColor.BLUE, BossEvent.BossBarOverlay.PROGRESS);
        serverTick = new CompositeEntityTick<>(cappedHeal, moveLogic);
        clientTick = lichParticles;

        if (!level.isClientSide){
            LichActions attackHelper = new LichActions(this, moveLogic);
            LichMovement moveHelper = new LichMovement(this);

            goalSelector.addGoal(1, new FloatGoal(this));
            goalSelector.addGoal(3, new CompositeGoal(moveHelper.buildAttackMovement(), attackHelper.buildAttackGoal()));
            goalSelector.addGoal(4, moveHelper.buildWanderGoal());

            targetSelector.addGoal(2, new FindTargetGoal<>(this, Player.class, d -> this.getBoundingBox().inflate(d), 10, true, false, null));
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        animationHolder.registerControllers(data);
        data.add(new AnimationController<>(this, "skull_float", 0, AnimationUtils.createIdlePredicate("skull_float")));
        data.add(new AnimationController<>(this, "float", 0, AnimationUtils.createIdlePredicate("float")));
        data.add(new AnimationController<>(this, "book_idle", 0, AnimationUtils.createIdlePredicate("book_idle")));
    }

    public boolean inLineOfSight(Entity target){
        boolean hasDirectLineOfSight = VanillaCopies.hasDirectLineOfSight(getEyePosition(), MobUtils.eyePos(target), level(), this);
        Vec3 directionToLich = MathUtils.unNormedDirection(MobUtils.eyePos(target), getEyePosition());
        boolean facingSameDirection = MathUtils.facingSameDirection(target.getLookAngle(), directionToLich);
        return hasDirectLineOfSight && facingSameDirection;
    }

    @Override
    public void clientTick() {
        velocityHistory.set(getDeltaMovement());
    }

    @Override
    public void serverTick(ServerLevel serverLevel) {
        if (shouldSetToNighttime)
            serverLevel.setDayTime(LichUtils.timeToNighttime(serverLevel.dayTime()));
    }

    @Override
    public boolean canCollideWith(@NotNull Entity entity) {
        return collides;
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, @NotNull DamageSource source) {
        return false;
    }

    @Override
    public @NotNull MobType getMobType() {
        return MobType.UNDEAD;
    }

    @Override
    public void checkDespawn() {
        BMDUtils.preventDespawnExceptPeaceful(this, level());
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(@NotNull DamageSource damageSource) {
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
    public void die(@NotNull DamageSource damageSource) {
        int expTicks = 18;
        int expPerTick = (int) (mobConfig.experienceDrop / (float) expTicks);
        preTickEvents.addEvent(
                new TimedEvent(
                        () -> VanillaCopies.awardExperience(expPerTick, MobUtils.eyePos(this), level()), 0,
                        expTicks,
                        () -> false
                )
        );

         level().getEntitiesOfClass(Phantom.class, new AABB(blockPosition()).inflate(100.0, 100.0, 100.0)).forEach(Phantom::kill);

         super.die(damageSource);
    }

    @Override
    protected void checkFallDamage(double y, boolean onGround, @NotNull BlockState state, @NotNull BlockPos pos) {}

    @Override
    public void travel(@NotNull Vec3 movementInput) {
        VanillaCopies.travel(movementInput, this, 0.91f);
    }
}