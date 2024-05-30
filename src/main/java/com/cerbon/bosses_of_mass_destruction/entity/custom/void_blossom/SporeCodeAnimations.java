package com.cerbon.bosses_of_mass_destruction.entity.custom.void_blossom;

import com.cerbon.bosses_of_mass_destruction.entity.GeoModel;
import com.cerbon.bosses_of_mass_destruction.entity.util.animation.ICodeAnimations;
import com.cerbon.bosses_of_mass_destruction.projectile.SporeBallProjectile;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;

public class SporeCodeAnimations implements ICodeAnimations<SporeBallProjectile> {

    public SporeCodeAnimations(){}

    @Override
    public void animate(SporeBallProjectile animatable, AnimationEvent<?> data, GeoModel<SporeBallProjectile> geoModel) {
        float pitch = animatable.impacted ? animatable.getXRot() : MathHelper.rotLerp(data.getPartialTick(), animatable.getXRot() - 5, animatable.getXRot());

        software.bernie.geckolib3.geo.render.built.GeoModel model = geoModel.getModel(geoModel.getModelLocation(animatable));
        model.getBone("root1").ifPresent(it -> it.setRotationX((float) Math.toRadians(pitch)));
    }
}
