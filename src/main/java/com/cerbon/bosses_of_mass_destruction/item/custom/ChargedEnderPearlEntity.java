package com.cerbon.bosses_of_mass_destruction.item.custom;

import com.cerbon.bosses_of_mass_destruction.entity.BMDEntities;
import com.cerbon.bosses_of_mass_destruction.item.BMDItems;
import com.cerbon.bosses_of_mass_destruction.packet.BMDPacketHandler;
import com.cerbon.bosses_of_mass_destruction.packet.custom.ChargedEnderPearlS2CPacket;
import com.cerbon.bosses_of_mass_destruction.particle.BMDParticles;
import com.cerbon.bosses_of_mass_destruction.particle.ClientParticleBuilder;
import com.cerbon.bosses_of_mass_destruction.sound.BMDSounds;
import com.cerbon.bosses_of_mass_destruction.util.BMDColors;
import com.cerbon.bosses_of_mass_destruction.util.BMDUtils;
import com.cerbon.bosses_of_mass_destruction.util.maelstrom.static_utilities.MathUtils;
import com.cerbon.bosses_of_mass_destruction.util.maelstrom.static_utilities.RandomUtils;
import com.cerbon.bosses_of_mass_destruction.util.maelstrom.static_utilities.VecUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

public class ChargedEnderPearlEntity extends ThrowableItemProjectile {
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

    public ChargedEnderPearlEntity(EntityType<? extends ThrowableItemProjectile> entityType, Level level) {
        super(entityType, level);
    }

    protected ChargedEnderPearlEntity(Level level, LivingEntity owner) {
        super(BMDEntities.CHARGED_ENDER_PEARL.get(), owner, level);
    }

    @Override
    protected @NotNull Item getDefaultItem() {
        return BMDItems.CHARGED_ENDER_PEARL.get();
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult result) {
        super.onHitEntity(result);
        result.getEntity().hurt(result.getEntity().level().damageSources().thrown(this, getOwner()), 0.0f);
    }

    @Override
    protected void onHit(@NotNull HitResult result) {
        super.onHit(result);
        if (!level().isClientSide())
            if (!this.isRemoved()) serverCollision();
    }

    private void serverCollision(){
        teleportEntity(getOwner());
        applyMobEffects(getOwner());
        BMDPacketHandler.sendToAllPlayersInChunk(new ChargedEnderPearlS2CPacket(position()), (ServerLevel) level(), position());
        playSound(BMDSounds.CHARGED_ENDER_PEARL.get(), 1.0f, BMDUtils.randomPitch(this.random) * 0.8f);
        discard();
    }

    private void teleportEntity(Entity entity){
        if (entity instanceof ServerPlayer player){
            if (player.connection.isAcceptingMessages() && player.level() == this.level() && !player.isSleeping()){
                if (player.isPassenger())
                    player.dismountTo(this.getX(), this.getY(), this.getZ());
                else
                    player.teleportTo(this.getX(), this.getY(), this.getZ());

                player.resetFallDistance();
            }
        } else if (entity != null){
            entity.teleportTo(this.getX(), this.getY(), this.getZ());
            entity.resetFallDistance();
        }
    }

    private void applyMobEffects(Entity entity){
        if (entity instanceof LivingEntity livingEntity){
            livingEntity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 120, 1));
            livingEntity.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 20, 0));
        }

        List<LivingEntity> livingEntities = level()
                .getEntitiesOfClass(LivingEntity.class,
                        new AABB(getX(), getY(), getZ(), getX(), getY(), getZ()).inflate(radius * 2, impactHeight * 2, radius * 2),
                        this::isInXzAndYDistance);

        for (LivingEntity entity1 : livingEntities){
            Vec3 direction = position().subtract(entity1.position());
            entity1.knockback(0.4f, direction.x(), direction.z());
        }
    }

    private boolean isInXzAndYDistance(LivingEntity entity){
        return isInXzDistance(entity) && isInYDistance(entity);
    }

    private boolean isInXzDistance(LivingEntity entity){
        Vec3 xzLineToEntity = VecUtils.planeProject(entity.position(), VecUtils.yAxis).subtract(VecUtils.planeProject(this.position(), VecUtils.yAxis));
        double sqXzDistanceTowardEntity = xzLineToEntity.lengthSqr();
        boolean entityCenterInRadius = sqXzDistanceTowardEntity < radius * radius;
        if (entityCenterInRadius) return true;

        Vec3 xzDirectionTowardEntity = xzLineToEntity.normalize();
        Vec3 xzPointTowardEntity = xzDirectionTowardEntity.multiply(radius, radius, radius).add(position());
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
        if (entity instanceof Player player && !player.isAlive())
            discard();
        else
            super.tick();
    }

    @Nullable
    @Override
    public Entity changeDimension(@NotNull ServerLevel destination) {
        Entity entity = getOwner();
        if (entity != null && entity.level().dimension() != destination.dimension())
            setOwner(null);

        return super.changeDimension(destination);
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return super.getAddEntityPacket();
    }

    public static void handlePearlImpact(Vec3 pos){
        spawnTeleportParticles(pos);
        spawnVerticalRods(pos);
    }

    private static void spawnTeleportParticles(Vec3 pos){
        for (int i = 0; i <= 10; i++){
            int startingRotation = new Random().nextInt(360);
            double randomRadius = RandomUtils.range(2.0, 3.0);
            double rotationSpeed = RandomUtils.range(3.0, 4.0);
            int finalI = i;
            enderParticle
                    .continuousPosition(particle -> rotateAroundPos(pos, finalI, particle.getAge(), startingRotation, randomRadius, rotationSpeed))
                    .build(rotateAroundPos(pos, i, 0, startingRotation, randomRadius, rotationSpeed), Vec3.ZERO);
        }
    }

    private static Vec3 rotateAroundPos(
            Vec3 pos,
            int i,
            int age,
            int startingRotation,
            double radius,
            double rotationSpeed
    ){
        Vec3 yOffset = VecUtils.yAxis.multiply(i / 15.0, i / 15.0, i / 15.0);
        Vec3 xzOffset = VecUtils.xAxis.yRot((float) Math.toRadians(age * rotationSpeed + startingRotation));
        return pos.add(yOffset).add(xzOffset.multiply(radius, radius, radius));
    }

    private static void spawnVerticalRods(Vec3 pos){
        for (int y = 0; y <= (int) impactHeight; y++)
            spawnVerticalRodRing(y, pos);
    }

    private static void spawnVerticalRodRing(int y, Vec3 pos){
        MathUtils.circleCallback(radius, 30 - (y * 3), VecUtils.yAxis,
                vec3 -> verticalRodParticle.build(
                        pos.add(vec3).add(VecUtils.yAxis.multiply(y + RandomUtils.range(0.0, 1.0), y + RandomUtils.range(0.0, 1.0), y + RandomUtils.range(0.0, 1.0))),
                        VecUtils.yAxis.multiply(0.1, 0.1, 0.1)
                ));
    }
}
