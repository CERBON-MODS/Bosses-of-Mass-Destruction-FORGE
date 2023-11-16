package com.cerbon.bosses_of_mass_destruction.entity.custom.obsidilith;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.VecUtils;
import com.cerbon.bosses_of_mass_destruction.client.render.IBoneLight;
import com.cerbon.bosses_of_mass_destruction.client.render.IRenderer;
import com.cerbon.bosses_of_mass_destruction.client.render.IRendererWithModel;
import com.cerbon.bosses_of_mass_destruction.util.BMDColors;
import com.cerbon.bosses_of_mass_destruction.util.BMDConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoCube;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import java.util.Random;

//TODO: Fix armor being opaque
public class ObsidilithArmorRenderer implements IRendererWithModel, IRenderer<ObsidilithEntity> {
    private final GeoModel<ObsidilithEntity> geoModel;
    private final EntityRendererProvider.Context context;

    private final ResourceLocation armorTexture = new ResourceLocation(BMDConstants.MOD_ID, "textures/entity/obsidilith_armor.png");
    private RenderHelper geoModelProvider = null;
    private VertexConsumer energyBuffer = null;
    private ObsidilithEntity entity = null;
    private RenderType type = null;

    public ObsidilithArmorRenderer(GeoModel<ObsidilithEntity> geoModel, EntityRendererProvider.Context context){
        this.geoModel = geoModel;
        this.context = context;
    }

    @Override
    public void render(ObsidilithEntity entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int light) {
        float renderAge = entity.tickCount + partialTicks;
        float textureOffset = renderAge * new Random().nextFloat();

        if (geoModelProvider == null)
            geoModelProvider = new RenderHelper(entity, context, geoModel);

        this.entity = entity;
        type = RenderType.energySwirl(armorTexture, textureOffset, textureOffset);
    }

    @Override
    public void render(BakedGeoModel model, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        energyBuffer = buffer.getBuffer(type);
        if(entity == null) return;
        if (type == null) return;

        if (entity.isShielded()){
            Vec3 color;
            switch (entity.currentAttack){
                case ObsidilithUtils.burstAttackStatus -> color = BMDColors.ORANGE;
                case ObsidilithUtils.waveAttackStatus -> color = BMDColors.RED;
                case ObsidilithUtils.spikeAttackStatus -> color = BMDColors.COMET_BLUE;
                case ObsidilithUtils.anvilAttackStatus -> color = BMDColors.ENDER_PURPLE;
                case ObsidilithUtils.pillarDefenseStatus -> color = BMDColors.WHITE;
                default -> color = BMDColors.WHITE;

            }
            color.add(VecUtils.unit).normalize().multiply(0.6, 0.6, 0.6);

            if (geoModelProvider == null) return;
            geoModelProvider.actuallyRender(
                    poseStack,
                    entity,
                    model,
                    type,
                    buffer,
                    energyBuffer,
                    false,
                    partialTicks,
                    packedLightIn,
                    OverlayTexture.NO_OVERLAY,
                    (float) color.x, (float) color.y, (float) color.z, 1.0f
            );
        }
    }

    private static class RenderHelper extends GeoEntityRenderer<ObsidilithEntity> {
        private final ObsidilithEntity entity;

        public RenderHelper(ObsidilithEntity entity, EntityRendererProvider.Context context, GeoModel<ObsidilithEntity> parentModel) {
            super(context, parentModel);
            this.entity = entity;
        }

        @Override
        public void renderCube(PoseStack poseStack, GeoCube cube, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
            poseStack.pushPose();
            poseStack.scale(1.08f, 1.05f, 1.08f);
            super.renderCube(poseStack, cube, buffer, IBoneLight.fullbright, packedOverlay, red, green, blue, alpha);
            poseStack.popPose();
        }

        @Override
        public ObsidilithEntity getAnimatable() {
            return entity;
        }
    }
}
