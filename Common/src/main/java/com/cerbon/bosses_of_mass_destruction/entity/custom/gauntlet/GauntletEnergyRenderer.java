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
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoCube;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class GauntletEnergyRenderer implements IRendererWithModel, IRenderer<GauntletEntity> {
    private final GeoModel<GauntletEntity> geoModel;
    private final EntityRendererProvider.Context context;

    private final ResourceLocation armorTexture = new ResourceLocation(BMDConstants.MOD_ID, "textures/entity/obsidilith_armor.png");
    private RenderHelper geoModelProvider;
    private GauntletEntity gauntletEntity;
    private RenderType type;

    public GauntletEnergyRenderer(GeoModel<GauntletEntity> geoModel, EntityRendererProvider.Context context) {
        this.geoModel = geoModel;
        this.context = context;
    }

    @Override
    public void render(GauntletEntity entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int light) {
        float renderAge = entity.tickCount + partialTicks;
        float textureOffset = renderAge * 0.01f;

        if (geoModelProvider == null)
            geoModelProvider = new RenderHelper(entity, geoModel, context);

        gauntletEntity = entity;
        type = RenderType.energySwirl(armorTexture, textureOffset, textureOffset);
    }

    @Override
    public void render(BakedGeoModel model, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        VertexConsumer energyBuffer = buffer.getBuffer(type);
        if (gauntletEntity == null) return;
        if (type == null) return;
        float renderAlpha = gauntletEntity.energyShieldHandler.getRenderAlpha();
        if (renderAlpha == 0.0f) return;
        float lerpedAlpha = Mth.lerp(partialTicks, renderAlpha - 0.1f, renderAlpha);

        if (geoModelProvider == null) return;
        geoModelProvider.actuallyRender(
                poseStack,
                gauntletEntity,
                model,
                type,
                buffer,
                energyBuffer,
                false,
                partialTicks,
                packedLightIn,
                OverlayTexture.NO_OVERLAY,
                0.8f * lerpedAlpha,
                0.2f * lerpedAlpha,
                0.2f * lerpedAlpha,
                lerpedAlpha
        );
    }

    private static class RenderHelper extends GeoEntityRenderer<GauntletEntity> {
        private final GauntletEntity gauntletEntity;

        public RenderHelper(GauntletEntity gauntletEntity, GeoModel<GauntletEntity> parentModel, EntityRendererProvider.Context context) {
            super(context, parentModel);
            this.gauntletEntity = gauntletEntity;
        }

        @Override
        public void renderCube(PoseStack poseStack, GeoCube cube, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
            poseStack.pushPose();
            poseStack.scale(1.1f, 1.05f, 1.1f);
            super.renderCube(poseStack, cube, buffer, IBoneLight.fullbright, packedOverlay, red, green, blue, alpha);
            poseStack.popPose();
        }

        @Override
        public GauntletEntity getAnimatable() {
            return gauntletEntity;
        }
    }
}
