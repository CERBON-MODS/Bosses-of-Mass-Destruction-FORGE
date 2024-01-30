package com.cerbon.bosses_of_mass_destruction.client.render;

import net.minecraft.resources.ResourceLocation;

@FunctionalInterface
public interface ITextureProvider<T> {
    ResourceLocation getTexture(T entity);
}

