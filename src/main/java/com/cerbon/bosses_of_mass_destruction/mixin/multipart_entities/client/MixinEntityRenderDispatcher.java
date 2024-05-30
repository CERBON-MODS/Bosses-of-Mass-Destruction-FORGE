package com.cerbon.bosses_of_mass_destruction.mixin.multipart_entities.client;

import com.cerbon.bosses_of_mass_destruction.api.multipart_entities.util.CompoundOrientedBox;
import com.cerbon.bosses_of_mass_destruction.api.multipart_entities.util.OrientedBox;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRendererManager.class)
public class MixinEntityRenderDispatcher {

    @Inject(method = "renderHitbox", at = @At("RETURN"))
    private void drawOrientedBoxes(MatrixStack matrix, IVertexBuilder vertices, Entity entity, float tickDelta, CallbackInfo ci) {
        final AxisAlignedBB box = entity.getBoundingBox();
        if (box instanceof CompoundOrientedBox) {
            CompoundOrientedBox compoundOrientedBox = (CompoundOrientedBox) box;

            matrix.pushPose();
            matrix.translate(-entity.getX(), -entity.getY(), -entity.getZ());

            for (final OrientedBox orientedBox : compoundOrientedBox) {
                matrix.pushPose();
                final Vector3d center = orientedBox.getCenter();
                matrix.translate(center.x, center.y, center.z);
                matrix.mulPose(orientedBox.getRotation().toFloatQuat());
                WorldRenderer.renderLineBox(matrix, vertices, orientedBox.getExtents(), 0, 0, 1, 1);
                matrix.popPose();
            }

            compoundOrientedBox.toVoxelShape().forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> WorldRenderer.renderLineBox(matrix, vertices, minX, minY, minZ, maxX, maxY, maxZ, 0, 1, 0, 1f));
            matrix.popPose();
        }
    }
}
