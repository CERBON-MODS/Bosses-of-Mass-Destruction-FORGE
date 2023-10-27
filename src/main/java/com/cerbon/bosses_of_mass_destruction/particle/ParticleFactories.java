package com.cerbon.bosses_of_mass_destruction.particle;

import com.cerbon.bosses_of_mass_destruction.util.BMDColors;
import com.cerbon.bosses_of_mass_destruction.util.maelstrom.static_utilities.MathUtils;

public class ParticleFactories {
    public static void cometTrail(){
        new ClientParticleBuilder(BMDParticles.DISAPPEARING_SWIRL.get())
                .color(f -> MathUtils.lerpVec(f, BMDColors.COMET_BLUE, BMDColors.FADED_COMET_BLUE))
                .brightness(BMDParticles.FULL_BRIGHT)
                .scale(f -> 0.5f + f * 0.3f);
    }

    public static void soulFlame(){
        new ClientParticleBuilder(BMDParticles.SOUL_FLAME.get())
                .brightness(BMDParticles.FULL_BRIGHT);
    }
}
