package com.cerbon.bosses_of_mass_destruction.entity.custom.gauntlet;

import com.cerbon.bosses_of_mass_destruction.entity.util.BaseEntity;
import com.cerbon.bosses_of_mass_destruction.entity.util.IEntityEventHandler;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;

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

    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        data.add(new AnimationController<>(entity, transition, attack));
    }

    @Override
    public void handleEntityEvent(byte status) {
        if (animationStatusFlags.containsKey(status)){
            doIdleAnimation = false;
            nextAnimation = animationStatusFlags.get(status);
        }
        if (status == stopAttackByte) doIdleAnimation = true;
    }

    private final AnimationController.AnimationStateHandler<BaseEntity> attack = animationState -> {
        Animation animationData = nextAnimation;
        nextAnimation = null;

        if (animationData != null){
            animationState.getController().forceAnimationReset();
            animationState.setAnimation(RawAnimation.begin().thenPlay(animationData.animationName).thenLoop(animationData.idleAnimationName));
        }

        if (doIdleAnimation)
            animationState.setAnimation(RawAnimation.begin().thenLoop("idle"));

        return PlayState.CONTINUE;
    };

    public record Animation(String animationName, String idleAnimationName) {}
}

