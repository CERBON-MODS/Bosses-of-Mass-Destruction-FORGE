package com.cerbon.bosses_of_mass_destruction.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import software.bernie.geckolib3.geo.render.built.GeoModel;

public interface IRendererWithModel {
    void render(
            GeoModel model,
            float partialTicks,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLightIn,
            int packedOverlayIn,
            float red,
            float green,
            float blue,
            float alpha
    );
}

