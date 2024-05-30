package com.cerbon.bosses_of_mass_destruction.particle;


import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.vector.Vector3d;

public class ParticleContext {
    public final IAnimatedSprite spriteSet;
    public final ClientWorld level;
    public final Vector3d pos;
    public final Vector3d vel;
    public final Boolean cycleSprites;

    public ParticleContext(IAnimatedSprite spriteSet,
                           ClientWorld level,
                           Vector3d pos,
                           Vector3d vel,
                           Boolean cycleSprites) {
        this.spriteSet = spriteSet;
        this.level = level;
        this.pos = pos;
        this.vel = vel;
        this.cycleSprites = cycleSprites;
    }
}
