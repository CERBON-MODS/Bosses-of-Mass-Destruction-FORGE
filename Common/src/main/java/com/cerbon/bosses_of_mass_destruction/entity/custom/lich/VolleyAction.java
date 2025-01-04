package com.cerbon.bosses_of_mass_destruction.entity.custom.lich;

import com.cerbon.bosses_of_mass_destruction.config.mob.LichConfig;
import com.cerbon.bosses_of_mass_destruction.entity.ai.action.IActionWithCooldown;
import com.cerbon.bosses_of_mass_destruction.entity.util.ProjectileThrower;
import com.cerbon.bosses_of_mass_destruction.projectile.MagicMissileProjectile;
import com.cerbon.bosses_of_mass_destruction.sound.BMDSounds;
import com.cerbon.cerbons_api.api.general.event.EventScheduler;
import com.cerbon.cerbons_api.api.general.event.TimedEvent;
import com.cerbon.cerbons_api.api.static_utilities.MathUtils;
import com.cerbon.cerbons_api.api.static_utilities.MobUtils;
import com.cerbon.cerbons_api.api.static_utilities.SoundUtils;
import com.cerbon.cerbons_api.api.static_utilities.VecUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class VolleyAction implements IActionWithCooldown {
    private final LichEntity entity;
    private final LichConfig mobConfig;
    private final EventScheduler eventScheduler;
    private final Supplier<Boolean> shouldCancel;

    public static final int missileThrowDelay = 46;
    public static final int missileThrowCooldown = 80;
    public static final int missileParticleSummonDelay = 16;

    public VolleyAction(LichEntity entity, LichConfig mobConfig, EventScheduler eventScheduler, Supplier<Boolean> shouldCancel) {
        this.entity = entity;
        this.mobConfig = mobConfig;
        this.eventScheduler = eventScheduler;
        this.shouldCancel = shouldCancel;
    }

    @Override
    public int perform() {
        LivingEntity target = entity.getTarget();
        if(!(target instanceof ServerPlayer)) return missileThrowCooldown;
        performVolley((ServerPlayer) target);
        return missileThrowCooldown;
    }

    public void performVolley(ServerPlayer target) {
        final Optional<MobEffect> missileMobEffect = Optional.ofNullable(BuiltInRegistries.MOB_EFFECT.get(ResourceLocation.tryParse(mobConfig.missile.mobEffectId)));
        final Function<Vec3, ProjectileThrower> missileThrower = getMissileThrower(missileMobEffect);

        eventScheduler.addEvent(
                new TimedEvent(
                        () -> {
                            Vec3 targetPos = target.getBoundingBox().getCenter();
                            for (Vec3 offset : getMissileLaunchOffsets(entity))
                                missileThrower.apply(offset).throwProjectile(targetPos.add(VecUtils.planeProject(offset, VecUtils.yAxis)));

                            SoundUtils.playSound(
                                    target.serverLevel(),
                                    entity.position(),
                                    BMDSounds.MISSILE_SHOOT.get(),
                                    SoundSource.HOSTILE,
                                    3.0f,
                                    64,
                                    null
                            );
                        },
                        missileThrowDelay,
                        1,
                        shouldCancel
                )
        );

        eventScheduler.addEvent(
                new TimedEvent(
                        () -> SoundUtils.playSound(
                                target.serverLevel(),
                                entity.position(),
                                BMDSounds.MISSILE_PREPARE.get(),
                                SoundSource.HOSTILE,
                                4.0f,
                                64,
                                null
                        ),
                        10,
                        1,
                        shouldCancel
                )
        );
    }

    @NotNull
    private Function<Vec3, ProjectileThrower> getMissileThrower(Optional<MobEffect> missileMobEffect) {
        final int missileEffectDuration = mobConfig.missile.mobEffectDuration;
        final int missileEffectAmplifier = mobConfig.missile.mobEffectAmplifier;

        return offset ->
                new ProjectileThrower(
                        () -> {
                            MagicMissileProjectile projectile = new MagicMissileProjectile(
                                    entity,
                                    entity.level(),
                                    livingEntity -> missileMobEffect.ifPresent(effect -> livingEntity.addEffect(new MobEffectInstance(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(effect), missileEffectDuration, missileEffectAmplifier))),
                                    MinionAction.summonEntityType != null ? List.of(MinionAction.summonEntityType) : List.of());

                            MobUtils.setPos(projectile, MobUtils.eyePos(entity).add(offset));
                            return new ProjectileThrower.ProjectileData(projectile, 1.6f, 0f, 0.2);
                        });
    }

    public static List<Vec3> getMissileLaunchOffsets(Entity entity) {
        List<Vec3> offsets = new ArrayList<>();
        offsets.add(MathUtils.axisOffset(entity.getLookAngle(), VecUtils.yAxis.add(VecUtils.zAxis.scale(2.0))));
        offsets.add(MathUtils.axisOffset(entity.getLookAngle(), VecUtils.yAxis.scale(1.5).add(VecUtils.zAxis)));
        offsets.add(MathUtils.axisOffset(entity.getLookAngle(), VecUtils.yAxis.scale(2.0)));
        offsets.add(MathUtils.axisOffset(entity.getLookAngle(), VecUtils.yAxis.scale(1.5).add(VecUtils.negateServer(VecUtils.zAxis))));
        offsets.add(MathUtils.axisOffset(entity.getLookAngle(), VecUtils.yAxis.add(VecUtils.negateServer(VecUtils.zAxis).scale(2.0))));
        return offsets;
    }
}
