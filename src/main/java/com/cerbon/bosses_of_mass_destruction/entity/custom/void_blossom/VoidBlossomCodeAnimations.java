package com.cerbon.bosses_of_mass_destruction.entity.custom.void_blossom;

import com.cerbon.bosses_of_mass_destruction.entity.GeoModel;
import com.cerbon.bosses_of_mass_destruction.entity.util.animation.ICodeAnimations;
import net.minecraft.util.Mth;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;

public class VoidBlossomCodeAnimations implements ICodeAnimations<VoidBlossomEntity> {
    @Override
    public void animate(VoidBlossomEntity animatable, AnimationEvent<?> data, GeoModel<VoidBlossomEntity> geoModel) {
        float bodyYaw = Mth.rotLerp(data.getPartialTick(), animatable.yBodyRotO, animatable.yBodyRot);

        var model = geoModel.getModel(geoModel.getModelResource(animatable));
        model.getBone("Leaves").ifPresent(geoBone -> geoBone.setRotationY((float) Math.toRadians(bodyYaw)));
        model.getBone("Thorns").ifPresent(geoBone -> geoBone.setRotationY((float) Math.toRadians(bodyYaw)));
        model.getBone("Roots").ifPresent(geoBone -> geoBone.setRotationY((float) Math.toRadians(bodyYaw)));
    }
}
