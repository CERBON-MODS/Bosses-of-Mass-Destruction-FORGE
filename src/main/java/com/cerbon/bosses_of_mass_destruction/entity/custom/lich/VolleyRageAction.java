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
import java.util.stream.Collectors;

public class VolleyRageAction implements IActionWithCooldown {
    private final LichEntity entity;
    private final LichConfig mobConfig;
    private final EventScheduler eventScheduler;
    private final Supplier<Boolean> shouldCancel;

    public static final int ragedMissileVolleyInitialDelay = 60;
    public static final int ragedMissileVolleyBetweenVolleyDelay = 30;
    public static final int ragedMissileParticleDelay = 30;

    public VolleyRageAction(LichEntity entity, LichConfig mobConfig, EventScheduler eventScheduler, Supplier<Boolean> shouldCancel) {
        this.entity = entity;
        this.mobConfig = mobConfig;
        this.eventScheduler = eventScheduler;
        this.shouldCancel = shouldCancel;
    }

    @Override
    public int perform() {
        LivingEntity target = entity.getTarget();
        if (!(target instanceof ServerPlayerEntity)) return 80;
        return performVolley((ServerPlayerEntity) target);
    }

    public int performVolley(ServerPlayerEntity target) {
        int rageMissileVolleys = getRageMissileVolleys(entity).size();
        final Optional<Effect> missileMobEffect = Optional.ofNullable(Registry.MOB_EFFECT.get(ResourceLocation.tryParse(mobConfig.missile.mobEffectId)));
        final Function<Vector3d, ProjectileThrower> missileThrower = getMissileThrower(missileMobEffect);

        BMDUtils.playSound(target.getLevel(), entity.position(), BMDSounds.MISSILE_PREPARE.get(), SoundCategory.HOSTILE, 4.0f, 64, null);
        for (int i = 0; i < rageMissileVolleys; i++){
            int i1 = i;
            eventScheduler.addEvent(
                    new TimedEvent(
                            () -> {
                                Vector3d targetPos = target.getBoundingBox().getCenter();
                                for (Vector3d offset : getRageMissileVolleys(entity).get(i1))
                                    missileThrower.apply(offset).throwProjectile(targetPos.add(offset));

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
                            ragedMissileVolleyInitialDelay + (i1 * ragedMissileVolleyBetweenVolleyDelay),
                            1,
                            shouldCancel
                    )
            );
        }

        return ragedMissileVolleyInitialDelay + (rageMissileVolleys * ragedMissileVolleyBetweenVolleyDelay);
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

    public static List<List<Vector3d>> getRageMissileVolleys(LichEntity entity) {
        double xOffset = 3.0;
        double zOffset = 4.0;
        int numPoints = 9;

        Vector3d lineStart = MathUtils.axisOffset(entity.getLookAngle(), VecUtils.xAxis.scale(xOffset).add(VecUtils.zAxis.scale(zOffset)));
        Vector3d lineEnd = MathUtils.axisOffset(entity.getLookAngle(), VecUtils.xAxis.scale(xOffset).add(VecUtils.zAxis.scale(-zOffset)));

        ArrayList<Vector3d> lineAcross = new ArrayList<>();
        MathUtils.lineCallback(lineStart, lineEnd, numPoints, (vec3, integer) -> lineAcross.add(vec3));

        List<Vector3d> lineUpDown = lineAcross.stream().map(vec3 -> VecUtils.rotateVector(vec3, entity.getLookAngle(), 90.0)).collect(Collectors.toList());
        List<Vector3d> cross = new ArrayList<>(lineAcross);
        cross.addAll(lineUpDown);
        List<Vector3d> xVolley = cross.stream().map(vec3 -> VecUtils.rotateVector(vec3, entity.getLookAngle(), 45.0)).collect(Collectors.toList());

        return Lists.newArrayList(lineAcross, lineUpDown, cross, xVolley);
    }
}

