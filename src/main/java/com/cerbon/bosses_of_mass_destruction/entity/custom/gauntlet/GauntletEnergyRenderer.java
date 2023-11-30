package com.cerbon.bosses_of_mass_destruction.entity.custom.gauntlet;

import com.cerbon.bosses_of_mass_destruction.client.render.IBoneLight;
import com.cerbon.bosses_of_mass_destruction.client.render.IRenderer;
import com.cerbon.bosses_of_mass_destruction.client.render.IRendererWithModel;
import com.cerbon.bosses_of_mass_destruction.util.BMDConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib3.geo.render.built.GeoCube;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class GauntletEnergyRenderer implements IRendererWithModel, IRenderer<GauntletEntity> {
    private final AnimatedGeoModel<GauntletEntity> geoModel;
    private final EntityRendererProvider.Context context;

    private final ResourceLocation armorTexture = new ResourceLocation(BMDConstants.MOD_ID, "textures/entity/obsidilith_armor.png");
    private RenderHelper geoModelProvider;
    private GauntletEntity gauntletEntity;
    private RenderType type;

    public GauntletEnergyRenderer(AnimatedGeoModel<GauntletEntity> geoModel, EntityRendererProvider.Context context) {
        this.geoModel = geoModel;
        this.context = context;
    }

    @Override
    public void render(GauntletEntity entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int light) {
        float renderAge = entity.tickCount + partialTicks;
        float textureOffset = renderAge * 0.01f;

        if (geoModelProvider == null)
            geoModelProvider = new RenderHelper(geoModel, context);

        gauntletEntity = entity;
        type = RenderType.energySwirl(armorTexture, textureOffset, textureOffset);
    }

    @Override
    public void render(GeoModel model, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        VertexConsumer energyBuffer = buffer.getBuffer(type);
        if (gauntletEntity == null) return;
        if (type == null) return;
        float renderAlpha = gauntletEntity.energyShieldHandler.getRenderAlpha();
        if (renderAlpha == 0.0f) return;
        float lerpedAlpha = Mth.lerp(partialTicks, renderAlpha - 0.1f, renderAlpha);

        if (geoModelProvider == null) return;
        geoModelProvider.render(
                model,
                gauntletEntity,
                partialTicks,
                type,
                poseStack,
                buffer,
                energyBuffer,
                packedLightIn,
                OverlayTexture.NO_OVERLAY,
                0.8f * lerpedAlpha,
                0.2f * lerpedAlpha,
                0.2f * lerpedAlpha,
                lerpedAlpha
        );
    }

    private static class RenderHelper extends GeoEntityRenderer<GauntletEntity> {

        public RenderHelper(AnimatedGeoModel<GauntletEntity> parentModel, EntityRendererProvider.Context context) {
            super(context, parentModel);
        }

        @Override
        public void renderCube(GeoCube cube, PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
            poseStack.pushPose();
            poseStack.scale(1.1f, 1.05f, 1.1f);
            super.renderCube(cube, poseStack, buffer, IBoneLight.fullbright, packedOverlay, red, green, blue, alpha);
            poseStack.popPose();
        }
    }
}
