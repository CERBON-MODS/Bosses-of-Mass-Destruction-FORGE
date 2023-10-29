package com.cerbon.bosses_of_mass_destruction.util;

import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

public class AnimationUtils {
    public static <T extends GeoAnimatable> AnimationController.AnimationStateHandler<T> createIdlePredicate(String animationName) {
        return animationState -> {
            animationState.getController().setAnimation(
                    RawAnimation.begin().thenLoop(animationName)
            );
            return PlayState.CONTINUE;
        };
    }
}
