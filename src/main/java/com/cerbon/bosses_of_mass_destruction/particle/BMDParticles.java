package com.cerbon.bosses_of_mass_destruction.particle;

import com.cerbon.bosses_of_mass_destruction.util.BMDConstants;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BMDParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, BMDConstants.MOD_ID);

    public static final RegistryObject<SimpleParticleType> DISAPPEARING_SWIRL = PARTICLE_TYPES.register("disappearing_swirl",
            () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> SOUL_FLAME = PARTICLE_TYPES.register("soul_flame",
            () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> MAGIC_CIRCLE = PARTICLE_TYPES.register("magic_circle",
            () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> OBSIDILITH_BURST = PARTICLE_TYPES.register("obsidilith_burst",
            () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> ENCHANT = PARTICLE_TYPES.register("enchant",
            () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> OBSIDILITH_BURST_INDICATOR = PARTICLE_TYPES.register("obsidilith_burst_indicator",
            () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> OBSIDILITH_WAVE = PARTICLE_TYPES.register("obsidilith_wave",
            () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> OBSIDILITH_WAVE_INDICATOR = PARTICLE_TYPES.register("obsidilith_wave_indicator",
            () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> DOWNSPARKLE = PARTICLE_TYPES.register("downsparkle",
            () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> OBSIDILITH_SPIKE_INDICATOR = PARTICLE_TYPES.register("obsidilith_spike_indicator",
            () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> OBSIDILITH_SPIKE = PARTICLE_TYPES.register("obsidilith_spike",
            () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> PILLAR_RUNE = PARTICLE_TYPES.register("pillar_rune",
            () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> PILLAR_SPAWN_INDICATOR = PARTICLE_TYPES.register("pillar_spawn_indicator",
            () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> PILLAR_SPAWN_INDICATOR_2 = PARTICLE_TYPES.register("pillar_spawn_indicator_2",
            () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> OBSIDILITH_ANVIL_INDICATOR = PARTICLE_TYPES.register("obsidilith_anvil_indicator",
            () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> SPARKLES = PARTICLE_TYPES.register("sparkles",
            () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> GAUNTLET_REVIVE_SPARKLES = PARTICLE_TYPES.register("gauntlet_revive_sparkles",
            () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> EYE_OPEN = PARTICLE_TYPES.register("eye_open",
            () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> LINE = PARTICLE_TYPES.register("line",
            () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> VOID_BLOSSOM_SPIKE_INDICATOR = PARTICLE_TYPES.register("void_blossom_spike_indicator",
            () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> VOID_BLOSSOM_SPIKE_WAVE_INDICATOR = PARTICLE_TYPES.register("void_blossom_spike_wave_indicator",
            () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> PETAL = PARTICLE_TYPES.register("petal",
            () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> SPORE = PARTICLE_TYPES.register("spore",
            () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> SPORE_INDICATOR = PARTICLE_TYPES.register("spore_indicator",
            () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> FLUFF = PARTICLE_TYPES.register("fluff",
            () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> POLLEN = PARTICLE_TYPES.register("pollen",
            () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> EARTHDIVE_INDICATOR = PARTICLE_TYPES.register("earthdive_indicator",
            () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> ROD = PARTICLE_TYPES.register("rod",
            () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> GROUND_ROD = PARTICLE_TYPES.register("ground_rod",
            () -> new SimpleParticleType(true));

    public static void register(IEventBus eventBus){
        PARTICLE_TYPES.register(eventBus);
    }
}
