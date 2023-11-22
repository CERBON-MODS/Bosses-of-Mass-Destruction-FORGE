package com.cerbon.bosses_of_mass_destruction.entity.custom.obsidilith;

import com.cerbon.bosses_of_mass_destruction.client.render.IBoneLight;
import com.cerbon.bosses_of_mass_destruction.client.render.IRenderer;
import com.cerbon.bosses_of_mass_destruction.util.BMDColors;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector4f;
import software.bernie.geckolib.cache.object.GeoBone;


public class ObsidilithBoneLight implements IBoneLight, IRenderer<ObsidilithEntity> {
    private ObsidilithEntity entity = null;
    private final Vector4f defaultBoneColor = new Vector4f(0.5f, 0.5f, 0.5f, 1f);

    @Override
    public int getLightForBone(GeoBone bone, int packedLight) {
        if (bone.getName().equals("middle_runes") && entity != null && entity.currentAttack > 0)
            return IBoneLight.fullbright;
        else
            return packedLight;

    }

    @Override
    public Vector4f getColorForBone(GeoBone bone, Vector4f rgbaColor) {
        if (bone.getName().equals("middle_runes")) {
            return switch (entity.currentAttack) {
                case ObsidilithUtils.burstAttackStatus -> colorToVec4(BMDColors.ORANGE);
                case ObsidilithUtils.waveAttackStatus -> colorToVec4(BMDColors.RED);
                case ObsidilithUtils.spikeAttackStatus -> colorToVec4(BMDColors.COMET_BLUE);
                case ObsidilithUtils.anvilAttackStatus -> colorToVec4(BMDColors.ENDER_PURPLE);
                case ObsidilithUtils.pillarDefenseStatus -> colorToVec4(BMDColors.WHITE);
                default -> defaultBoneColor;
            };
        }

        return IBoneLight.super.getColorForBone(bone, rgbaColor);
    }

    private Vector4f colorToVec4(Vec3 color) {
        return new Vector4f(
                (float) color.x,
                (float) color.y,
                (float) color.z,
                1.0f
        );
    }

    @Override
    public void render(
            ObsidilithEntity entity,
            float yaw,
            float partialTicks,
            PoseStack poseStack,
            MultiBufferSource multiBufferSource,
            int light
    ) {
        this.entity = entity;
    }
}

