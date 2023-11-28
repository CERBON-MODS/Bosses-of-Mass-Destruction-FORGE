package com.cerbon.bosses_of_mass_destruction.util;

import com.cerbon.bosses_of_mass_destruction.entity.util.animation.AnimationPredicate;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;

public class AnimationUtils {
    public static <T extends IAnimatable> AnimationPredicate<T> createIdlePredicate(String animationName) {
        return new AnimationPredicate<>(animationState -> {
            animationState.getController().setAnimation(
                    new AnimationBuilder().addAnimation(animationName, true)
            );
            return PlayState.CONTINUE;
        });
    }
}
