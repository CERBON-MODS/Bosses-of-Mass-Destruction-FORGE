package com.cerbon.bosses_of_mass_destruction.entity.custom.gauntlet;

import com.cerbon.bosses_of_mass_destruction.client.render.ITextureProvider;
import com.cerbon.bosses_of_mass_destruction.util.BMDConstants;
import net.minecraft.util.ResourceLocation;

public class GauntletTextureProvider implements ITextureProvider<GauntletEntity> {

    @Override
    public ResourceLocation getTexture(GauntletEntity entity) {
        return entity.hurtTime > 0
                ? new ResourceLocation(BMDConstants.MOD_ID, "textures/entity/gauntlet_hurt.png")
                : new ResourceLocation(BMDConstants.MOD_ID, "textures/entity/gauntlet.png");
    }
}
