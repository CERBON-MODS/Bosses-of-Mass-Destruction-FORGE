package com.cerbon.bosses_of_mass_destruction.entity.custom.gauntlet;

import com.cerbon.bosses_of_mass_destruction.entity.GeoModel;
import com.cerbon.bosses_of_mass_destruction.entity.util.animation.ICodeAnimations;
import net.minecraft.util.Mth;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;

public class GauntletCodeAnimations implements ICodeAnimations<GauntletEntity> {

    @Override
    public void animate(GauntletEntity animatable, AnimationEvent<?> data, GeoModel<GauntletEntity> geoModel) {
        float headPitch = Mth.lerp(data.getPartialTick(), animatable.xRotO, animatable.getXRot());
        var model = geoModel.getModel(geoModel.getModelLocation(animatable));

        model.getBone("codeRoot").ifPresent(bone -> bone.setRotationX((float) -Math.toRadians(headPitch)));
    }
}
