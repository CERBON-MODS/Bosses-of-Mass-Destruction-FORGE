package com.cerbon.bosses_of_mass_destruction.particle;

import com.cerbon.cerbons_api.api.general.particle.ClientParticleBuilder;
import com.cerbon.cerbons_api.api.static_utilities.MathUtils;
import com.cerbon.cerbons_api.api.static_utilities.Vec3Colors;

public class ParticleFactories {
    public static ClientParticleBuilder cometTrail(){
        return new ClientParticleBuilder(BMDParticles.DISAPPEARING_SWIRL.get())
                .color(f -> MathUtils.lerpVec(f, Vec3Colors.COMET_BLUE, Vec3Colors.FADED_COMET_BLUE))
                .brightness(BMDParticles.FULL_BRIGHT)
                .scale(f -> 0.5f + f * 0.3f);
    }

    public static ClientParticleBuilder soulFlame(){
        return new ClientParticleBuilder(BMDParticles.SOUL_FLAME.get())
                .brightness(BMDParticles.FULL_BRIGHT);
    }
}
