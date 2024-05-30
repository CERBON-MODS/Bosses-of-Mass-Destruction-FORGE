package com.cerbon.bosses_of_mass_destruction.entity.custom.gauntlet;

import com.cerbon.bosses_of_mass_destruction.entity.GeoModel;
import com.cerbon.bosses_of_mass_destruction.entity.util.animation.ICodeAnimations;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;

public class GauntletCodeAnimations implements ICodeAnimations<GauntletEntity> {

    @Override
    public void animate(GauntletEntity animatable, AnimationEvent<?> data, GeoModel<GauntletEntity> geoModel) {
        float headPitch = MathHelper.lerp(data.getPartialTick(), animatable.xRotO, animatable.xRot);
        software.bernie.geckolib3.geo.render.built.GeoModel model = geoModel.getModel(geoModel.getModelLocation(animatable));

        model.getBone("codeRoot").ifPresent(bone -> bone.setRotationX((float) -Math.toRadians(headPitch)));
    }
}
