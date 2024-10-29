package com.cerbon.bosses_of_mass_destruction.entity.custom.gauntlet;

import com.cerbon.bosses_of_mass_destruction.entity.GeoModel;
import com.cerbon.bosses_of_mass_destruction.entity.util.animation.ICodeAnimations;
import net.minecraft.util.Mth;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.BakedGeoModel;

public class GauntletCodeAnimations implements ICodeAnimations<GauntletEntity> {

    @Override
    public void animate(GauntletEntity animatable, AnimationState<?> data, GeoModel<GauntletEntity> geoModel) {
        float headPitch = Mth.lerp(data.getPartialTick(), animatable.xRotO, animatable.getXRot());
        BakedGeoModel model = geoModel.getBakedModel(geoModel.getModelResource(animatable));

        model.getBone("codeRoot").ifPresent(bone -> bone.setRotX((float) -Math.toRadians(headPitch)));
    }
}
