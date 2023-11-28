package com.cerbon.bosses_of_mass_destruction.entity.util.animation;

import com.cerbon.bosses_of_mass_destruction.entity.GeoModel;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.IAnimationTickable;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;

public interface ICodeAnimations<T extends IAnimatable & IAnimationTickable> {
    void animate(T animatable, AnimationEvent<?> data, GeoModel<T> geoModel);
}

