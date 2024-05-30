package com.cerbon.bosses_of_mass_destruction.entity.util;

import com.cerbon.bosses_of_mass_destruction.client.render.IOverlayOverride;
import com.cerbon.bosses_of_mass_destruction.client.render.IRenderLight;
import com.cerbon.bosses_of_mass_destruction.client.render.IRenderer;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.util.Color;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.GeoModelProvider;
import software.bernie.geckolib3.model.provider.data.EntityModelData;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class SimpleGeoRenderer<T extends Entity & IAnimatable> extends EntityRenderer<T> implements IGeoRenderer<T> {
    private final AnimatedGeoModel<T> modelProvider;
    private final IRenderer<T> postRenderers;
    private final IRenderer<T> preRenderers;
    private final IRenderLight<T> brightness;
    private final IOverlayOverride overlayOverride;

    private IRenderTypeBuffer provider;
    private final List<GeoLayerRenderer<T>> layerRenderers = new ArrayList<>();

    public SimpleGeoRenderer(EntityRendererManager context, AnimatedGeoModel<T> modelProvider, IRenderer<T> postRenderers, IRenderer<T> preRenderers, IRenderLight<T> brightness, IOverlayOverride overlayOverride) {
        super(context);
        this.modelProvider = modelProvider;
        this.postRenderers = postRenderers;
        this.preRenderers = preRenderers;
        this.brightness = brightness;
        this.overlayOverride = overlayOverride;
    }

    @Override
    protected int getBlockLightLevel(@Nonnull T entity, @Nonnull BlockPos pos) {
        return brightness != null ? brightness.getBlockLight(entity,pos) : super.getBlockLightLevel(entity, pos);
    }

    @Override
    public void render(T entity, float entityYaw, float partialTick, @Nonnull MatrixStack poseStack, @Nonnull IRenderTypeBuffer buffer, int packedLight) {
        boolean shouldSit = entity.isPassenger() && entity.getVehicle() != null;
        EntityModelData entityModelData = new EntityModelData();
        entityModelData.isSitting = shouldSit;
        float f = MathHelper.rotLerp(partialTick, entity.yRotO, entity.yRot);
        float f1 = 0.0f;
        float f2 = f1 - f;
        float f7;
        if (shouldSit && entity.getVehicle() instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) entity.getVehicle();

            f = MathHelper.rotLerp(partialTick, livingEntity.yBodyRotO, livingEntity.yBodyRot);
            f2 = f1 - f;
            f7 = MathHelper.wrapDegrees(f2);
            if (f7 < -85.0f) {
                f7 = -85.0f;
            }
            if (f7 >= 85.0f) {
                f7 = 85.0f;
            }
            f = f1 - f7;
            if (f7 * f7 > 2500.0f) {
                f += f7 * 0.2f;
            }
            f2 = f1 - f;
        }
        float f6 = MathHelper.lerp(partialTick, entity.xRotO, entity.xRot);
        f7 = handleRotationFloat(entity, partialTick);
        applyRotations(entity, poseStack, f);
        float limbSwingAmount = 0.0f;
        float limbSwing = 0.0f;
        AnimationEvent<?> predicate= new AnimationEvent<>(entity,
                limbSwing,
                limbSwingAmount,
                partialTick,
                false,
                Lists.newArrayList(entityModelData));
        modelProvider.setLivingAnimations(entity, getUniqueID(entity), predicate);
        poseStack.pushPose();
        if (preRenderers != null) preRenderers.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
        poseStack.translate(0.0, 0.009999999776482582, 0.0);
        Minecraft.getInstance().textureManager.bind(getTextureLocation(entity));
        GeoModel model = modelProvider.getModel(modelProvider.getModelLocation(entity));
        Color renderColor = Color.ofRGBA(255, 255, 255, 255);
        RenderType renderType = getRenderType(entity, partialTick, poseStack, buffer, null , packedLight, getTextureLocation(entity));
        this.render(model,
                entity,
                partialTick,
                renderType,
                poseStack,
                buffer,
                null ,
                packedLight,
                overlayOverride != null ? overlayOverride.getOverlay() :getPackedOverlay(),
                (float) renderColor.getRed() / 255.0f,
                (float) renderColor.getBlue() / 255.0f,
                (float) renderColor.getGreen() / 255.0f,
                (float) renderColor.getAlpha() / 255.0f);

        if (!entity.isSpectator()) {
            for (GeoLayerRenderer<T> next : layerRenderers) {
                next.render(poseStack,
                        buffer,
                        packedLight,
                        entity,
                        limbSwing,
                        limbSwingAmount,
                        partialTick,
                        f7,
                        f2,
                        f6
                );
            }
        }
        poseStack.popPose();
        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
        if (postRenderers != null) postRenderers.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
    }

    private void applyRotations(T entityLiving, MatrixStack poseStack, float rotationYaw){
        Pose pose = entityLiving.getPose();
        if (pose != Pose.SLEEPING)
            poseStack.mulPose(Vector3f.YP.rotationDegrees(rotationYaw));
    }

    private float handleRotationFloat(T livingBase, float partialTicks){
        return (float) livingBase.tickCount + partialTicks;
    }

    @Override
    public GeoModelProvider<T> getGeoModelProvider() {
        return modelProvider;
    }

    private int getPackedOverlay(){
        return OverlayTexture.pack(OverlayTexture.u((float) 0.0), false);
    }

    @Override
    public void setCurrentRTB(IRenderTypeBuffer bufferSource) {
        provider = bufferSource;
    }

    @Override
    public IRenderTypeBuffer getCurrentRTB() {
        return provider;
    }

    @Override
    public @Nonnull ResourceLocation getTextureLocation(@Nonnull T entity) {
        return modelProvider.getTextureLocation(entity);
    }
}
