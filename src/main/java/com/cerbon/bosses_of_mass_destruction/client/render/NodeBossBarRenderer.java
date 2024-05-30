package com.cerbon.bosses_of_mass_destruction.client.render;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.MathUtils;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.BossInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

public class NodeBossBarRenderer {
    private final String entityTypeKey;
    private final List<Float> hpPercentages;
    private final ResourceLocation noteTexture;
    private final int textureSize;

    public NodeBossBarRenderer(String entityTypeKey, List<Float> hpPercentages, ResourceLocation noteTexture, int textureSize) {
        this.entityTypeKey = entityTypeKey;
        this.hpPercentages = hpPercentages;
        this.noteTexture = noteTexture;
        this.textureSize = textureSize;
    }

    public void renderBossBar(MatrixStack poseStack, int x, int y, BossInfo bossEvent, CallbackInfo callbackInfo){
        ITextComponent name = bossEvent.getName();

        if (name instanceof TranslationTextComponent) {
            if (((TranslationTextComponent) name).getKey().equals(entityTypeKey)) {
                float colorLocation = bossEvent.getColor().ordinal() * 5 * 2f;
                AbstractGui.blit(
                        poseStack, x, y, 0f, colorLocation, 182, 5,
                        textureSize,
                        textureSize
                );

                int i = (int) (bossEvent.getPercent() * 183.0f);
                if (i > 0){
                    float progressLocation = bossEvent.getColor().ordinal() * 5 * 2 + 5f;
                    AbstractGui.blit(
                            poseStack, x, y, 0f, progressLocation, i, 5,
                            textureSize,
                            textureSize
                    );
                }
            }

            renderBossNodes(bossEvent, poseStack, x, y);

            callbackInfo.cancel();
        }
    }

    private void renderBossNodes(
            BossInfo bossEvent,
            MatrixStack poseStack,
            int x,
            int y
    ) {
        //RenderSystem.setShaderTexture(0, noteTexture);
        int steppedPercentage = (int) (192 * MathUtils.roundedStep(bossEvent.getPercent(), hpPercentages, true)) + 7;
        AbstractGui.blit(
                poseStack, x - 3, y - 1, 0f, 0f, steppedPercentage, 7,
                textureSize,
                textureSize
        );

        int steppedPercentageReverse = 192 - steppedPercentage;
        AbstractGui.blit(
                poseStack,
                x - 3 + steppedPercentage,
                y - 1,
                (float) steppedPercentage,
                7f,
                steppedPercentageReverse,
                7,
                textureSize,
                textureSize
        );
    }
}

