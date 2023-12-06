package com.cerbon.bosses_of_mass_destruction.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector4f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;

public class BMDBlockEntityRenderer<T extends BlockEntity & IAnimatable> extends GeoBlockRenderer<T> {
    private final AnimatedGeoModel<T> model;
    private final IBoneLight iBoneLight;

    public BMDBlockEntityRenderer(BlockEntityRendererProvider.Context rendererProvider, AnimatedGeoModel<T> modelProvider, IBoneLight iBoneLight) {
        super(rendererProvider, modelProvider);
        this.model = modelProvider;
        this.iBoneLight = iBoneLight;
    }


    @Override
    public RenderType getRenderType(T animatable, float partialTick, PoseStack poseStack, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, int packedLight, ResourceLocation texture) {
        return RenderType.entityCutoutNoCull(model.getTextureLocation(animatable));
    }

    @Override
    public void renderRecursively(GeoBone bone, PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        int packedLight1 = (iBoneLight != null) ? iBoneLight.getLightForBone(bone, packedLight) : packedLight;
        Vector4f color = new Vector4f(red, green, blue, alpha);
        Vector4f newColor = (iBoneLight != null) ? iBoneLight.getColorForBone(bone, color) : color;
        super.renderRecursively(
                bone,
                poseStack,
                buffer,
                packedLight1,
                packedOverlay,
                newColor.x(), newColor.y(), newColor.z(), newColor.w());
    }
}

