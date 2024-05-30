package com.cerbon.bosses_of_mass_destruction.entity;

import com.cerbon.bosses_of_mass_destruction.client.render.ITextureProvider;
import com.cerbon.bosses_of_mass_destruction.entity.util.animation.ICodeAnimations;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.IAnimationTickable;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.model.AnimatedTickingGeoModel;

import java.util.function.Function;

public class GeoModel<T extends IAnimatable & IAnimationTickable> extends AnimatedTickingGeoModel<T> {
    private final Function<T, ResourceLocation> modelLocation;
    private final ITextureProvider<T> textureProvider;
    private final ResourceLocation animationLocation;
    private final ICodeAnimations<T> codeAnimations;

    public GeoModel(Function<T, ResourceLocation> modelLocation, ITextureProvider<T> textureProvider, ResourceLocation animationLocation, ICodeAnimations<T> codeAnimations){
        this.modelLocation = modelLocation;
        this.textureProvider = textureProvider;
        this.animationLocation = animationLocation;
        this.codeAnimations = codeAnimations;
    }

    @Override
    public ResourceLocation getModelLocation(T t) {
        return modelLocation.apply(t);
    }

    @Override
    public ResourceLocation getTextureLocation(T t) {
        return textureProvider.getTexture(t);
    }

    @Override
    public ResourceLocation getAnimationFileLocation(T t) {
        return animationLocation;
    }

    @Override
    public void setLivingAnimations(T animatable, Integer instanceId, AnimationEvent customPredicate) {
        super.setLivingAnimations(animatable, instanceId, customPredicate);
        if (animatable != null && customPredicate != null) codeAnimations.animate(animatable, customPredicate, this);
    }


}

