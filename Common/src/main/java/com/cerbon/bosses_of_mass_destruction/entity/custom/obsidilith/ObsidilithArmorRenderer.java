package com.cerbon.bosses_of_mass_destruction.entity.custom.obsidilith;

import com.cerbon.bosses_of_mass_destruction.client.render.IBoneLight;
import com.cerbon.bosses_of_mass_destruction.client.render.IRenderer;
import com.cerbon.bosses_of_mass_destruction.client.render.IRendererWithModel;
import com.cerbon.bosses_of_mass_destruction.util.BMDConstants;
import com.cerbon.cerbons_api.api.static_utilities.Vec3Colors;
import com.cerbon.cerbons_api.api.static_utilities.VecUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoCube;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import java.util.Random;

public class ObsidilithArmorRenderer implements IRendererWithModel, IRenderer<ObsidilithEntity> {
    private final GeoModel<ObsidilithEntity> geoModel;
    private final EntityRendererProvider.Context context;

    private final ResourceLocation armorTexture = ResourceLocation.fromNamespaceAndPath(BMDConstants.MOD_ID, "textures/entity/obsidilith_armor.png");
    private RenderHelper geoModelProvider;
    private ObsidilithEntity obsidilithEntity;
    private RenderType type;

    public ObsidilithArmorRenderer(GeoModel<ObsidilithEntity> geoModel, EntityRendererProvider.Context context){
        this.geoModel = geoModel;
        this.context = context;
    }

    @Override
    public void render(ObsidilithEntity entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int light) {
        float renderAge = entity.tickCount + partialTicks;
        float textureOffset = renderAge * new Random().nextFloat();

        if (geoModelProvider == null)
            geoModelProvider = new RenderHelper(entity, geoModel, context);

        this.obsidilithEntity = entity;
        type = RenderType.energySwirl(armorTexture, textureOffset, textureOffset);
    }

    @Override
    public void render(BakedGeoModel model, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        VertexConsumer energyBuffer = buffer.getBuffer(type);
        if(obsidilithEntity == null) return;
        if (type == null) return;

        if (obsidilithEntity.isShielded()){
            Vec3 color = getColor().add(VecUtils.unit).normalize().scale(0.6);
            var colour = FastColor.ARGB32.colorFromFloat(1.0f, (float) color.x, (float) color.y, (float) color.z);

            if (geoModelProvider == null) return;
            geoModelProvider.actuallyRender(
                    poseStack,
                    obsidilithEntity,
                    model,
                    type,
                    buffer,
                    energyBuffer,
                    false,
                    partialTicks,
                    packedLightIn,
                    OverlayTexture.NO_OVERLAY,
                    colour
            );
        }
    }

    private Vec3 getColor() {
        Vec3 color;
        switch (obsidilithEntity.currentAttack){
            case ObsidilithUtils.burstAttackStatus -> color = Vec3Colors.ORANGE;
            case ObsidilithUtils.waveAttackStatus -> color = Vec3Colors.RED;
            case ObsidilithUtils.spikeAttackStatus -> color = Vec3Colors.COMET_BLUE;
            case ObsidilithUtils.anvilAttackStatus -> color = Vec3Colors.ENDER_PURPLE;
            case ObsidilithUtils.pillarDefenseStatus -> color = Vec3Colors.WHITE;
            default -> color = Vec3Colors.WHITE;
        }
        return color;
    }

    private static class RenderHelper extends GeoEntityRenderer<ObsidilithEntity> {
        private final ObsidilithEntity entity;

        public RenderHelper(ObsidilithEntity entity, GeoModel<ObsidilithEntity> parentModel, EntityRendererProvider.Context context) {
            super(context, parentModel);
            this.entity = entity;
        }

        @Override
        public void renderCube(PoseStack poseStack, GeoCube cube, VertexConsumer buffer, int packedLight, int packedOverlay, int colour) {
            poseStack.pushPose();
            poseStack.scale(1.08f, 1.05f, 1.08f);
            super.renderCube(poseStack, cube, buffer, IBoneLight.fullbright, packedOverlay, colour);
            poseStack.popPose();
        }

        @Override
        public ObsidilithEntity getAnimatable() {
            return entity;
        }
    }
}
