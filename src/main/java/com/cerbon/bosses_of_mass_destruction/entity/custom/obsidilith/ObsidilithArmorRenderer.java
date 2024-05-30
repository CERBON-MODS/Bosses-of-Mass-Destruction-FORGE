package com.cerbon.bosses_of_mass_destruction.entity.custom.obsidilith;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.VecUtils;
import com.cerbon.bosses_of_mass_destruction.client.render.IBoneLight;
import com.cerbon.bosses_of_mass_destruction.client.render.IRenderer;
import com.cerbon.bosses_of_mass_destruction.client.render.IRendererWithModel;
import com.cerbon.bosses_of_mass_destruction.util.BMDColors;
import com.cerbon.bosses_of_mass_destruction.util.BMDConstants;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import software.bernie.geckolib3.geo.render.built.GeoCube;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

import java.util.Random;

public class ObsidilithArmorRenderer implements IRendererWithModel, IRenderer<ObsidilithEntity> {
    private final AnimatedGeoModel<ObsidilithEntity> geoModel;
    private final EntityRendererManager context;

    private final ResourceLocation armorTexture = new ResourceLocation(BMDConstants.MOD_ID, "textures/entity/obsidilith_armor.png");
    private RenderHelper geoModelProvider;
    private ObsidilithEntity obsidilithEntity;
    private RenderType type;

    public ObsidilithArmorRenderer(AnimatedGeoModel<ObsidilithEntity> geoModel, EntityRendererManager context){
        this.geoModel = geoModel;
        this.context = context;
    }

    @Override
    public void render(ObsidilithEntity entity, float yaw, float partialTicks, MatrixStack poseStack, IRenderTypeBuffer buffer, int light) {
        float renderAge = entity.tickCount + partialTicks;
        float textureOffset = renderAge * new Random().nextFloat();

        if (geoModelProvider == null)
            geoModelProvider = new RenderHelper(geoModel, context);

        this.obsidilithEntity = entity;
        type = RenderType.energySwirl(armorTexture, textureOffset, textureOffset);
    }

    @Override
    public void render(GeoModel model, float partialTicks, MatrixStack poseStack, IRenderTypeBuffer buffer, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        IVertexBuilder energyBuffer = buffer.getBuffer(type);
        if(obsidilithEntity == null) return;
        if (type == null) return;

        if (obsidilithEntity.isShielded()){
            Vector3d color = getColor().add(VecUtils.unit).normalize().scale(0.6);

            if (geoModelProvider == null) return;
            geoModelProvider.render(
                    model,
                    obsidilithEntity,
                    partialTicks,
                    type,
                    poseStack,
                    buffer,
                    energyBuffer,
                    packedLightIn,
                    OverlayTexture.NO_OVERLAY,
                    (float) color.x, (float) color.y, (float) color.z, 1.0f
            );
        }
    }

    private Vector3d getColor() {
        Vector3d color;
        switch (obsidilithEntity.currentAttack){
            case ObsidilithUtils.burstAttackStatus: color = BMDColors.ORANGE;
            case ObsidilithUtils.waveAttackStatus: color = BMDColors.RED;
            case ObsidilithUtils.spikeAttackStatus: color = BMDColors.COMET_BLUE;
            case ObsidilithUtils.anvilAttackStatus:color = BMDColors.ENDER_PURPLE;
            case ObsidilithUtils.pillarDefenseStatus: color = BMDColors.WHITE;
            default: color = BMDColors.WHITE;
        }
        return color;
    }

    private static class RenderHelper extends GeoEntityRenderer<ObsidilithEntity> {

        public RenderHelper(AnimatedGeoModel<ObsidilithEntity> parentModel, EntityRendererManager context) {
            super(context, parentModel);
        }

        @Override
        public void renderCube(GeoCube cube, MatrixStack poseStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
            poseStack.pushPose();
            poseStack.scale(1.08f, 1.05f, 1.08f);
            super.renderCube(cube, poseStack, buffer, IBoneLight.fullbright, packedOverlay, red, green, blue, alpha);
            poseStack.popPose();
        }
    }
}
