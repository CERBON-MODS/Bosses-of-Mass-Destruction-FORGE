package com.cerbon.bosses_of_mass_destruction.entity.custom.lich;

import com.cerbon.bosses_of_mass_destruction.client.render.IRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.level.Level;


public class EternalNightRenderer implements IRenderer<LichEntity> {

    @Override
    public void render(LichEntity entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int light) {
        Level level = entity.level();
        boolean isAddedToWorld = level.getEntity(entity.getId()) != null;

        if (entity.shouldSetToNighttime && level instanceof ClientLevel && isAddedToWorld) {
            ((ClientLevel) level).setDayTime(LichUtils.timeToNighttime(level.dayTime()));
        }
    }
}
