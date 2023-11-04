package com.cerbon.bosses_of_mass_destruction.mixin;

import com.cerbon.bosses_of_mass_destruction.client.render.NodeBossBarRenderer;
import com.cerbon.bosses_of_mass_destruction.entity.BMDEntities;
import com.cerbon.bosses_of_mass_destruction.entity.custom.lich.LichUtils;
import com.cerbon.bosses_of_mass_destruction.util.BMDConstants;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.BossEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BossHealthOverlay.class)
public class BossHealthOverlayMixin {
    @Shadow @Final private static ResourceLocation GUI_BARS_LOCATION;

    @Unique private static final NodeBossBarRenderer bmd_lichBossBarRenderer = new NodeBossBarRenderer(
            BMDEntities.LICH.get().getDescriptionId(),
            LichUtils.hpPercentRageModes,
            new ResourceLocation(BMDConstants.MOD_ID, "textures/gui/lich_boss_bar_dividers.png"),
            LichUtils.textureSize
    );

    @Inject(method = "drawBar(Lnet/minecraft/client/gui/GuiGraphics;IILnet/minecraft/world/BossEvent;)V", at = @At("HEAD"), cancellable = true)
    private void drawCustomBossBar(GuiGraphics guiGraphics, int x, int y, BossEvent bossEvent, CallbackInfo ci){
        bmd_lichBossBarRenderer.renderBossBar(GUI_BARS_LOCATION, guiGraphics, x, y, bossEvent, ci);
    }
}
