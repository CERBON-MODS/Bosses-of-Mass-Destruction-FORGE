package com.cerbon.bosses_of_mass_destruction.projectile.comet;

import com.cerbon.bosses_of_mass_destruction.entity.GeoModel;
import com.cerbon.bosses_of_mass_destruction.entity.util.animation.ICodeAnimations;
import net.minecraft.util.Mth;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;

public class CometCodeAnimations implements ICodeAnimations<CometProjectile> {
    @Override
    public void animate(
            CometProjectile animatable,
            AnimationEvent<?> data,
            GeoModel<CometProjectile> geoModel
    ) {
        float pitch = Mth.rotLerp(data.getPartialTick(), animatable.getXRot() - 5, animatable.getXRot());
        software.bernie.geckolib3.geo.render.built.GeoModel model = geoModel.getModel(geoModel.getModelLocation(animatable));
        model.getBone("root1").ifPresent(geoBone -> geoBone.setRotationX((float) Math.toRadians(pitch)));
    }
}
