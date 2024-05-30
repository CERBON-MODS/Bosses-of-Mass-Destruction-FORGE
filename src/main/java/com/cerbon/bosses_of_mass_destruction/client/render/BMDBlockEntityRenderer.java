package com.cerbon.bosses_of_mass_destruction.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector4f;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;

import javax.annotation.Nullable;

public class BMDBlockEntityRenderer<T extends TileEntity & IAnimatable> extends GeoBlockRenderer<T> {
    private final AnimatedGeoModel<T> model;
    private final IBoneLight iBoneLight;

    public BMDBlockEntityRenderer(TileEntityRendererDispatcher rendererProvider, AnimatedGeoModel<T> modelProvider, IBoneLight iBoneLight) {
        super(rendererProvider, modelProvider);
        this.model = modelProvider;
        this.iBoneLight = iBoneLight;
    }


    @Override
    public RenderType getRenderType(T animatable, float partialTick, MatrixStack poseStack, @Nullable IRenderTypeBuffer bufferSource, @Nullable IVertexBuilder buffer, int packedLight, ResourceLocation texture) {
        return RenderType.entityCutoutNoCull(model.getTextureLocation(animatable));
    }

    @Override
    public void renderRecursively(GeoBone bone, MatrixStack poseStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
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

