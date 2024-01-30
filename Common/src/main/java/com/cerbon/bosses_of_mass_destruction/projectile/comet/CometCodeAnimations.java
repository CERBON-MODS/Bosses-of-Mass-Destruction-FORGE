package com.cerbon.bosses_of_mass_destruction.projectile.comet;

import com.cerbon.bosses_of_mass_destruction.entity.GeoModel;
import com.cerbon.bosses_of_mass_destruction.entity.util.animation.ICodeAnimations;
import net.minecraft.util.Mth;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.core.animation.AnimationState;

public class CometCodeAnimations implements ICodeAnimations<CometProjectile> {
    @Override
    public void animate(
            CometProjectile animatable,
            AnimationState<?> data,
            GeoModel<CometProjectile> geoModel
    ) {
        float pitch = Mth.rotLerp(data.getPartialTick(), animatable.getXRot() - 5, animatable.getXRot());
        BakedGeoModel model = geoModel.getBakedModel(geoModel.getModelResource(animatable));
        model.getBone("root1").ifPresent(geoBone -> geoBone.setRotX((float) Math.toRadians(pitch)));
    }
}
