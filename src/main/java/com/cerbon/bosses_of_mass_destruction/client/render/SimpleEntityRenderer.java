package com.cerbon.bosses_of_mass_destruction.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;

public class SimpleEntityRenderer<T extends Entity> extends EntityRenderer<T> {
    private final IRenderer<T> renderer;
    private final ITextureProvider<T> textureProvider;
    private final IRenderLight<T> brightness;

    public SimpleEntityRenderer(EntityRendererManager renderManager, IRenderer<T> renderer, ITextureProvider<T> textureProvider, IRenderLight<T> brightness) {
        super(renderManager);
        this.renderer = renderer;
        this.textureProvider = textureProvider;
        this.brightness = brightness;
    }

    @Override
    public void render(
            @Nonnull T entity,
            float entityYaw,
            float partialTick,
            @Nonnull MatrixStack poseStack,
            @Nonnull IRenderTypeBuffer buffer,
            int packedLight
    ) {
        renderer.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
    }

    @Override
    public @Nonnull ResourceLocation getTextureLocation(@Nonnull T entity) {
        return textureProvider.getTexture(entity);
    }

    @Override
    protected int getBlockLightLevel(@Nonnull T entity, @Nonnull BlockPos pos) {
        return brightness != null ? brightness.getBlockLight(entity, pos) : super.getBlockLightLevel(entity, pos);
    }
}

