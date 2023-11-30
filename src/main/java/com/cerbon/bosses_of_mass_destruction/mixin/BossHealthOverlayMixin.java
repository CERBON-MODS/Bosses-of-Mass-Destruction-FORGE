package com.cerbon.bosses_of_mass_destruction.mixin;

import com.cerbon.bosses_of_mass_destruction.client.render.NodeBossBarRenderer;
import com.cerbon.bosses_of_mass_destruction.entity.BMDEntities;
import com.cerbon.bosses_of_mass_destruction.entity.custom.lich.LichUtils;
import com.cerbon.bosses_of_mass_destruction.entity.custom.obsidilith.ObsidilithUtils;
import com.cerbon.bosses_of_mass_destruction.entity.custom.void_blossom.VoidBlossomEntity;
import com.cerbon.bosses_of_mass_destruction.util.BMDConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.BossEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BossHealthOverlay.class)
public class BossHealthOverlayMixin {

    @Inject(method = "drawBar(Lcom/mojang/blaze3d/vertex/PoseStack;IILnet/minecraft/world/BossEvent;)V", at = @At("HEAD"), cancellable = true)
    private void drawCustomBossBar(PoseStack poseStack, int x, int y, BossEvent bossEvent, CallbackInfo ci) {

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
