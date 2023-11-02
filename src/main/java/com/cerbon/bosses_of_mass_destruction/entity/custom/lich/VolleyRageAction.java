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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class VolleyRageAction implements IActionWithCooldown {
    public static final int ragedMissileVolleyInitialDelay = 60;
    public static final int ragedMissileVolleyBetweenVolleyDelay = 30;
    public static final int ragedMissileParticleDelay = 30;

    private final LichEntity entity;
    private final LichConfig mobConfig;
    private final EventScheduler eventScheduler;
    private final Supplier<Boolean> shouldCancel;

    public VolleyRageAction(LichEntity entity, LichConfig mobConfig, EventScheduler eventScheduler, Supplier<Boolean> shouldCancel) {
        this.entity = entity;
        this.mobConfig = mobConfig;
        this.eventScheduler = eventScheduler;
        this.shouldCancel = shouldCancel;
    }

    @Override
    public int perform() {
        LivingEntity target = entity.getTarget();
        if (!(target instanceof ServerPlayer)) return 80;
        return performVolley((ServerPlayer) target);
    }

    public int performVolley(ServerPlayer target) {
        int rageMissileVolleys = getRageMissileVolleys(entity).size();
        final Optional<MobEffect> missileMobEffect = Optional.ofNullable(ForgeRegistries.MOB_EFFECTS.getValue(ResourceLocation.tryParse(mobConfig.missile.mobEffectId)));
        final int missileEffectDuration = mobConfig.missile.mobEffectDuration;
        final int missileEffectAmplifier = mobConfig.missile.mobEffectAmplifier;
        final Function<Vec3, ProjectileThrower> missileThrower = offset ->
                new ProjectileThrower(
                        () -> {
                            MagicMissileProjectile projectile = new MagicMissileProjectile(
                                    entity,
                                    entity.level()
                                    ,livingEntity -> missileMobEffect.ifPresent(effect -> livingEntity.addEffect(new MobEffectInstance(effect, missileEffectDuration, missileEffectAmplifier))),
                                    MinionAction.summonEntityType != null ? List.of(MinionAction.summonEntityType) : List.of());

                            MobUtils.setPos(projectile, MobUtils.eyePos(entity).add(offset));
                            return new ProjectileThrower.ProjectileData(projectile, 1.6f, 0f, 0.2);
                        });

        for (int i = 0; i < rageMissileVolleys; i++){
            int i1 = i;
            eventScheduler.addEvent(
                    new TimedEvent(
                            () -> {
                                Vec3 targetPos = target.getBoundingBox().getCenter();
                                for (Vec3 offset : getRageMissileVolleys(entity).get(i1))
                                    missileThrower.apply(offset).throwProjectile(targetPos.add(offset));

                                BMDUtils.playSound(
                                        target.serverLevel(),
                                        entity.position(),
                                        BMDSounds.MISSILE_SHOOT.get(),
                                        SoundSource.HOSTILE,
                                        3.0f,
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

    public static List<List<Vec3>> getRageMissileVolleys(LichEntity entity) {
        double xOffset = 3.0;
        double zOffset = 4.0;
        int numPoints = 9;

        Vec3 lineStart = MathUtils.axisOffset(entity.getLookAngle(), VecUtils.xAxis.multiply(xOffset, xOffset, xOffset).add(VecUtils.zAxis.multiply(zOffset, zOffset, zOffset)));
        Vec3 lineEnd = MathUtils.axisOffset(entity.getLookAngle(), VecUtils.xAxis.multiply(xOffset, xOffset, xOffset).add(VecUtils.zAxis.multiply(-zOffset, -zOffset, -zOffset)));

        ArrayList<Vec3> lineAcross = new ArrayList<>();
        MathUtils.lineCallback(lineStart, lineEnd, numPoints, (vec3, integer) -> lineAcross.add(vec3));

        List<Vec3> lineUpDown = lineAcross.stream().map(vec3 -> VecUtils.rotateVector(vec3, entity.getLookAngle(), 90.0)).toList();
        List<Vec3> cross = new ArrayList<>(lineAcross);
        cross.addAll(lineUpDown);
        List<Vec3> xVolley = cross.stream().map(vec3 -> VecUtils.rotateVector(vec3, entity.getLookAngle(), 45.0)).toList();

        return List.of(lineAcross, lineUpDown, cross, xVolley);
    }
}

