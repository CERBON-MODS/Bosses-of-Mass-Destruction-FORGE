package com.cerbon.bosses_of_mass_destruction.entity.util;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.EventScheduler;
import com.cerbon.bosses_of_mass_destruction.entity.damage.IDamageHandler;
import com.google.errorprone.annotations.ForOverride;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.util.GeckoLibUtil;

public abstract class BaseEntity extends PathfinderMob implements GeoEntity {
    private final EntityType<? extends PathfinderMob> entityType;
    private final Level level;
    private AnimatableInstanceCache animationFactory = null;
    protected Vec3 idlePosition = Vec3.ZERO;
    protected ServerBossEvent bossBar = null;
    protected IDamageHandler damageHandler = null;
    protected IEntityEventHandler entityEventHandler = null;
    protected IEntityTick<ClientLevel> clientTick = null;
    protected IEntityTick<ServerLevel> serverTick = null;
    protected IDataAccessorHandler dataAccessorHandler = null;
    protected IMobEffectFilter mobEffectHandler = null;
    protected IMoveHandler moveHandler = null;
    protected INbtHandler nbtHandler = null;
    protected IEntityTick<ClientLevel> deathClientTick = null;
    protected IEntityTick<ServerLevel> deathServerTick = null;
    protected EventScheduler preTickEvents = new EventScheduler();
    protected EventScheduler postTickEvents = new EventScheduler();

    public BaseEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
        this.entityType = entityType;
        this.level = level;
    }

    @Override
    public void tick() {
        preTickEvents.updateEvents();
        if (idlePosition == Vec3.ZERO)
            idlePosition = position();

        Level sideLevel = level();
        if (sideLevel.isClientSide() && sideLevel instanceof ClientLevel clientLevel){
            clientTick();
            if (clientTick != null)
                clientTick.tick(clientLevel);
        }else if (sideLevel instanceof ServerLevel serverLevel){
            serverTick(serverLevel);
            if (serverTick != null)
                serverTick.tick(serverLevel);
        }

        super.tick();
        postTickEvents.updateEvents();
    }

    @Override
    protected void tickDeath() {
        Level sideLevel = level();
        if (sideLevel.isClientSide() && sideLevel instanceof ClientLevel clientLevel && deathClientTick != null)
            deathClientTick.tick(clientLevel);
        else if (sideLevel instanceof ServerLevel serverLevel && deathServerTick != null)
            deathServerTick.tick(serverLevel);
        else
            super.tickDeath();
    }

    @ForOverride
    public void clientTick(){}

    @ForOverride
    public void serverTick(ServerLevel serverLevel){}

    @Override
    public void aiStep() {
        super.aiStep();
        getSensing().tick();
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        if (bossBar != null)
            bossBar.setProgress(this.getHealth() / this.getMaxHealth());
    }

    @Override
    public void load(@NotNull CompoundTag compound) {
        super.load(compound);
        if (hasCustomName() && bossBar != null)
            bossBar.setName(this.getDisplayName());
    }

    @Override
    public void setCustomName(@Nullable Component name) {
        super.setCustomName(name);
        if (bossBar != null)
            bossBar.setName(this.getDisplayName());
    }

    @Override
    public void startSeenByPlayer(@NotNull ServerPlayer serverPlayer) {
        super.startSeenByPlayer(serverPlayer);
        if (bossBar != null)
            bossBar.addPlayer(serverPlayer);
    }

    @Override
    public void stopSeenByPlayer(@NotNull ServerPlayer serverPlayer) {
        super.stopSeenByPlayer(serverPlayer);
        if (bossBar != null)
            bossBar.removePlayer(serverPlayer);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (entityEventHandler != null)
            entityEventHandler.handleEntityEvent(id);
        super.handleEntityEvent(id);
    }

    @Override
    public void onSyncedDataUpdated(@NotNull EntityDataAccessor<?> data) {
        super.onSyncedDataUpdated(data);
        if (dataAccessorHandler != null)
            dataAccessorHandler.onSyncedDataUpdated(data);
    }

    @Override
    public boolean canBeAffected(@NotNull MobEffectInstance effectInstance) {
        IMobEffectFilter mobEffectHandler1 = mobEffectHandler;
        if (mobEffectHandler1 != null)
            return mobEffectHandler1.canBeAffected(effectInstance);
        return super.canBeAffected(effectInstance);
    }

    @Override
    public boolean hurt(@NotNull DamageSource source, float amount) {
        EntityStats stats = new EntityStats(this);
        IDamageHandler handler = damageHandler;
        if (!level().isClientSide() && handler != null)
            handler.beforeDamage(stats, source, amount);

        boolean result = handler != null
                ? handler.shouldDamage(this, source, amount) && super.hurt(source, amount)
                : super.hurt(source, amount);

        if (!level().isClientSide() && handler != null)
            handler.afterDamage(stats, source, amount, result);

        return result;
    }

    @Override
    public void setTarget(@Nullable LivingEntity target) {
        if (target == null) idlePosition = position();
        super.setTarget(target);
    }

    @Override
    public void move(@NotNull MoverType type, @NotNull Vec3 movement) {
        boolean shouldDoDefault = moveHandler != null && moveHandler.canMove(type, movement);
        if (moveHandler == null || shouldDoDefault)
            super.move(type, movement);
    }

    @Override
    public @NotNull CompoundTag saveWithoutId(@NotNull CompoundTag compound) {
        CompoundTag superCompound = super.saveWithoutId(compound);
        return nbtHandler != null ? nbtHandler.toTag(compound) : superCompound;
    }

    public Vec3 safeGetTargetPos(){
        LivingEntity target = getTarget();
        return target == null ? Vec3.ZERO : target.position();
    }

    public AnimatableInstanceCache getAnimatableInstanceCache() {
        if (animationFactory == null) {
            animationFactory = GeckoLibUtil.createInstanceCache(this);
        }
        return animationFactory;
    }
}

