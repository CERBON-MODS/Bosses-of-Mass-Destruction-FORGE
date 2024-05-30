package com.cerbon.bosses_of_mass_destruction.entity.custom.lich;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.EventScheduler;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.TimedEvent;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.MathUtils;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.MobUtils;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.VecUtils;
import com.cerbon.bosses_of_mass_destruction.config.mob.LichConfig;
import com.cerbon.bosses_of_mass_destruction.entity.ai.action.IActionWithCooldown;
import com.cerbon.bosses_of_mass_destruction.entity.util.ProjectileThrower;
import com.cerbon.bosses_of_mass_destruction.projectile.MagicMissileProjectile;
import com.cerbon.bosses_of_mass_destruction.sound.BMDSounds;
import com.cerbon.bosses_of_mass_destruction.util.BMDUtils;
import com.google.common.collect.Lists;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;

import javax.annotation.Nonnull;
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
        if(!(target instanceof ServerPlayerEntity)) return missileThrowCooldown;
        performVolley((ServerPlayerEntity) target);
        return missileThrowCooldown;
    }

    public void performVolley(ServerPlayerEntity target) {
        final Optional<Effect> missileMobEffect = Optional.ofNullable(Registry.MOB_EFFECT.get(ResourceLocation.tryParse(mobConfig.missile.mobEffectId)));
        final Function<Vector3d, ProjectileThrower> missileThrower = getMissileThrower(missileMobEffect);

        eventScheduler.addEvent(
                new TimedEvent(
                        () -> {
                            Vector3d targetPos = target.getBoundingBox().getCenter();
                            for (Vector3d offset : getMissileLaunchOffsets(entity))
                                missileThrower.apply(offset).throwProjectile(targetPos.add(VecUtils.planeProject(offset, VecUtils.yAxis)));

                            BMDUtils.playSound(
                                    target.getLevel(),
                                    entity.position(),
                                    BMDSounds.MISSILE_SHOOT.get(),
                                    SoundCategory.HOSTILE,
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
                        () -> BMDUtils.playSound(
                                target.getLevel(),
                                entity.position(),
                                BMDSounds.MISSILE_PREPARE.get(),
                                SoundCategory.HOSTILE,
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

    @Nonnull
    private Function<Vector3d, ProjectileThrower> getMissileThrower(Optional<Effect> missileMobEffect) {
        final int missileEffectDuration = mobConfig.missile.mobEffectDuration;
        final int missileEffectAmplifier = mobConfig.missile.mobEffectAmplifier;

        return offset ->
                new ProjectileThrower(
                        () -> {
                            MagicMissileProjectile projectile = new MagicMissileProjectile(
                                    entity,
                                    entity.level,
                                    livingEntity -> missileMobEffect.ifPresent(effect -> livingEntity.addEffect(new EffectInstance(effect, missileEffectDuration, missileEffectAmplifier))),
                                    MinionAction.summonEntityType != null ? Lists.newArrayList(MinionAction.summonEntityType) : Lists.newArrayList());

                            MobUtils.setPos(projectile, MobUtils.eyePos(entity).add(offset));
                            return new ProjectileThrower.ProjectileData(projectile, 1.6f, 0f, 0.2);
                        });
    }

    public static List<Vector3d> getMissileLaunchOffsets(Entity entity) {
        List<Vector3d> offsets = new ArrayList<>();
        offsets.add(MathUtils.axisOffset(entity.getLookAngle(), VecUtils.yAxis.add(VecUtils.zAxis.scale(2.0))));
        offsets.add(MathUtils.axisOffset(entity.getLookAngle(), VecUtils.yAxis.scale(1.5).add(VecUtils.zAxis)));
        offsets.add(MathUtils.axisOffset(entity.getLookAngle(), VecUtils.yAxis.scale(2.0)));
        offsets.add(MathUtils.axisOffset(entity.getLookAngle(), VecUtils.yAxis.scale(1.5).add(VecUtils.negateServer(VecUtils.zAxis))));
        offsets.add(MathUtils.axisOffset(entity.getLookAngle(), VecUtils.yAxis.add(VecUtils.negateServer(VecUtils.zAxis).scale(2.0))));
        return offsets;
    }
}
