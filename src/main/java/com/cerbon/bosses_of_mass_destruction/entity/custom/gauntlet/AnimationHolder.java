package com.cerbon.bosses_of_mass_destruction.entity.custom.gauntlet;

import com.cerbon.bosses_of_mass_destruction.entity.util.BaseEntity;
import com.cerbon.bosses_of_mass_destruction.entity.util.IEntityEventHandler;
import com.cerbon.bosses_of_mass_destruction.entity.util.animation.AnimationPredicate;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.manager.AnimationData;

import java.util.Map;

public class AnimationHolder implements IEntityEventHandler {
    private final BaseEntity entity;
    private final Map<Byte, Animation> animationStatusFlags;
    private final Byte stopAttackByte;
    private final int transition;
    private Animation nextAnimation;
    private boolean doIdleAnimation = true;

    public AnimationHolder(BaseEntity entity, Map<Byte, Animation> animationStatusFlags, Byte stopAttackByte, int transition) {
        this.entity = entity;
        this.animationStatusFlags = animationStatusFlags;
        this.stopAttackByte = stopAttackByte;
        this.transition = transition;
    }

    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(entity, "attack", transition, attack));
    }

    @Override
    public void handleEntityEvent(byte status) {
        if (animationStatusFlags.containsKey(status)){
            doIdleAnimation = false;
            nextAnimation = animationStatusFlags.get(status);
        }
        if (status == stopAttackByte) doIdleAnimation = true;
    }

    private final AnimationPredicate<BaseEntity> attack = new AnimationPredicate<>(animationState -> {
        Animation animationData = nextAnimation;
        nextAnimation = null;

        if (animationData != null){
            animationState.getController().markNeedsReload();
            animationState.getController().setAnimation(
                    new AnimationBuilder()
                            .addAnimation(animationData.animationName, false)
                            .addAnimation(animationData.idleAnimationName, true)
            );
        }

        if (doIdleAnimation)
            animationState.getController().setAnimation(
                    new AnimationBuilder().addAnimation("idle", true)
            );

        return PlayState.CONTINUE;
    });

    public static class Animation {
        private final String animationName;
        private final String idleAnimationName;

        public Animation(String animationName, String idleAnimationName) {
            this.animationName = animationName;
            this.idleAnimationName = idleAnimationName;
        }
    }
}

