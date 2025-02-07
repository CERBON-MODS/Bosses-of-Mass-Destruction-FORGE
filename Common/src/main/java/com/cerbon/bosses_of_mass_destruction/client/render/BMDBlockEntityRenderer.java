package com.cerbon.bosses_of_mass_destruction.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector4f;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class BMDBlockEntityRenderer<T extends BlockEntity & GeoAnimatable> extends GeoBlockRenderer<T> {
    private final IBoneLight iBoneLight;

    public BMDBlockEntityRenderer(GeoModel<T> model, IBoneLight iBoneLight) {
        super(model);
        this.iBoneLight = iBoneLight;
    }

    @Override
    public RenderType getRenderType(T animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityCutoutNoCull(model.getTextureResource(animatable));
    }

    @Override
    public void renderRecursively(
            PoseStack poseStack,
            T animatable,
            GeoBone bone,
            RenderType renderType,
            MultiBufferSource bufferSource,
            VertexConsumer buffer,
            boolean isReRender,
            float partialTick,
            int packedLight,
            int packedOverlay,
            int colour
    ) {
        int packedLight1 = (iBoneLight != null) ? iBoneLight.getLightForBone(bone, packedLight) : packedLight;
        int red = FastColor.ARGB32.red(colour);
        int blue = FastColor.ARGB32.blue(colour);
        int green = FastColor.ARGB32.green(colour);
        int alpha = FastColor.ARGB32.alpha(colour);
        Vector4f color = new Vector4f(red, green, blue, alpha);
        Vector4f newColor = (iBoneLight != null) ? iBoneLight.getColorForBone(bone, color) : color;
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
                FastColor.ARGB32.color((int) newColor.w, (int) newColor.x, (int) newColor.y, (int) newColor.z));
    }
}

