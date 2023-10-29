package com.cerbon.bosses_of_mass_destruction.entity.util;

import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public interface ICodeAnimations<T extends GeoAnimatable> {
    void animate(T animatable, AnimationState<?> data, GeoModel<T> geoModel);
}

