package com.cerbon.bosses_of_mass_destruction.entity.custom.obsidilith;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.TimedEvent;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.MathUtils;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.MobUtils;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.VecUtils;
import com.cerbon.bosses_of_mass_destruction.capability.util.BMDCapabilities;
import com.cerbon.bosses_of_mass_destruction.config.mob.ObsidilithConfig;
import com.cerbon.bosses_of_mass_destruction.entity.BMDEntities;
import com.cerbon.bosses_of_mass_destruction.entity.ai.action.CooldownAction;
import com.cerbon.bosses_of_mass_destruction.entity.ai.action.IActionWithCooldown;
import com.cerbon.bosses_of_mass_destruction.entity.ai.goals.ActionGoal;
import com.cerbon.bosses_of_mass_destruction.entity.ai.goals.FindTargetGoal;
import com.cerbon.bosses_of_mass_destruction.entity.custom.void_blossom.CappedHeal;
import com.cerbon.bosses_of_mass_destruction.entity.damage.CompositeDamageHandler;
import com.cerbon.bosses_of_mass_destruction.entity.damage.DamageMemory;
import com.cerbon.bosses_of_mass_destruction.entity.util.BaseEntity;
import com.cerbon.bosses_of_mass_destruction.entity.util.EffectsImmunity;
import com.cerbon.bosses_of_mass_destruction.particle.BMDParticles;
import com.cerbon.bosses_of_mass_destruction.sound.BMDSounds;
import com.cerbon.bosses_of_mass_destruction.util.BMDUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class ObsidilithEntity extends BaseEntity {
    private final ObsidilithConfig mobConfig = BMDEntities.mobConfig.obsidilithConfig;

    private final Map<Byte, IActionWithCooldown> statusRegistry = Map.of(
            ObsidilithUtils.burstAttackStatus, new BurstAction(this),
            ObsidilithUtils.waveAttackStatus, new WaveAction(this),
            ObsidilithUtils.spikeAttackStatus, new SpikeAction(this),
            ObsidilithUtils.anvilAttackStatus, new AnvilAction(this, mobConfig.anvilAttackExplosionStrength),
            ObsidilithUtils.pillarDefenseStatus, new PillarAction(this)
    );

    private final DamageMemory damageMemory = new DamageMemory(10, this);
    private final ObsidilithMoveLogic moveLogic = new ObsidilithMoveLogic(statusRegistry, this, damageMemory);
    private final ObsidilithEffectHandler effectHandler;

    private final List<BlockPos> activePillars = new ArrayList<>();

    public byte currentAttack = 0;

    public ObsidilithEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
        noCulling = true;

        this.effectHandler = new ObsidilithEffectHandler(this, BMDCapabilities.getLevelEventScheduler(level));

        bossBar = new ServerBossEvent(this.getDisplayName(), BossEvent.BossBarColor.PURPLE, BossEvent.BossBarOverlay.NOTCHED_12);
        damageHandler = new CompositeDamageHandler(moveLogic, new ShieldDamageHandler(this::isShielded), damageMemory);
        mobEffectHandler = new EffectsImmunity(MobEffects.WITHER, MobEffects.POISON);
        serverTick = new CappedHeal(this, ObsidilithUtils.hpPillarShieldMilestones, mobConfig.idleHealingPerTick);

        if (!level.isClientSide()) {
            goalSelector.addGoal(1, buildAttackGoal());

            targetSelector.addGoal(2, new FindTargetGoal<>(this, Player.class, d -> getBoundingBox().inflate(d), 10, true, false, null));

            preTickEvents.addEvent(
                    new TimedEvent(
                            () -> BMDUtils.playSound((ServerLevel) level, position(), BMDSounds.WAVE_INDICATOR.get(), SoundSource.HOSTILE, 1.5f, 0.7f, 24, null),
                            1
                    )
            );
        }
        entityData.define(ObsidilithUtils.isShielded, false);
    }

    private ActionGoal buildAttackGoal(){
        CooldownAction attackAction = new CooldownAction(moveLogic, 80);
        return new ActionGoal(
                this::canContinueAttack,
                null,
                attackAction,
                null,
                attackAction
        );
    }

    @Override
    public void serverTick(ServerLevel serverLevel) {
        super.serverTick(serverLevel);

        activePillars.removeIf(pos -> level().getBlockState(pos).getBlock() != Blocks.OAK_PLANKS //TODO: Change to obsidilith rune
                || !pos.closerThan(blockPosition(), 64));

        getEntityData().set(ObsidilithUtils.isShielded, !activePillars.isEmpty());

        if (this.tickCount % 40 == 0){
            activePillars.stream().findAny().ifPresent(
                    pos -> MathUtils.lineCallback(VecUtils.asVec3(pos).add(0.5, 0.5, 0.5), MobUtils.eyePos(this), 15,
                            (vec3, i) -> preTickEvents.addEvent(
                                    new TimedEvent(
                                            () -> BMDUtils.spawnParticle(serverLevel, BMDParticles.PILLAR_RUNE.get(), vec3, Vec3.ZERO, 0, 0.0),
                                            i
                                    )
                            ))
            );
        }
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public void push(@NotNull Entity entity) {}

    @Override
    public void die(@NotNull DamageSource damageSource) {
        if (mobConfig.spawnPillarOnDeath) {
            ObsidilithUtils.onDeath(this, mobConfig.experienceDrop);
            if (level().isClientSide)
                effectHandler.handleStatus(ObsidilithUtils.deathStatus);
        }

    }

    public boolean canContinueAttack(){
        return isAlive() && getTarget() != null;
    }

    @Override
    public void handleEntityEvent(byte status) {
        IActionWithCooldown attackStatus = statusRegistry.get(status);
        if (attackStatus != null){
            effectHandler.handleStatus(status);
            currentAttack = status;
            preTickEvents.addEvent(new TimedEvent(() -> currentAttack = 0, 40));
        }
        super.handleEntityEvent(status);
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(@NotNull DamageSource damageSource) {
        return BMDSounds.OBSIDILITH_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return BMDSounds.OBSIDILITH_DEATH.get();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        data.add(new AnimationController<GeoAnimatable>(this, "summon", 0, animationState -> {
            animationState.setAnimation(RawAnimation.begin().thenPlay("summon"));
            return PlayState.CONTINUE;
        }));
    }

    @Override
    public void move(@NotNull MoverType type, @NotNull Vec3 movement) {
        super.move(type, new Vec3(0.0, movement.y, 0.0));
    }

    @Override
    public boolean isOnFire() {
        return false;
    }

    @Override
    public int getArmorValue() {
        return getTarget() != null ? super.getArmorValue() : 24;
    }

    @Override
    public void checkDespawn() {
        BMDUtils.preventDespawnExceptPeaceful(this, level());
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, @NotNull DamageSource source) {
        return false;
    }

    public boolean isShielded(){
        return getEntityData().get(ObsidilithUtils.isShielded);
    }

    public void addActivePillar(BlockPos pos){
        activePillars.add(pos);
    }

    @Override
    public @NotNull CompoundTag saveWithoutId(@NotNull CompoundTag compound) {
        int[] activePillarsArray = activePillars.stream()
                .flatMapToInt(p -> IntStream.of(p.getX(), p.getY(), p.getZ()))
                .toArray();
        compound.putIntArray(activePillars.toString(), activePillarsArray);
        return super.saveWithoutId(compound);
    }

    @Override
    public void load(@NotNull CompoundTag compound) {
        super.load(compound);
        if (compound.contains(activePillars.toString())) {
            int[] activePillarsArray = compound.getIntArray(activePillars.toString());
            for (int i = 0; i < activePillarsArray.length; i += 3) {
                BlockPos pos = new BlockPos(activePillarsArray[i], activePillarsArray[i + 1], activePillarsArray[i + 2]);
                activePillars.add(pos);
            }
        }
    }
}
