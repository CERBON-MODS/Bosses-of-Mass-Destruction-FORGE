package com.cerbon.bosses_of_mass_destruction.entity.custom.lich;

import com.cerbon.bosses_of_mass_destruction.client.render.IBoneLight;
import software.bernie.geckolib3.geo.render.built.GeoBone;

import java.util.Arrays;
import java.util.List;

public class LichBoneLight implements IBoneLight {

    @Override
    public int getLightForBone(GeoBone bone, int packedLight) {
        List<String> boneNames = Arrays.asList("crown_crystals", "crystal", "leftEye", "rightEye");
        return boneNames.contains(bone.getName()) ? IBoneLight.fullbright : packedLight;
    }
}


