package com.cerbon.bosses_of_mass_destruction.particle;


import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.math.vector.Vector3d;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Function;

public class SimpleParticleProvider implements IParticleFactory<BasicParticleType> {
    private final IAnimatedSprite spriteSet;
    private final Function<ParticleContext, Particle> particleProvider;

    public SimpleParticleProvider(IAnimatedSprite spriteSet, Function<ParticleContext, Particle> particleProvider){
        this.spriteSet = spriteSet;
        this.particleProvider = particleProvider;
    }

    @Nullable
    @Override
    public Particle createParticle(@Nonnull BasicParticleType type, @Nonnull ClientWorld level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        return particleProvider.apply(new ParticleContext(spriteSet, level, new Vector3d(x, y, z), new Vector3d(xSpeed, ySpeed, zSpeed), true));
    }
}
