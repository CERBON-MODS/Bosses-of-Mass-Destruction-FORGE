package com.cerbon.bosses_of_mass_destruction.entity.custom.gauntlet;

import com.cerbon.bosses_of_mass_destruction.client.render.ITextureProvider;
import com.cerbon.bosses_of_mass_destruction.util.BMDConstants;
import net.minecraft.resources.ResourceLocation;

public class GauntletTextureProvider implements ITextureProvider<GauntletEntity> {

    @Override
    public ResourceLocation getTexture(GauntletEntity entity) {
        return entity.hurtTime > 0
                ? ResourceLocation.fromNamespaceAndPath(BMDConstants.MOD_ID, "textures/entity/gauntlet_hurt.png")
                : ResourceLocation.fromNamespaceAndPath(BMDConstants.MOD_ID, "textures/entity/gauntlet.png");
    }
}
