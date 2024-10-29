package com.cerbon.bosses_of_mass_destruction.entity.custom.void_blossom;

import com.cerbon.bosses_of_mass_destruction.entity.GeoModel;
import com.cerbon.bosses_of_mass_destruction.entity.util.animation.ICodeAnimations;
import net.minecraft.util.Mth;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.BakedGeoModel;

public class VoidBlossomCodeAnimations implements ICodeAnimations<VoidBlossomEntity> {
    @Override
    public void animate(VoidBlossomEntity animatable, AnimationState<?> data, GeoModel<VoidBlossomEntity> geoModel) {
        float bodyYaw = Mth.rotLerp(data.getPartialTick(), animatable.yBodyRotO, animatable.yBodyRot);

        BakedGeoModel model = geoModel.getBakedModel(geoModel.getModelResource(animatable));
        model.getBone("Leaves").ifPresent(geoBone -> geoBone.setRotY((float) Math.toRadians(bodyYaw)));
        model.getBone("Thorns").ifPresent(geoBone -> geoBone.setRotY((float) Math.toRadians(bodyYaw)));
        model.getBone("Roots").ifPresent(geoBone -> geoBone.setRotY((float) Math.toRadians(bodyYaw)));
    }
}
