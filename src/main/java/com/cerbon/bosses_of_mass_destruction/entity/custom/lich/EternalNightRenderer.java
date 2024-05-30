package com.cerbon.bosses_of_mass_destruction.entity.custom.lich;

import com.cerbon.bosses_of_mass_destruction.client.render.IRenderer;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.world.World;


public class EternalNightRenderer implements IRenderer<LichEntity> {

    @Override
    public void render(LichEntity entity, float yaw, float partialTicks, MatrixStack poseStack, IRenderTypeBuffer buffer, int light) {
        World level = entity.level;
        boolean isAddedToWorld = level.getEntity(entity.getId()) != null;

        if (entity.shouldSetToNighttime && level instanceof ClientWorld && isAddedToWorld) {
            ((ClientWorld) level).setDayTime(LichUtils.timeToNighttime(level.dayTime()));
        }
    }
}
