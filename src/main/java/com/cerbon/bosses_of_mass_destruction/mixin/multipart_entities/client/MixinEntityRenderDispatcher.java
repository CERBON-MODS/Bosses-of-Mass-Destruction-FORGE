package com.cerbon.bosses_of_mass_destruction.mixin.multipart_entities.client;

import com.cerbon.bosses_of_mass_destruction.api.multipart_entities.util.CompoundOrientedBox;
import com.cerbon.bosses_of_mass_destruction.api.multipart_entities.util.OrientedBox;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public class MixinEntityRenderDispatcher {

    @Inject(method = "renderHitbox", at = @At("RETURN"))
    private static void drawOrientedBoxes(PoseStack matrix, VertexConsumer vertices, Entity entity, float tickDelta, CallbackInfo ci) {
        final AABB box = entity.getBoundingBox();
        if (box instanceof final CompoundOrientedBox compoundOrientedBox) {
            matrix.pushPose();
            matrix.translate(-entity.getX(), -entity.getY(), -entity.getZ());
            for (final OrientedBox orientedBox : compoundOrientedBox) {
                matrix.pushPose();
                final Vec3 center = orientedBox.getCenter();
                matrix.translate(center.x, center.y, center.z);
                matrix.mulPose(orientedBox.getRotation().toFloatQuat());
                LevelRenderer.renderLineBox(matrix, vertices, orientedBox.getExtents(), 0, 0, 1, 1);
                matrix.popPose();
            }
            compoundOrientedBox.toVoxelShape().forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> LevelRenderer.renderLineBox(matrix, vertices, minX, minY, minZ, maxX, maxY, maxZ, 0, 1, 0, 1f));
            matrix.popPose();
        }
    }
}
