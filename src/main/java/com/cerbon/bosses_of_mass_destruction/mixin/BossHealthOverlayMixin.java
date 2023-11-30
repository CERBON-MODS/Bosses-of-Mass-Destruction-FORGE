package com.cerbon.bosses_of_mass_destruction.mixin;

import com.cerbon.bosses_of_mass_destruction.client.render.NodeBossBarRenderer;
import com.cerbon.bosses_of_mass_destruction.entity.BMDEntities;
import com.cerbon.bosses_of_mass_destruction.entity.custom.lich.LichUtils;
import com.cerbon.bosses_of_mass_destruction.entity.custom.obsidilith.ObsidilithUtils;
import com.cerbon.bosses_of_mass_destruction.entity.custom.void_blossom.VoidBlossomEntity;
import com.cerbon.bosses_of_mass_destruction.util.BMDConstants;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.BossEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BossHealthOverlay.class)
public class BossHealthOverlayMixin {
    @Shadow @Final private static ResourceLocation GUI_BARS_LOCATION;

    @Inject(method = "drawBar(Lnet/minecraft/client/gui/GuiGraphics;IILnet/minecraft/world/BossEvent;)V", at = @At("HEAD"), cancellable = true)
    private void drawCustomBossBar(GuiGraphics guiGraphics, int x, int y, BossEvent bossEvent, CallbackInfo ci) {

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

        lichBossBarRenderer.renderBossBar(GUI_BARS_LOCATION, guiGraphics, x, y, bossEvent, ci);
        voidBlossomBarRenderer.renderBossBar(GUI_BARS_LOCATION, guiGraphics, x, y, bossEvent, ci);
        ObsidilithUtils.obsidilithBossBarRenderer.renderBossBar(GUI_BARS_LOCATION, guiGraphics, x, y, bossEvent, ci);
    }
}
