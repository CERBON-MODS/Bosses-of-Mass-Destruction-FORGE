package com.cerbon.bosses_of_mass_destruction.entity.util;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.EventScheduler;
import com.cerbon.bosses_of_mass_destruction.entity.damage.IDamageHandler;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerBossInfo;
import net.minecraft.world.server.ServerWorld;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.IAnimationTickable;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class BaseEntity extends CreatureEntity implements IAnimatable, IAnimationTickable {
    public Vector3d idlePosition = Vector3d.ZERO;
    protected ServerBossInfo bossBar;
    protected IDamageHandler damageHandler;
    protected IEntityEventHandler entityEventHandler;
    protected IEntityTick<World> clientTick;
    protected IEntityTick<ServerWorld> serverTick;
    protected IDataAccessorHandler dataAccessorHandler;
    protected IMobEffectFilter mobEffectHandler;
    protected IMoveHandler moveHandler;
    protected INbtHandler nbtHandler;
    protected IEntityTick<World> deathClientTick;
    protected IEntityTick<ServerWorld> deathServerTick;
    protected final EventScheduler preTickEvents = new EventScheduler();
    protected final EventScheduler postTickEvents = new EventScheduler();
    protected AnimationFactory animationFactory;

    public BaseEntity(EntityType<? extends CreatureEntity> entityType, World level) {
        super(entityType, level);
    }

    @Override
    public void tick() {
        preTickEvents.updateEvents();

        if (idlePosition == Vector3d.ZERO)
            idlePosition = position();

        if (level.isClientSide()){
            clientTick();

            if (clientTick != null)
                clientTick.tick(level);

        }else if (level instanceof ServerWorld){
            serverTick((ServerWorld) level);

            if (serverTick != null)
                serverTick.tick((ServerWorld) level);
        }

        super.tick();

        postTickEvents.updateEvents();
    }

    @Override
    protected void tickDeath() {
        if (level.isClientSide() && deathClientTick != null)
            deathClientTick.tick(level);

        else if (level instanceof ServerWorld && deathServerTick != null)
            deathServerTick.tick((ServerWorld) level);

        else {
            ++deathTime;
            if (deathTime >= 20 && !level.isClientSide() && !removed) {
                level.broadcastEntityEvent(this, (byte) 60);
                this.remove();
            }
        }
    }

    public void clientTick(){}

    public void serverTick(ServerWorld serverLevel){}

    @Override
    public void aiStep() {
        super.aiStep();
        getSensing().tick();
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        if (bossBar != null)
            bossBar.setPercent(this.getHealth() / this.getMaxHealth());
    }

    @Override
    public void load(@Nonnull CompoundNBT compound) {
        super.load(compound);
        if (hasCustomName() && bossBar != null)
            bossBar.setName(this.getDisplayName());

        if (nbtHandler != null)
            nbtHandler.fromTag(compound);
    }

    @Override
    public void setCustomName(@Nullable ITextComponent name) {
        super.setCustomName(name);
        if (bossBar != null)
            bossBar.setName(this.getDisplayName());
    }

    @Override
    public void startSeenByPlayer(@Nonnull ServerPlayerEntity serverPlayer) {
        super.startSeenByPlayer(serverPlayer);
        if (bossBar != null)
            bossBar.addPlayer(serverPlayer);
    }

    @Override
    public void stopSeenByPlayer(@Nonnull ServerPlayerEntity serverPlayer) {
        super.stopSeenByPlayer(serverPlayer);
        if (bossBar != null)
            bossBar.removePlayer(serverPlayer);
    }

    @Override
    public void readAdditionalSaveData(@Nonnull CompoundNBT compound) {
        super.readAdditionalSaveData(compound);
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (entityEventHandler != null)
            entityEventHandler.handleEntityEvent(id);

        super.handleEntityEvent(id);
    }

    @Override
    public void onSyncedDataUpdated(@Nonnull DataParameter<?> data) {
        super.onSyncedDataUpdated(data);
        if (dataAccessorHandler != null)
            dataAccessorHandler.onSyncedDataUpdated(data);
    }

    @Override
    public boolean canBeAffected(@Nonnull EffectInstance effectInstance) {
        return mobEffectHandler != null ? mobEffectHandler.canBeAffected(effectInstance) : super.canBeAffected(effectInstance);
    }

    @Override
    public boolean hurt(@Nonnull DamageSource source, float amount) {
        EntityStats stats = new EntityStats(this);
        IDamageHandler handler = damageHandler;

        if (!level.isClientSide() && handler != null)
            handler.beforeDamage(stats, source, amount);

        boolean result = handler != null
                ? handler.shouldDamage(this, source, amount) && super.hurt(source, amount)
                : super.hurt(source, amount);

        if (!level.isClientSide() && handler != null)
            handler.afterDamage(stats, source, amount, result);

        return result;
    }

    @Override
    public void setTarget(@Nullable LivingEntity target) {
        if (target == null) idlePosition = position();
        super.setTarget(target);
    }

    @Override
    public void move(@Nonnull MoverType type, @Nonnull Vector3d movement) {
        boolean shouldDoDefault = moveHandler != null && moveHandler.canMove(type, movement);
        if (moveHandler == null || shouldDoDefault)
            super.move(type, movement);
    }

    @Override
    public @Nonnull CompoundNBT saveWithoutId(@Nonnull CompoundNBT compound) {
        CompoundNBT superCompound = super.saveWithoutId(compound);
        return nbtHandler != null ? nbtHandler.toTag(superCompound) : superCompound;
    }

    @Override
    public AnimationFactory getFactory() {
        if (animationFactory == null)
            animationFactory = new AnimationFactory(this);

        return animationFactory;
    }

    @Override
    public int tickTimer() {
        return tickCount;
    }

    public Vector3d safeGetTargetPos(){
        LivingEntity target = getTarget();
        return target == null ? Vector3d.ZERO : target.position();
    }
}

