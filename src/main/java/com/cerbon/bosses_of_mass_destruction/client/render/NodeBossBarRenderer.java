package com.cerbon.bosses_of_mass_destruction.client.render;

import com.cerbon.bosses_of_mass_destruction.util.maelstrom.static_utilities.MathUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.BossEvent;
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

    public void renderBossBar(ResourceLocation texture, GuiGraphics guiGraphics, int x, int y, BossEvent bossEvent, CallbackInfo callbackInfo){
        Component name = bossEvent.getName();
        ComponentContents barContent = name.getContents();

        if (barContent instanceof TranslatableContents translatableContents && translatableContents.getKey().equals(entityTypeKey)){
            float colorLocation = bossEvent.getColor().ordinal() * 5 * 2f;
            guiGraphics.blit(
                    texture, x, y, 0f, colorLocation, 182, 5,
                    textureSize,
                    textureSize
            );

            int i = (int) (bossEvent.getProgress() * 183.0f);
            if (i > 0){
                float progressLocation = bossEvent.getColor().ordinal() * 5 * 2 * 5f;
                guiGraphics.blit(
                        texture, x, y, 0f, progressLocation, i, 5,
                        textureSize,
                        textureSize
                );
            }

            renderBossNodes(bossEvent, guiGraphics, x, y);

            callbackInfo.cancel();
        }
    }

    private void renderBossNodes(
            BossEvent bossEvent,
            GuiGraphics guiGraphics,
            int x,
            int y
    ) {
        int steppedPercentage = (int) (192 * MathUtils.roundedStep(bossEvent.getProgress(), hpPercentages, true)) + 7;
        guiGraphics.blit(
                noteTexture, x - 3, y - 1, 0f, 0f, steppedPercentage, 7,
                textureSize,
                textureSize
        );

        int steppedPercentageReverse = 192 - steppedPercentage;
        guiGraphics.blit(
                noteTexture,
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

