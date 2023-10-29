package com.cerbon.bosses_of_mass_destruction.entity;

import com.cerbon.bosses_of_mass_destruction.client.render.ITextureProvider;
import com.cerbon.bosses_of_mass_destruction.entity.util.ICodeAnimations;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.AnimationState;

import java.util.function.Function;

public class GeoModel<T extends GeoAnimatable> extends software.bernie.geckolib.model.GeoModel<T> {
    private final Function<T, ResourceLocation> modelLocation;
    private final ITextureProvider<T> textureProvider;
    private final ResourceLocation animationLocation;
    private final ICodeAnimations<T> codeAnimations;
    private final Function<ResourceLocation, RenderType> renderType;

    public GeoModel(Function<T, ResourceLocation> modelLocation, ITextureProvider<T> textureProvider, ResourceLocation animationLocation, ICodeAnimations<T> codeAnimations, Function<ResourceLocation, RenderType> renderType){
        this.modelLocation = modelLocation;
        this.textureProvider = textureProvider;
        this.animationLocation = animationLocation;
        this.codeAnimations = codeAnimations;
        this.renderType = renderType;
    }

    @Override
    public ResourceLocation getModelResource(T t) {
        return modelLocation.apply(t);
    }

    @Override
    public ResourceLocation getTextureResource(T t) {
        return textureProvider.getTexture(t);
    }

    @Override
    public ResourceLocation getAnimationResource(T t) {
        return animationLocation;
    }

    @Override
    public RenderType getRenderType(T animatable, ResourceLocation texture) {
        return renderType.apply(texture);
    }

    @Override
    public void setCustomAnimations(T animatable, long instanceId, AnimationState<T> animationState) {
        if (animationState != null)
            codeAnimations.animate(animatable, animationState, this);
    }
}

