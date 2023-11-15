package com.cerbon.bosses_of_mass_destruction.entity.custom.gauntlet;

import com.cerbon.bosses_of_mass_destruction.client.render.ITextureProvider;
import com.cerbon.bosses_of_mass_destruction.util.BMDConstants;
import net.minecraft.resources.ResourceLocation;

public class GauntletTextureProvider implements ITextureProvider<GauntletEntity> {

    @Override
    public ResourceLocation getTexture(GauntletEntity entity) {
        if (entity.hurtTime > 0)
            return new ResourceLocation(BMDConstants.MOD_ID, "textures/entity/gauntlet_hurt.png");
        else {
            return new ResourceLocation(BMDConstants.MOD_ID, "textures/entity/gauntlet.png");
        }
    }
}
