package com.cerbon.bosses_of_mass_destruction.particle;

import com.cerbon.bosses_of_mass_destruction.util.BMDConstants;
import com.cerbon.cerbons_api.api.registry.RegistryEntry;
import com.cerbon.cerbons_api.api.registry.ResourcefulRegistries;
import com.cerbon.cerbons_api.api.registry.ResourcefulRegistry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;

public class BMDParticles {
    public static final ResourcefulRegistry<ParticleType<?>> PARTICLE_TYPES = ResourcefulRegistries.create(BuiltInRegistries.PARTICLE_TYPE, BMDConstants.MOD_ID);

    public static final RegistryEntry<BMDSimpleParticleType> DISAPPEARING_SWIRL = PARTICLE_TYPES.register("disappearing_swirl", () ->
            new BMDSimpleParticleType(true));

    public static final RegistryEntry<BMDSimpleParticleType> SOUL_FLAME = PARTICLE_TYPES.register("soul_flame", () ->
            new BMDSimpleParticleType(true));

    public static final RegistryEntry<BMDSimpleParticleType> MAGIC_CIRCLE = PARTICLE_TYPES.register("magic_circle", () ->
            new BMDSimpleParticleType(true));

    public static final RegistryEntry<BMDSimpleParticleType> OBSIDILITH_BURST = PARTICLE_TYPES.register("obsidilith_burst", () ->
            new BMDSimpleParticleType(true));

    public static final RegistryEntry<BMDSimpleParticleType> ENCHANT = PARTICLE_TYPES.register("enchant", () ->
            new BMDSimpleParticleType(true));

    public static final RegistryEntry<BMDSimpleParticleType> OBSIDILITH_BURST_INDICATOR = PARTICLE_TYPES.register("obsidilith_burst_indicator", () ->
            new BMDSimpleParticleType(true));

    public static final RegistryEntry<BMDSimpleParticleType> OBSIDILITH_WAVE = PARTICLE_TYPES.register("obsidilith_wave", () ->
            new BMDSimpleParticleType(true));

    public static final RegistryEntry<BMDSimpleParticleType> OBSIDILITH_WAVE_INDICATOR = PARTICLE_TYPES.register("obsidilith_wave_indicator", () ->
            new BMDSimpleParticleType(true));

    public static final RegistryEntry<BMDSimpleParticleType> DOWNSPARKLE = PARTICLE_TYPES.register("downsparkle", () ->
            new BMDSimpleParticleType(true));

    public static final RegistryEntry<BMDSimpleParticleType> OBSIDILITH_SPIKE_INDICATOR = PARTICLE_TYPES.register("obsidilith_spike_indicator", () ->
            new BMDSimpleParticleType(true));

    public static final RegistryEntry<BMDSimpleParticleType> OBSIDILITH_SPIKE = PARTICLE_TYPES.register("obsidilith_spike", () ->
            new BMDSimpleParticleType(true));

    public static final RegistryEntry<BMDSimpleParticleType> PILLAR_RUNE = PARTICLE_TYPES.register("pillar_rune", () ->
            new BMDSimpleParticleType(true));

    public static final RegistryEntry<BMDSimpleParticleType> PILLAR_SPAWN_INDICATOR = PARTICLE_TYPES.register("pillar_spawn_indicator", () ->
            new BMDSimpleParticleType(true));

    public static final RegistryEntry<BMDSimpleParticleType> PILLAR_SPAWN_INDICATOR_2 = PARTICLE_TYPES.register("pillar_spawn_indicator_2", () ->
            new BMDSimpleParticleType(true));

    public static final RegistryEntry<BMDSimpleParticleType> OBSIDILITH_ANVIL_INDICATOR = PARTICLE_TYPES.register("obsidilith_anvil_indicator", () ->
            new BMDSimpleParticleType(true));

    public static final RegistryEntry<BMDSimpleParticleType> SPARKLES = PARTICLE_TYPES.register("sparkles", () ->
            new BMDSimpleParticleType(true));

    public static final RegistryEntry<BMDSimpleParticleType> GAUNTLET_REVIVE_SPARKLES = PARTICLE_TYPES.register("gauntlet_revive_sparkles", () ->
            new BMDSimpleParticleType(true));

    public static final RegistryEntry<BMDSimpleParticleType> EYE_OPEN = PARTICLE_TYPES.register("eye_open", () ->
            new BMDSimpleParticleType(true));

    public static final RegistryEntry<BMDSimpleParticleType> LINE = PARTICLE_TYPES.register("line", () ->
            new BMDSimpleParticleType(true));

    public static final RegistryEntry<BMDSimpleParticleType> VOID_BLOSSOM_SPIKE_INDICATOR = PARTICLE_TYPES.register("void_blossom_spike_indicator", () ->
            new BMDSimpleParticleType(true));

    public static final RegistryEntry<BMDSimpleParticleType> VOID_BLOSSOM_SPIKE_WAVE_INDICATOR = PARTICLE_TYPES.register("void_blossom_spike_wave_indicator", () ->
            new BMDSimpleParticleType(true));

    public static final RegistryEntry<BMDSimpleParticleType> PETAL = PARTICLE_TYPES.register("petal", () ->
            new BMDSimpleParticleType(true));

    public static final RegistryEntry<BMDSimpleParticleType> SPORE = PARTICLE_TYPES.register("spore", () ->
            new BMDSimpleParticleType(true));

    public static final RegistryEntry<BMDSimpleParticleType> SPORE_INDICATOR = PARTICLE_TYPES.register("spore_indicator", () ->
            new BMDSimpleParticleType(true));

    public static final RegistryEntry<BMDSimpleParticleType> FLUFF = PARTICLE_TYPES.register("fluff", () ->
            new BMDSimpleParticleType(false));

    public static final RegistryEntry<BMDSimpleParticleType> POLLEN = PARTICLE_TYPES.register("pollen", () ->
            new BMDSimpleParticleType(true));

    public static final RegistryEntry<BMDSimpleParticleType> EARTHDIVE_INDICATOR = PARTICLE_TYPES.register("earthdive_indicator", () ->
            new BMDSimpleParticleType(false));

    public static final RegistryEntry<BMDSimpleParticleType> ROD = PARTICLE_TYPES.register("rod", () ->
            new BMDSimpleParticleType(false));

    public static final RegistryEntry<BMDSimpleParticleType> GROUND_ROD = PARTICLE_TYPES.register("ground_rod", () ->
            new BMDSimpleParticleType(false));

    public static final int FULL_BRIGHT = 15728880;

    public static void register() {
        PARTICLE_TYPES.register();
    }
}
