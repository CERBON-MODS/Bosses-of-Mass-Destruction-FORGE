package com.cerbon.bosses_of_mass_destruction.util;

import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;

public class AnimationUtils {
    public static <T extends IAnimatable> AnimationController.IAnimationPredicate<T> createIdlePredicate(String animationName) {
        return animationState -> {
            animationState.getController().setAnimation(
                    new AnimationBuilder().addAnimation(animationName, true)
            );
            return PlayState.CONTINUE;
        };
    }
}
