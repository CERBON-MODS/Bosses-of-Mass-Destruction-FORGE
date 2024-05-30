package com.cerbon.bosses_of_mass_destruction.mixin;

import com.cerbon.bosses_of_mass_destruction.client.render.NodeBossBarRenderer;
import com.cerbon.bosses_of_mass_destruction.entity.BMDEntities;
import com.cerbon.bosses_of_mass_destruction.entity.custom.lich.LichUtils;
import com.cerbon.bosses_of_mass_destruction.entity.custom.obsidilith.ObsidilithUtils;
import com.cerbon.bosses_of_mass_destruction.entity.custom.void_blossom.VoidBlossomEntity;
import com.cerbon.bosses_of_mass_destruction.util.BMDConstants;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.overlay.BossOverlayGui;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.BossInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BossOverlayGui.class)
public class BossHealthOverlayMixin {

    @Inject(method = "drawBar", at = @At("HEAD"), cancellable = true)
    private void drawCustomBossBar(MatrixStack poseStack, int x, int y, BossInfo bossEvent, CallbackInfo ci) {

        NodeBossBarRenderer lichBossBarRenderer = new NodeBossBarRenderer(
                BMDEntities.LICH.get().getDescriptionId(),
                LichUtils.hpPercentRageModes,
                new ResourceLocation(BMDConstants.MOD_ID, "textures/gui/lich_boss_bar_dividers.png"),
                LichUtils.textureSize
        );

        NodeBossBarRenderer voidBlossomBarRenderer = new NodeBossBarRenderer(
                BMDEntities.VOID_BLOSSOM.get().getDescriptionId(),
                VoidBlossomEntity.hpMilestones,
                new ResourceLocation(BMDConstants.MOD_ID, "textures/gui/void_blossom_boss_bar_dividers.png"),
                LichUtils.textureSize
        );

        lichBossBarRenderer.renderBossBar(poseStack, x, y, bossEvent, ci);
        voidBlossomBarRenderer.renderBossBar(poseStack, x, y, bossEvent, ci);
        ObsidilithUtils.obsidilithBossBarRenderer.renderBossBar(poseStack, x, y, bossEvent, ci);
    }
}
