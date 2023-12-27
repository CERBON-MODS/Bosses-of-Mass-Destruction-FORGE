package com.cerbon.bosses_of_mass_destruction.entity.custom.lich;

import com.cerbon.bosses_of_mass_destruction.entity.GeoModel;
import com.cerbon.bosses_of_mass_destruction.entity.util.animation.ICodeAnimations;
import com.cerbon.cerbons_api.api.static_utilities.MathUtils;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.core.animation.AnimationState;

public class LichCodeAnimations implements ICodeAnimations<LichEntity> {

    @Override
    public void animate(LichEntity animatable, AnimationState<?> data, GeoModel<LichEntity> geoModel) {
        float bodyYaw = Mth.rotLerp(data.getPartialTick(), animatable.yBodyRotO, animatable.yBodyRot);
        float headYaw = Mth.rotLerp(data.getPartialTick(), animatable.yHeadRotO, animatable.yHeadRot);

        float headPitch = Mth.lerp(data.getPartialTick(), animatable.xRotO, animatable.getXRot());

        Vec3 velocity = MathUtils.lerpVec(data.getPartialTick(), animatable.velocityHistory.get(1), animatable.velocityHistory.get(0));

        int neutralPoseDegree = 30;
        int maxDegreeVariation = 15;
        double bodyPitch = (MathUtils.directionToPitch(velocity) * (maxDegreeVariation / 90.0)) + neutralPoseDegree;

        float yaw = headYaw - bodyYaw;
        double adjustedHeadPitch = headPitch - bodyPitch;

        BakedGeoModel model = geoModel.getBakedModel(geoModel.getModelResource(animatable));
        model.getBone("code_root").ifPresent(bone -> bone.setRotX((float) -Math.toRadians(bodyPitch)));
        model.getBone("headBase").ifPresent(bone -> bone.setRotX((float) -Math.toRadians(adjustedHeadPitch)));
        model.getBone("headBase").ifPresent(bone -> bone.setRotY((float) Math.toRadians(yaw)));
    }
}
