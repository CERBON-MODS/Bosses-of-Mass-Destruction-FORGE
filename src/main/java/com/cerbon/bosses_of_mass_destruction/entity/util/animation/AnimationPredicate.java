package com.cerbon.bosses_of_mass_destruction.entity.util.animation;

import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;

import java.util.function.Function;

public class AnimationPredicate<T extends IAnimatable> implements AnimationController.IAnimationPredicate<T> {

    private final Function<AnimationEvent<?>, PlayState> predicate;

    public AnimationPredicate(Function<AnimationEvent<?>, PlayState> predicate) {
        this.predicate = predicate;
    }

    @Override
    public PlayState test(AnimationEvent<T> animationEvent) {
        return predicate.apply(animationEvent);
    }
}
