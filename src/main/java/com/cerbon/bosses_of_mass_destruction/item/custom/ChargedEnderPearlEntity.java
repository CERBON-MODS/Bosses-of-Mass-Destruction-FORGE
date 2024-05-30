package com.cerbon.bosses_of_mass_destruction.item.custom;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.MathUtils;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.RandomUtils;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.VecUtils;
import com.cerbon.bosses_of_mass_destruction.entity.BMDEntities;
import com.cerbon.bosses_of_mass_destruction.item.BMDItems;
import com.cerbon.bosses_of_mass_destruction.packet.BMDPacketHandler;
import com.cerbon.bosses_of_mass_destruction.packet.custom.ChargedEnderPearlS2CPacket;
import com.cerbon.bosses_of_mass_destruction.particle.BMDParticles;
import com.cerbon.bosses_of_mass_destruction.particle.ClientParticleBuilder;
import com.cerbon.bosses_of_mass_destruction.sound.BMDSounds;
import com.cerbon.bosses_of_mass_destruction.util.BMDColors;
import com.cerbon.bosses_of_mass_destruction.util.BMDUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.item.Item;
import net.minecraft.network.IPacket;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class ChargedEnderPearlEntity extends ProjectileItemEntity {
    private static final double radius = 3.0;
    private static final double impactHeight = 3.0;
    private static final ClientParticleBuilder verticalRodParticle = new ClientParticleBuilder(BMDParticles.ROD.get())
            .brightness(BMDParticles.FULL_BRIGHT)
            .color(age -> MathUtils.lerpVec(age, BMDColors.LIGHT_ENDER_PEARL, BMDColors.DARK_ENDER_PEARL))
            .colorVariation(0.25);
    private static final ClientParticleBuilder enderParticle = new ClientParticleBuilder(BMDParticles.FLUFF.get())
            .brightness(BMDParticles.FULL_BRIGHT)
            .color(BMDColors.ENDER_PURPLE)
            .colorVariation(0.25);

    public ChargedEnderPearlEntity(EntityType<? extends ProjectileItemEntity> entityType, World level) {
        super(entityType, level);
    }

    protected ChargedEnderPearlEntity(World level, LivingEntity owner) {
        super(BMDEntities.CHARGED_ENDER_PEARL.get(), owner, level);
    }

    @Override
    protected @Nonnull Item getDefaultItem() {
        return BMDItems.CHARGED_ENDER_PEARL.get();
    }

    @Override
    protected void onHitEntity(@Nonnull EntityRayTraceResult result) {
        super.onHitEntity(result);
        result.getEntity().hurt(DamageSource.thrown(this, getOwner()), 0.0f);
    }

    @Override
    protected void onHit(@Nonnull RayTraceResult result) {
        super.onHit(result);
        if (!level.isClientSide())
            if (!this.removed) serverCollision();
    }

    private void serverCollision(){
        teleportEntity(getOwner());
        applyMobEffects(getOwner());
        BMDPacketHandler.sendToAllPlayersTrackingChunk(new ChargedEnderPearlS2CPacket(position()), (ServerWorld) level, position());
        playSound(BMDSounds.CHARGED_ENDER_PEARL.get(), 1.0f, BMDUtils.randomPitch(this.random) * 0.8f);
        remove();
    }

    private void teleportEntity(Entity entity){
        if (entity instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) entity;

            if (player.connection.getConnection().isConnected() && player.level == this.level && !player.isSleeping()){
                if (player.isPassenger()) {
                    player.stopRiding();
                    player.teleportTo(this.getX(), this.getY(), this.getZ());
                }
                else
                    player.teleportTo(this.getX(), this.getY(), this.getZ());

                player.fallDistance = 0;
            }
        } else if (entity != null){
            entity.teleportTo(this.getX(), this.getY(), this.getZ());
            entity.fallDistance = 0;
        }
    }

    private void applyMobEffects(Entity entity){
        if (entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) entity;

            livingEntity.addEffect(new EffectInstance(Effects.DAMAGE_RESISTANCE, 120, 1));
            livingEntity.addEffect(new EffectInstance(Effects.SLOW_FALLING, 20, 0));
        }

        List<LivingEntity> livingEntities = level
                .getEntitiesOfClass(LivingEntity.class,
                        new AxisAlignedBB(getX(), getY(), getZ(), getX(), getY(), getZ()).inflate(radius * 2, impactHeight * 2, radius * 2),
                        this::isInXzAndYDistance);

        for (LivingEntity entity1 : livingEntities){
            Vector3d direction = position().subtract(entity1.position());
            entity1.knockback(0.4f, direction.x(), direction.z());
        }
    }

    private boolean isInXzAndYDistance(LivingEntity entity){
        return isInXzDistance(entity) && isInYDistance(entity);
    }

    private boolean isInXzDistance(LivingEntity entity){
        Vector3d xzLineToEntity = VecUtils.planeProject(entity.position(), VecUtils.yAxis).subtract(VecUtils.planeProject(this.position(), VecUtils.yAxis));
        double sqXzDistanceTowardEntity = xzLineToEntity.lengthSqr();
        boolean entityCenterInRadius = sqXzDistanceTowardEntity < radius * radius;
        if (entityCenterInRadius) return true;

        Vector3d xzDirectionTowardEntity = xzLineToEntity.normalize();
        Vector3d xzPointTowardEntity = xzDirectionTowardEntity.scale(radius).add(position());
        return entity.getBoundingBox().inflate(0.0, 10.0, 0.0).contains(xzPointTowardEntity);
    }

    private boolean isInYDistance(LivingEntity entity){
        double yBottom = position().y() - 1;
        double yTop = position().y() + impactHeight;
        return entity.getBoundingBox().maxY > yBottom || entity.getBoundingBox().minY < yTop;
    }

    @Override
    public void tick() {
        Entity entity = getOwner();
        if (entity instanceof PlayerEntity && !entity.isAlive())
            remove();
        else
            super.tick();
    }

    @Nullable
    @Override
    public Entity changeDimension(@Nonnull ServerWorld destination) {
        Entity entity = getOwner();
        if (entity != null && entity.level.dimension() != destination.dimension())
            setOwner(null);

        return super.changeDimension(destination);
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return super.getAddEntityPacket();
    }

    public static void handlePearlImpact(Vector3d pos){
        spawnTeleportParticles(pos);
        spawnVerticalRods(pos);
    }

    private static void spawnTeleportParticles(Vector3d pos){
        for (int i = 0; i <= 10; i++){
            int startingRotation = new Random().nextInt(360);
            double randomRadius = RandomUtils.range(2.0, 3.0);
            double rotationSpeed = RandomUtils.range(3.0, 4.0);
            int finalI = i;
            enderParticle
                    .continuousPosition(particle -> rotateAroundPos(pos, finalI, particle.getAge(), startingRotation, randomRadius, rotationSpeed))
                    .build(rotateAroundPos(pos, i, 0, startingRotation, randomRadius, rotationSpeed), Vector3d.ZERO);
        }
    }

    private static Vector3d rotateAroundPos(
            Vector3d pos,
            int i,
            int age,
            int startingRotation,
            double radius,
            double rotationSpeed
    ){
        Vector3d yOffset = VecUtils.yAxis.scale(i / 15.0);
        Vector3d xzOffset = VecUtils.xAxis.yRot((float) Math.toRadians(age * rotationSpeed + startingRotation));
        return pos.add(yOffset).add(xzOffset.scale(radius));
    }

    private static void spawnVerticalRods(Vector3d pos){
        for (int y = 0; y <= (int) impactHeight; y++)
            spawnVerticalRodRing(y, pos);
    }

    private static void spawnVerticalRodRing(int y, Vector3d pos){
        MathUtils.circleCallback(radius, 30 - (y * 3), VecUtils.yAxis,
                vec3 -> verticalRodParticle.build(
                        pos.add(vec3).add(VecUtils.yAxis.scale(y + RandomUtils.range(0.0, 1.0))),
                        VecUtils.yAxis.scale(0.1)
                ));
    }
}
