package com.cerbon.bosses_of_mass_destruction.entity.custom.obsidilith;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.TimedEvent;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.MathUtils;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.MobUtils;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.VecUtils;
import com.cerbon.bosses_of_mass_destruction.block.BMDBlocks;
import com.cerbon.bosses_of_mass_destruction.capability.util.BMDCapabilities;
import com.cerbon.bosses_of_mass_destruction.config.mob.ObsidilithConfig;
import com.cerbon.bosses_of_mass_destruction.entity.ai.action.CooldownAction;
import com.cerbon.bosses_of_mass_destruction.entity.ai.action.IActionWithCooldown;
import com.cerbon.bosses_of_mass_destruction.entity.ai.goals.ActionGoal;
import com.cerbon.bosses_of_mass_destruction.entity.ai.goals.FindTargetGoal;
import com.cerbon.bosses_of_mass_destruction.entity.custom.void_blossom.CappedHeal;
import com.cerbon.bosses_of_mass_destruction.entity.damage.CompositeDamageHandler;
import com.cerbon.bosses_of_mass_destruction.entity.damage.DamageMemory;
import com.cerbon.bosses_of_mass_destruction.entity.util.BaseEntity;
import com.cerbon.bosses_of_mass_destruction.entity.util.EffectsImmunity;
import com.cerbon.bosses_of_mass_destruction.entity.util.animation.AnimationPredicate;
import com.cerbon.bosses_of_mass_destruction.particle.BMDParticles;
import com.cerbon.bosses_of_mass_destruction.sound.BMDSounds;
import com.cerbon.bosses_of_mass_destruction.util.BMDUtils;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.BossInfo;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerBossInfo;
import net.minecraft.world.server.ServerWorld;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.manager.AnimationData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.IntStream;

public class ObsidilithEntity extends BaseEntity {
    private final ObsidilithConfig mobConfig;
    private final Map<Byte, IActionWithCooldown> statusRegistry;
    private final ObsidilithMoveLogic moveLogic;
    private final ObsidilithEffectHandler effectHandler;

    private final List<BlockPos> activePillars = new ArrayList<>();

    public byte currentAttack = 0;

    public ObsidilithEntity(EntityType<? extends CreatureEntity> entityType, World level, ObsidilithConfig mobConfig) {
        super(entityType, level);
        this.mobConfig = mobConfig;

        noCulling = true;
        this.statusRegistry = new HashMap<>();
        statusRegistry.put(ObsidilithUtils.burstAttackStatus, new BurstAction(this));
        statusRegistry.put(ObsidilithUtils.waveAttackStatus, new WaveAction(this));
        statusRegistry.put(ObsidilithUtils.spikeAttackStatus, new SpikeAction(this));
        statusRegistry.put(ObsidilithUtils.anvilAttackStatus, new AnvilAction(this, mobConfig.anvilAttackExplosionStrength));
        statusRegistry.put(ObsidilithUtils.pillarDefenseStatus, new PillarAction(this));

        DamageMemory damageMemory = new DamageMemory(10, this);
        this.moveLogic = new ObsidilithMoveLogic(statusRegistry, this, damageMemory);
        this.effectHandler = new ObsidilithEffectHandler(this, BMDCapabilities.getLevelEventScheduler(level));

        bossBar = new ServerBossInfo(this.getDisplayName(), BossInfo.Color.PURPLE, BossInfo.Overlay.NOTCHED_12);
        damageHandler = new CompositeDamageHandler(moveLogic, new ShieldDamageHandler(this::isShielded), damageMemory);
        mobEffectHandler = new EffectsImmunity(Effects.WITHER, Effects.POISON);
        serverTick = new CappedHeal(this, ObsidilithUtils.hpPillarShieldMilestones, mobConfig.idleHealingPerTick);

        if (!level.isClientSide()) {
            goalSelector.addGoal(1, buildAttackGoal());

            targetSelector.addGoal(2, new FindTargetGoal<>(this, PlayerEntity.class, d -> getBoundingBox().inflate(d), 10, true, false, null));

            preTickEvents.addEvent(
                    new TimedEvent(
                            () -> BMDUtils.playSound((ServerWorld) level, position(), BMDSounds.WAVE_INDICATOR.get(), SoundCategory.HOSTILE, 1.5f, 0.7f, 24, null),
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
    public void serverTick(ServerWorld serverLevel) {
        super.serverTick(serverLevel);

        activePillars.removeIf(
                pos -> level.getBlockState(pos).getBlock() != BMDBlocks.OBSIDILITH_RUNE.get()
                || !pos.closerThan(blockPosition(), 64));

        getEntityData().set(ObsidilithUtils.isShielded, !activePillars.isEmpty());

        if (this.tickCount % 40 == 0){
            if (!activePillars.isEmpty()){
                BlockPos pos = activePillars.get(new Random().nextInt(activePillars.size()));
                MathUtils.lineCallback(VecUtils.asVec3(pos).add(0.5, 0.5, 0.5), MobUtils.eyePos(this), 15, (vec3, i) ->
                        preTickEvents.addEvent(
                                new TimedEvent(
                                        () -> BMDUtils.spawnParticle(serverLevel, BMDParticles.PILLAR_RUNE.get(), vec3, Vector3d.ZERO, 0, 0.0),
                                        i
                                )
                        ));
            }
        }
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public void push(@Nonnull Entity entity) {}

    @Override
    public void die(@Nonnull DamageSource damageSource) {
        if (mobConfig.spawnPillarOnDeath) {
            ObsidilithUtils.onDeath(this, mobConfig.experienceDrop);
            if (level.isClientSide)
                effectHandler.handleStatus(ObsidilithUtils.deathStatus);
        }
        super.die(damageSource);
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
    protected SoundEvent getHurtSound(@Nonnull DamageSource damageSource) {
        return BMDSounds.OBSIDILITH_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return BMDSounds.OBSIDILITH_DEATH.get();
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "summon", 0, new AnimationPredicate<>(animationState -> {
            animationState.getController().setAnimation(
                    new AnimationBuilder().addAnimation("summon", false)
            );
            return PlayState.CONTINUE;
        })));
    }

    @Override
    public void move(@Nonnull MoverType type, @Nonnull Vector3d movement) {
        super.move(type, new Vector3d(0.0, movement.y, 0.0));
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
        BMDUtils.preventDespawnExceptPeaceful(this, level);
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier) {
        return false;
    }

    public boolean isShielded(){
        return getEntityData().get(ObsidilithUtils.isShielded);
    }

    public void addActivePillar(BlockPos pos){
        activePillars.add(pos);
    }

    @Override
    public @Nonnull CompoundNBT saveWithoutId(@Nonnull CompoundNBT compound) {
        int[] activePillarsArray = activePillars.stream()
                .flatMapToInt(p -> IntStream.of(p.getX(), p.getY(), p.getZ()))
                .toArray();
        compound.putIntArray("activePillars", activePillarsArray);
        return super.saveWithoutId(compound);
    }

    @Override
    public void load(@Nonnull CompoundNBT compound) {
        super.load(compound);
        if (compound.contains("activePillars")) {
            int[] activePillarsArray = compound.getIntArray("activePillars");
            for (int i = 0; i < activePillarsArray.length; i += 3) {
                BlockPos pos = new BlockPos(activePillarsArray[i], activePillarsArray[i + 1], activePillarsArray[i + 2]);
                activePillars.add(pos);
            }
        }
    }
}
