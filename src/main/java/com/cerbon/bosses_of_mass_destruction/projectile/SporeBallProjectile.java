package com.cerbon.bosses_of_mass_destruction.projectile;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.EventScheduler;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.TimedEvent;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.MathUtils;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.VecUtils;
import com.cerbon.bosses_of_mass_destruction.capability.util.BMDCapabilities;
import com.cerbon.bosses_of_mass_destruction.damagesource.UnshieldableDamageSource;
import com.cerbon.bosses_of_mass_destruction.entity.BMDEntities;
import com.cerbon.bosses_of_mass_destruction.entity.custom.obsidilith.RiftBurst;
import com.cerbon.bosses_of_mass_destruction.particle.BMDParticles;
import com.cerbon.bosses_of_mass_destruction.particle.ClientParticleBuilder;
import com.cerbon.bosses_of_mass_destruction.sound.BMDSounds;
import com.cerbon.bosses_of_mass_destruction.util.BMDColors;
import com.cerbon.bosses_of_mass_destruction.util.BMDUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.item.DirectionalPlaceContext;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.IAnimationTickable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class SporeBallProjectile extends BaseThrownItemProjectile implements IAnimatable, IAnimationTickable {
    private final List<Vector3d> circlePoints = MathUtils.buildBlockCircle(7.0);
    private final ClientParticleBuilder projectileParticles = new ClientParticleBuilder(BMDParticles.DISAPPEARING_SWIRL.get())
            .color(BMDColors.GREEN)
            .colorVariation(0.4)
            .scale(0.2f)
            .brightness(BMDParticles.FULL_BRIGHT);
    private final byte particle = 5;
    private float impactedPitch = 0f;


    public float impactedTicks = 0f;
    public boolean impacted = false;

    public static final int explosionDelay = 30;

    public SporeBallProjectile(EntityType<? extends ProjectileItemEntity> entityType, World level) {
        super(entityType, level);
        collisionPredicate = hitResult -> true;
    }

    public SporeBallProjectile(LivingEntity livingEntity, World level, Predicate<EntityRayTraceResult> entityPredicate){
        super(BMDEntities.SPORE_BALL.get(), livingEntity, level, entityPredicate);
        collisionPredicate = hitResult -> true;
    }

    @Override
    protected void onHitBlock(@Nonnull BlockRayTraceResult result) {
        onImpact();
    }

    @Override
    public void clientTick() {
        super.clientTick();
        if (impacted)
            impactedTicks++;
    }

    @Override
    public @Nonnull Vector3d getDeltaMovement() {
        return !impacted ? super.getDeltaMovement() : Vector3d.ZERO;
    }

    private void onImpact(){
        if (impacted) return;

        impactedPitch = getXRot();
        impacted = true;
        Entity owner = getOwner();
        if (owner instanceof LivingEntity)
            doExplosion(((LivingEntity) owner));
        else if (!level.isClientSide())
            remove();
    }

    public float getXRot() {
        return impacted ? impactedPitch : tickCount * 5f;
    }

    public float getYRot() {
        return 0f;
    }

    private void doExplosion(LivingEntity owner){
        level.broadcastEntityEvent(this, particle);
        playSound(BMDSounds.SPORE_BALL_LAND.get(), 1.0f, BMDUtils.randomPitch(random) - 0.2f);
        EventScheduler eventScheduler = BMDCapabilities.getLevelEventScheduler(level);
        Consumer<LivingEntity> onImpact = entity -> {
            float damage = (float) owner.getAttributeValue(Attributes.ATTACK_DAMAGE);
            if (this.getOwner() != null){
                entity.hurt(new UnshieldableDamageSource(this.getOwner()), damage);
                owner.addEffect(new EffectInstance(Effects.POISON, 140));
            }
        };

        if (!level.isClientSide) {
            RiftBurst riftBurst = new RiftBurst(
                    owner,
                    (ServerWorld) level,
                    BMDParticles.SPORE_INDICATOR.get(),
                    BMDParticles.SPORE.get(),
                    explosionDelay,
                    eventScheduler,
                    onImpact,
                    this::isOpenBlock,
                    this::posFinder
            );


            eventScheduler.addEvent(
                    new TimedEvent(
                            () -> {
                                playSound(BMDSounds.SPORE_IMPACT.get(), 1.5f, BMDUtils.randomPitch(random));
                                remove();
                            },
                            explosionDelay
                    )
            );

            Vector3d center = VecUtils.asVec3(blockPosition()).add(VecUtils.unit.scale(0.5));
            for (Vector3d point : circlePoints)
                riftBurst.tryPlaceRift(center.add(point));
        }
    }

    private BlockPos posFinder(Vector3d pos){
        BlockPos above = new BlockPos(pos.add(VecUtils.yAxis.scale(2.0)));
        BlockPos groundPos = BMDUtils.findGroundBelow(level, above, pos1 -> true);
        BlockPos up = groundPos.above();
        return (up.getY() + 8 >= above.getY() && isOpenBlock(up)) ? up : null;
    }

    private boolean isOpenBlock(BlockPos up) {
        BlockState blockState = level.getBlockState(up);
        return blockState.canBeReplaced(new DirectionalPlaceContext(
                level,
                up,
                Direction.DOWN,
                ItemStack.EMPTY,
                Direction.UP
        )) || blockState.getBlock() == Blocks.GRASS_BLOCK;
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == particle)
            for (Vector3d point : MathUtils.circlePoints(0.8, 16, VecUtils.yAxis))
                projectileParticles.build(point.add(position()), point.scale(0.1));

        super.handleEntityEvent(id);
    }

    @Override
    public void entityHit(EntityRayTraceResult entityHitResult) {
        if (level.isClientSide()) return;
        Entity owner = getOwner();
        Entity entity = entityHitResult.getEntity();

        if (owner instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) owner;

            if (entity != livingEntity) {
                float damage = (float) livingEntity.getAttributeValue(Attributes.ATTACK_DAMAGE);
                entity.hurt(DamageSource.thrown(this, livingEntity), damage);
            }
        }
    }

    @Override
    public void registerControllers(AnimationData animationData) {}

    @Override
    public AnimationFactory getFactory() {
        return new AnimationFactory(this);
    }

    @Override
    public int tickTimer() {
        return 0;
    }
}
