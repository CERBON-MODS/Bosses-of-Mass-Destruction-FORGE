package com.cerbon.bosses_of_mass_destruction.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import software.bernie.geckolib3.geo.render.built.GeoModel;

public interface IRendererWithModel {
    void render(
            GeoModel model,
            float partialTicks,
            MatrixStack poseStack,
            IRenderTypeBuffer buffer,
            int packedLightIn,
            int packedOverlayIn,
            float red,
            float green,
            float blue,
            float alpha
    );
}

