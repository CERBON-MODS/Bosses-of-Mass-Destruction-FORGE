package com.cerbon.bosses_of_mass_destruction.item.custom;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.Event;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.RandomUtils;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.VecUtils;
import com.cerbon.bosses_of_mass_destruction.capability.util.BMDCapabilities;
import com.cerbon.bosses_of_mass_destruction.entity.BMDEntities;
import com.cerbon.bosses_of_mass_destruction.entity.custom.lich.LichUtils;
import com.cerbon.bosses_of_mass_destruction.item.BMDItems;
import com.cerbon.bosses_of_mass_destruction.particle.BMDParticles;
import com.cerbon.bosses_of_mass_destruction.particle.ClientParticleBuilder;
import com.cerbon.bosses_of_mass_destruction.sound.BMDSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IRendersAsItem;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

public class SoulStarEntity extends Entity implements IRendersAsItem {
    private static final DataParameter<ItemStack> ITEM = EntityDataManager.defineId(SoulStarEntity.class, DataSerializers.ITEM_STACK);
    private final ClientParticleBuilder particleBuilder = new ClientParticleBuilder(BMDParticles.SPARKLES.get())
            .color(LichUtils.blueColorFade)
            .age(RandomUtils.range(80, 100))
            .colorVariation(0.3)
            .scale(f -> 0.05f - (f * 0.025f))
            .brightness(BMDParticles.FULL_BRIGHT);

    private double targetX = 0.0;
    private double targetY = 0.0;
    private double targetZ = 0.0;

    public SoulStarEntity(EntityType<?> entityType, World level) {
        super(entityType, level);
    }

    public SoulStarEntity(World level, double x, double y, double z){
        this(BMDEntities.SOUL_STAR.get(), level);
        absMoveTo(x, y, z);
    }

    public void setItem(ItemStack stack){
        if (stack.getItem() != Items.ENDER_EYE || stack.hasTag())
            getEntityData().set(ITEM, Util.make(stack.copy(), stack1 -> stack1.setCount(1)));
    }

    private ItemStack getTrackedItem(){
        return getEntityData().get(ITEM);
    }

    @Override
    public @Nonnull ItemStack getItem() {
        ItemStack itemStack = getTrackedItem();
        return itemStack.isEmpty() ? new ItemStack(BMDItems.SOUL_STAR.get()) : itemStack;
    }

    @Override
    protected void defineSynchedData() {
        getEntityData().define(ITEM, ItemStack.EMPTY);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean shouldRenderAtSqrDistance(double distance) {
        double d = this.getBoundingBox().getSize() * 4.0;
        if (Double.isNaN(d))
            d = 4.0;

        d *= 64.0;
        return distance < d * d;
    }

    /**
     * @param pos the block the eye of ender is drawn towards
     */
    public void initTargetPos(BlockPos pos){
        double d = pos.getX();
        double e = pos.getZ();
        double f = d - this.getX();
        double g = e - this.getZ();
        double h = Math.sqrt(f * f + g * g);
        targetX = this.getX() + f / h * 12.0;
        targetZ = this.getZ() + g / h * 12.0;
        targetY = this.getY() + 8.0;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void lerpMotion(double x, double y, double z) {
        this.setDeltaMovement(x, y, z);
        if (xRotO == 0.0f && yRotO == 0.0f){
            double f = Math.sqrt(x * x + z * z);
            yRot = ((float) (MathHelper.atan2(x, z) * 57.2957763671875));
            xRot = ((float) (MathHelper.atan2(y, f) * 57.2957763671875));
            yRotO = yRot;
            xRotO = xRot;
        }
    }

    @Override
    public void tick() {
        if (level.isClientSide() && tickCount == 1){
            double rotationOffset = random.nextDouble() * 360;
            BMDCapabilities.getLevelEventScheduler(level).addEvent(
                    new Event(
                            () -> true,
                            () -> spawnTrailParticles(rotationOffset),
                            () -> this.removed
                    )
            );
        }

        super.tick();
        Vector3d vec3 = getDeltaMovement();
        double d = this.getX() + vec3.x;
        double e = this.getY() + vec3.y;
        double f = this.getZ() + vec3.z;
        double g = Math.sqrt(vec3.x * vec3.x + vec3.z * vec3.z);
        xRot = (updateRotation(xRotO, (float) (MathHelper.atan2(vec3.y, g) * 57.2957763671875)));
        yRot = (updateRotation(yRotO, (float) (MathHelper.atan2(vec3.x, vec3.z) * 57.2957763671875)));

        if (!level.isClientSide()){
            double xd = targetX - d;
            double zd = targetZ - f;
            float distance = (float) Math.sqrt(xd * xd + zd * zd);
            float k = (float) MathHelper.atan2(zd, xd);
            double l = MathHelper.lerp(0.0025, g, distance);
            double m = vec3.y;
            if(distance < 1.0f){
                l *= 0.8;
                m *= 0.8;

                playSound(BMDSounds.SOUL_STAR.get(), 1.0f, 1.0f);
                remove();
                level.addFreshEntity(new ItemEntity(level, this.getX(), this.getY(), this.getZ(), this.getItem()));
            }
            int n = this.getY() < targetY ? 1 : -1;
            vec3 = new Vector3d(Math.cos(k) * l, m + (n - m) * 0.014999999664723873, Math.sin(k) * l);
            setDeltaMovement(vec3);

            absMoveTo(d, e, f);
        }else
            setPosRaw(d, e, f);

        spawnParticles(d, vec3, e, f);
    }

    private void spawnTrailParticles(double rotationOffset){
        Vector3d look = getDeltaMovement();
        Vector3d cross = look.cross(VecUtils.yAxis).normalize();
        Vector3d rotatedOffset = VecUtils.rotateVector(cross, look, rotationOffset + tickCount * 30.0).scale(0.25);
        Vector3d particlePos = position().add(rotatedOffset);
        particleBuilder.build(particlePos, getDeltaMovement().scale(0.1));
    }

    private void spawnParticles(double d, Vector3d vec3, double e, double f){
        if (this.isInWater()){
            for (int p = 0; p <= 3; p++){
                level.addParticle(
                        ParticleTypes.BUBBLE,
                        d - vec3.x * 0.25,
                        e - vec3.y * 0.25,
                        f - vec3.z * 0.25,
                        vec3.x,
                        vec3.y,
                        vec3.z
                );
            }
        }
    }

    @Override
    protected void addAdditionalSaveData(@Nonnull CompoundNBT compound) {
        ItemStack itemStack = this.getTrackedItem();
        if (!itemStack.isEmpty())
            compound.put("Item", itemStack.save(new CompoundNBT()));
    }

    @Override
    protected void readAdditionalSaveData(@Nonnull CompoundNBT compound) {
        ItemStack itemStack = ItemStack.of(compound.getCompound("Item"));
        setItem(itemStack);
    }

    @Override
    public float getBrightness() {
        return 1.0f;
    }

    @Override
    public boolean isAttackable() {
        return false;
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return new SSpawnObjectPacket(this);
    }

    private float updateRotation(float f, float g){
        float f1 = f;
        while (g - f1 < -180.0f)
            f1 -= 360.0f;

        while (g - f1 >= 180.0f)
            f1 += 360.0f;

        return MathHelper.lerp(0.2f, f1, g);
    }
}
