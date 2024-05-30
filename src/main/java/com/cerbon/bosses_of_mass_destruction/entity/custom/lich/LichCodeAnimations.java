package com.cerbon.bosses_of_mass_destruction.entity.custom.lich;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.MathUtils;
import com.cerbon.bosses_of_mass_destruction.entity.GeoModel;
import com.cerbon.bosses_of_mass_destruction.entity.util.animation.ICodeAnimations;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;

public class LichCodeAnimations implements ICodeAnimations<LichEntity> {

    @Override
    public void animate(LichEntity animatable, AnimationEvent<?> data, GeoModel<LichEntity> geoModel) {
        float bodyYaw = MathHelper.rotLerp(data.getPartialTick(), animatable.yBodyRotO, animatable.yBodyRot);
        float headYaw = MathHelper.rotLerp(data.getPartialTick(), animatable.yHeadRotO, animatable.yHeadRot);

        float headPitch = MathHelper.lerp(data.getPartialTick(), animatable.xRotO, animatable.xRot);

        Vector3d velocity = MathUtils.lerpVec(data.getPartialTick(), animatable.velocityHistory.get(1), animatable.velocityHistory.get(0));

        int neutralPoseDegree = 30;
        int maxDegreeVariation = 15;
        double bodyPitch = (MathUtils.directionToPitch(velocity) * (maxDegreeVariation / 90.0)) + neutralPoseDegree;

        float yaw = headYaw - bodyYaw;
        double adjustedHeadPitch = headPitch - bodyPitch;

        software.bernie.geckolib3.geo.render.built.GeoModel model = geoModel.getModel(geoModel.getModelLocation(animatable));
        model.getBone("code_root").ifPresent(bone -> bone.setRotationX((float) -Math.toRadians(bodyPitch)));
        model.getBone("headBase").ifPresent(bone -> bone.setRotationX((float) -Math.toRadians(adjustedHeadPitch)));
        model.getBone("headBase").ifPresent(bone -> bone.setRotationX((float) Math.toRadians(yaw)));
    }
}
