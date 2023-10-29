package com.cerbon.bosses_of_mass_destruction.util;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.MathUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class VanillaCopies {
    public static DamageSource create(Level level, ResourceKey<DamageType> key, Entity attacker) {
        Holder<DamageType> damageType = level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(key);
        return new DamageSource(damageType, attacker);
    }

    public static void travel(Vec3 relative, LivingEntity entity, float baseFrictionCoefficient) {
        if (entity.isInWater()) {
            entity.moveRelative(0.02F, relative);
            entity.move(MoverType.SELF, entity.getDeltaMovement());
            entity.setDeltaMovement(entity.getDeltaMovement().multiply(0.8, 0.8, 0.8));

        } else if (entity.isInLava()) {
            entity.moveRelative(0.02F, relative);
            entity.move(MoverType.SELF, entity.getDeltaMovement());
            entity.setDeltaMovement(entity.getDeltaMovement().multiply(0.5, 0.5, 0.5));

        } else {
            float friction = entity.onGround() ? entity.level().getBlockState(BlockPos.containing(entity.getX(), entity.getY(), entity.getZ())).getBlock()
                    .getFriction() * baseFrictionCoefficient : baseFrictionCoefficient;
            float g = 0.16277137F / (friction * friction * friction);

            entity.moveRelative(entity.onGround() ? 0.1F * g : 0.02F, relative);
            entity.move(MoverType.SELF, entity.getDeltaMovement());
            entity.setDeltaMovement(entity.getDeltaMovement().multiply(friction, friction, friction));
        }
        entity.calculateEntityAnimation(false);
    }

    public static void lookAtTarget(Mob mobEntity, Vec3 target, float maxYawChange, float maxPitchChange) {
        double d = target.x - mobEntity.getX();
        double e = target.z - mobEntity.getZ();
        double g = target.y - mobEntity.getEyeY();

        double h = Math.sqrt(d * d + e * e);
        float i = (float) ((Mth.atan2(e, d) * 57.2957763671875) - 90.0);
        float j = (float) (-(Mth.atan2(g, h) * 57.2957763671875));
        mobEntity.setXRot(changeAngle(mobEntity.getXRot(), j, maxPitchChange));
        mobEntity.setYRot(changeAngle(mobEntity.getYRot(), i, maxYawChange));
    }

    public static float changeAngle(float oldAngle, float newAngle, float maxChangeInAngle) {
        float f = Mth.wrapDegrees(newAngle - oldAngle);

        if (f > maxChangeInAngle)
            f = maxChangeInAngle;

        if (f < -maxChangeInAngle)
            f = -maxChangeInAngle;

        return oldAngle + f;
    }

    public static void handleClientSpawnEntity(Minecraft client, ClientboundAddEntityPacket packet) {
        double d = packet.getX();
        double e = packet.getY();
        double f = packet.getZ();
        EntityType<?> entityType = packet.getType();
        ClientLevel level = client.level;

        if (level != null) {
            Entity entity15 = entityType.create(level);

            if (entity15 != null) {
                int i = packet.getId();
                entity15.syncPacketPositionCodec(d, e, f);
                entity15.moveTo(d, e, f);
                entity15.setXRot((packet.getXRot() * 360) / 256.0F);
                entity15.setYRot((packet.getYRot() * 360) / 256.0F);
                entity15.setId(i);
                entity15.setUUID(packet.getUUID());
                level.putNonPlayerEntity(i, entity15);
            }
        }
    }

    public static void renderBillboard(
            PoseStack poseStack,
            MultiBufferSource buffer,
            int i,
            EntityRenderDispatcher dispatcher,
            RenderType type,
            Quaternionf rotation
    ) {
        poseStack.pushPose();
        poseStack.mulPose(dispatcher.cameraOrientation());
        poseStack.mulPose(new Quaternionf().rotateY((float) Math.toRadians(180)));
        poseStack.mulPose(rotation);
        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix4f = pose.pose();
        Matrix3f matrix3f = pose.normal();
        VertexConsumer vertexConsumer = buffer.getBuffer(type);
        produceVertex(vertexConsumer, matrix4f, matrix3f, i, 0.0f, 0, 0, 1);
        produceVertex(vertexConsumer, matrix4f, matrix3f, i, 1.0f, 0, 1, 1);
        produceVertex(vertexConsumer, matrix4f, matrix3f, i, 1.0f, 1, 1, 0);
        produceVertex(vertexConsumer, matrix4f, matrix3f, i, 0.0f, 1, 0, 0);
        poseStack.popPose();
    }

    public static void produceVertex(
            VertexConsumer vertexConsumer,
            Matrix4f modelMatrix,
            Matrix3f normalMatrix,
            int light,
            float x,
            int y,
            int textureU,
            int textureV
    ) {
        vertexConsumer.vertex(modelMatrix, x - 0.5f, (float) y - 0.25f, 0.0f)
                .color(255, 255, 255, 255)
                .uv((float) textureU, (float) textureV)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(light)
                .normal(normalMatrix, 0.0f, 1.0f, 0.0f)
                .endVertex();
    }

    public static boolean hasDirectLineOfSight(Vec3 to, Vec3 from, BlockGetter level, Entity entity) {
        ClipContext context = new ClipContext(
                to,
                from,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                entity
        );
        return level.clip(context).getType() == HitResult.Type.MISS;
    }

    public static int getBlockLight(Entity entity, BlockPos blockPos) {
        if (entity.isOnFire())
            return 15;
        else
            return entity.level().getBrightness(LightLayer.BLOCK, blockPos);
    }

    public static void awardExperience(int amount, Vec3 pos, Level level) {
        int amt = amount;
        while (amt > 0) {
            int i = ExperienceOrb.getExperienceValue(amt);
            amt -= i;
            level.addFreshEntity(new ExperienceOrb(level, pos.x, pos.y, pos.z, i));
        }
    }

    public static Vector3f @NotNull [] buildFlatGeometry(
            @NotNull Camera camera, float tickDelta,
            double prevPosX,
            double prevPosY,
            double prevPosZ,
            double x,
            double y,
            double z,
            float scale,
            float rotation
    ) {
        Vec3 vec3 = camera.getPosition();
        float f = (float) (Mth.lerp(tickDelta, prevPosX, x) - vec3.x());
        float g = (float) (Mth.lerp(tickDelta, prevPosY, y) - vec3.y());
        float h = (float) (Mth.lerp(tickDelta, prevPosZ, z) - vec3.z());

        Vector3f[] vector3fs = new Vector3f[]{
                new Vector3f(-1.0f, 0.0f, -1.0f),
                new Vector3f(-1.0f, 0.0f, 1.0f),
                new Vector3f(1.0f, 0.0f, 1.0f),
                new Vector3f(1.0f, 0.0f, -1.0f)
        };

        for (int k = 0; k <= 3; k++) {
            Vector3f vector3f2 = vector3fs[k];
            vector3f2.rotate(Axis.YP.rotationDegrees(rotation));
            vector3f2.mul(scale);
            vector3f2.add(f, g, h);
        }

        return vector3fs;
    }

    public static Vector3f[] buildBillBoardGeometry(
            @NotNull Camera camera, float tickDelta,
            double prevPosX,
            double prevPosY,
            double prevPosZ,
            double x,
            double y,
            double z,
            float scale,
            float rotation
    ) {
        Vec3 vec3 = camera.getPosition();
        float f = (float) (Mth.lerp(tickDelta, prevPosX, x) - vec3.x());
        float g = (float) (Mth.lerp(tickDelta, prevPosY, y) - vec3.y());
        float h = (float) (Mth.lerp(tickDelta, prevPosZ, z) - vec3.z());
        Quaternionf quaternion2 = camera.rotation();

        Vector3f[] vector3fs = {
                new Vector3f(-1.0f, -1.0f, 0.0f),
                new Vector3f(-1.0f, 1.0f, 0.0f),
                new Vector3f(1.0f, 1.0f, 0.0f),
                new Vector3f(1.0f, -1.0f, 0.0f)
        };

        for (int k = 0; k <= 3; k++){
            Vector3f vector3f2 = vector3fs[k];
            vector3f2.rotate(Axis.ZP.rotationDegrees(rotation));
            vector3f2.rotate(quaternion2);
            vector3f2.mul(scale);
            vector3f2.add(f, g, h);
        }

        return vector3fs;
    }

    public static boolean destroyBlocks(Entity entity, AABB aabb) {
        int i = Mth.floor(aabb.minX);
        int j = Mth.floor(aabb.minY);
        int k = Mth.floor(aabb.minZ);
        int l = Mth.floor(aabb.maxX);
        int m = Mth.floor(aabb.maxY);
        int n = Mth.floor(aabb.maxZ);
        boolean bl = false;
        boolean bl2 = false;
        for (int o = i; o <= l; o++)
            for (int p = j; p <= m; p++)
                for (int q = k; q <= n; q++) {
                    BlockPos blockPos = new BlockPos(o, p, q);
                    BlockState blockState = entity.level().getBlockState(blockPos);

                    if (!blockState.isAir() && blockState.getBlock() == Blocks.FIRE)
                        if (entity.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) && !blockState.is(BlockTags.WITHER_IMMUNE))
                            bl2 = entity.level().destroyBlock(blockPos, false) || bl2;
                        else
                            bl = true;
                }
        return bl;
    }

    public static void renderBeam(LivingEntity actor, Vec3 target, Vec3 prevTarget, float partialTicks, Vec3 color, PoseStack poseStack, MultiBufferSource buffer, RenderType renderType){
        float j = actor.level().getGameTime() + partialTicks;
        float k = j % 1.0F;
        float l = actor.getEyeHeight();
        poseStack.pushPose();
        poseStack.translate(0.0, l, 0.0);
        Vec3 vec3 = MathUtils.lerpVec(partialTicks, prevTarget, target);
        Vec3 vec32 = fromLerpedPosition(actor, l, partialTicks);
        Vec3 vec33 = vec3.subtract(vec32);
        float m = (float) vec33.length();
        vec33 = vec33.normalize();
        float n = (float) Math.acos(vec33.y);
        float o = (float) Math.atan2(vec33.z, vec33.x);
        poseStack.mulPose(Axis.YP.rotationDegrees((1.5707964f - o) * 57.295776f));
        poseStack.mulPose(Axis.XP.rotationDegrees(n * 57.295776f));

        float q = j * 0.05F * -1.5F;
        int red = (int) (color.x() * 255);
        int green = (int) (color.y() * 255);
        int blue = (int) (color.z() * 255);
        float x = Mth.cos(q + 2.3561945F) * 0.282F;
        float y = Mth.sin(q + 2.3561945F) * 0.282F;
        float z = Mth.cos(q + 0.7853982F) * 0.282F;
        float aa = Mth.sin(q + 0.7853982F) * 0.282F;
        float ab = Mth.cos(q + 3.926991F) * 0.282F;
        float ac = Mth.sin(q + 3.926991F) * 0.282F;
        float ad = Mth.cos(q + 5.4977875F) * 0.282F;
        float ae = Mth.sin(q + 5.4977875F) * 0.282F;
        float af = Mth.cos(q + 3.1415927F) * 0.2F;
        float ag = Mth.sin(q + 3.1415927F) * 0.2F;
        float ah = Mth.cos(q + 0.0F) * 0.2F;
        float ai = Mth.sin(q + 0.0F) * 0.2F;
        float aj = Mth.cos(q + 1.5707964F) * 0.2F;
        float ak = Mth.sin(q + 1.5707964F) * 0.2F;
        float al = Mth.cos(q + 4.712389F) * 0.2F;
        float am = Mth.sin(q + 4.712389F) * 0.2F;
        float aq = -1.0F - k; // Negated K to reverse direction of laser movement
        float ar = m * 2.5f + aq;
        VertexConsumer vertexConsumer = buffer.getBuffer(renderType);
        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix4f = pose.pose();
        Matrix3f matrix3f = pose.normal();

        vertex(vertexConsumer, matrix4f, matrix3f, af, m, ag, red, green, blue, 0.4999f, ar);
        vertex(vertexConsumer, matrix4f, matrix3f, af, 0.0f, ag, red, green, blue, 0.4999f, aq);
        vertex(vertexConsumer, matrix4f, matrix3f, ah, 0.0f, ai, red, green, blue, 0.0f, aq);
        vertex(vertexConsumer, matrix4f, matrix3f, ah, m, ai, red, green, blue, 0.0f, ar);
        vertex(vertexConsumer, matrix4f, matrix3f, aj, m, ak, red, green, blue, 0.4999f, ar);
        vertex(vertexConsumer, matrix4f, matrix3f, aj, 0.0f, ak, red, green, blue, 0.4999f, aq);
        vertex(vertexConsumer, matrix4f, matrix3f, al, 0.0f, am, red, green, blue, 0.0f, aq);
        vertex(vertexConsumer, matrix4f, matrix3f, al, m, am, red, green, blue, 0.0f, ar);

        float as = 0.0F;
        if (actor.tickCount % 2 == 0)
            as = 0.5F;

        vertex(vertexConsumer, matrix4f, matrix3f, x, m, y, red, green, blue, 0.5f, as + 0.5f);
        vertex(
                vertexConsumer,
                matrix4f,
                matrix3f,
                z,
                m,
                aa,
                red,
                green,
                blue,
                1.0f,
                as + 0.5f
            );
        vertex(vertexConsumer, matrix4f, matrix3f, ad, m, ae, red, green, blue, 1.0f, as);
        vertex(vertexConsumer, matrix4f, matrix3f, ab, m, ac, red, green, blue, 0.5f, as);
        poseStack.popPose();
    }

    public static void vertex(
            VertexConsumer vertexConsumer,
            Matrix4f matrix4f,
            Matrix3f matrix3f,
            float f,
            float g,
            float h,
            int i,
            int j,
            int k,
            float l,
            float m
    ) {
        vertexConsumer.vertex(matrix4f, f, g, h)
                .color(i, j, k, 255)
                .uv(l, m)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(15728800)
                .normal(matrix3f, 0.0F, 0.0F, -1.0F) // Changed from normal(0, 1, 0) because that brightened it for some reason that I cannot fathom with my pathetic opengl knowledge
                .endVertex();
    }

    public static Vec3 fromLerpedPosition(LivingEntity entity, double yOffset, float delta) {
        double d = Mth.lerp(delta, entity.xOld, entity.getX());
        double e = Mth.lerp(delta, entity.yOld, entity.getY()) + yOffset;
        double f = Mth.lerp(delta, entity.zOld, entity.getZ());
        return new Vec3(d, e, f);
    }

    public static void onBreakInCreative(Level level, BlockPos pos, BlockState state, Player player) {
        DoubleBlockHalf doubleBlockHalf = state.getValue(DoublePlantBlock.HALF);

        if (doubleBlockHalf == DoubleBlockHalf.UPPER) {
            BlockPos blockPos = pos.below();
            BlockState blockState = level.getBlockState(blockPos);

            if (blockState.getBlock() == state.getBlock() && blockState.getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.LOWER) {
                level.setBlock(blockPos, Blocks.AIR.defaultBlockState(), 35);
                level.levelEvent(player, 2001, blockPos, Block.getId(blockState));
            }
        }
    }
}
