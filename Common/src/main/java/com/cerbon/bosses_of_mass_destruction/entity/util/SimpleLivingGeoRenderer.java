package com.cerbon.bosses_of_mass_destruction.entity.util;

import com.cerbon.bosses_of_mass_destruction.client.render.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector4f;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SimpleLivingGeoRenderer<T extends Entity & GeoAnimatable> extends GeoEntityRenderer<T> {
    private final IRenderLight<T> brightness;
    private final IBoneLight iBoneLight;
    private final IRenderer<T> renderer;
    private final IRendererWithModel renderWithModel;
    private final IOverlayOverride overlayOverride;
    private final boolean deathRotation;

    public SimpleLivingGeoRenderer(EntityRendererProvider.Context renderManager, GeoModel<T> model, IRenderLight<T> brightness, IBoneLight iBoneLight, IRenderer<T> renderer, IRendererWithModel renderWithModel, IOverlayOverride overlayOverride, boolean deathRotation) {
        super(renderManager, model);
        this.brightness = brightness;
        this.iBoneLight = iBoneLight;
        this.renderer = renderer;
        this.renderWithModel = renderWithModel;
        this.overlayOverride = overlayOverride;
        this.deathRotation = deathRotation;
    }

    @Override
    protected int getBlockLightLevel(@NotNull T entity, @NotNull BlockPos pos) {
        return brightness != null ? brightness.getBlockLight(entity, pos) : super.getBlockLightLevel(entity, pos);
    }

    @Override
    public void renderRecursively(PoseStack poseStack, T animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        int packedLight1 = iBoneLight != null ? iBoneLight.getLightForBone(bone, packedLight) : packedLight;
        int red = FastColor.ABGR32.red(colour);
        int green = FastColor.ABGR32.green(colour);
        int blue = FastColor.ABGR32.blue(colour);
        int alpha = FastColor.ABGR32.alpha(colour);
        Vector4f color = new Vector4f(red, green, blue, alpha).mul(1 / 255f);
        Vector4f newColor = (iBoneLight != null ? iBoneLight.getColorForBone(bone, color) : color).mul(255f);
        super.renderRecursively(
                poseStack,
                animatable,
                bone,
                renderType,
                bufferSource,
                buffer,
                isReRender,
                partialTick,
                packedLight1,
                packedOverlay,
                FastColor.ABGR32.color((int) newColor.w, (int) newColor.x, (int) newColor.y, (int) newColor.z)
        );
    }

    @Override
    public void render(
            T entity,
            float entityYaw,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight
    ) {
        if (renderer != null) renderer.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        poseStack.pushPose();
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        poseStack.popPose();
    }

    @Override
    public void actuallyRender(PoseStack poseStack, T animatable, BakedGeoModel model, @Nullable RenderType renderType, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
        int packetOverlay = overlayOverride != null ? overlayOverride.getOverlay() : packedOverlay;
        if (renderWithModel != null) renderWithModel.render(model, partialTick, poseStack, bufferSource, packedLight, packetOverlay, FastColor.ABGR32.red(colour), FastColor.ABGR32.green(colour), FastColor.ABGR32.blue(colour), FastColor.ABGR32.alpha(colour));
    }

    @Override
    public int getPackedOverlay(T animatable, float u, float partialTick) {
        return overlayOverride != null ? overlayOverride.getOverlay() : super.getPackedOverlay(animatable, u, partialTick);
    }

    @Override
    protected float getDeathMaxRotation(T animatable) {
        return deathRotation ? 90f : 0f;
    }
}
