package com.cerbon.bosses_of_mass_destruction.particle;

import com.cerbon.bosses_of_mass_destruction.util.BMDColors;
import com.cerbon.bosses_of_mass_destruction.util.BMDConstants;
import com.cerbon.bosses_of_mass_destruction.util.VanillaCopies;
import com.cerbon.bosses_of_mass_destruction.util.maelstrom.static_utilities.MathUtils;
import com.cerbon.bosses_of_mass_destruction.util.maelstrom.static_utilities.RandomUtils;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
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

    public static void initClient(RegisterParticleProvidersEvent event){
        final int FULL_BRIGHT = 15728880;

        event.registerSpriteSet(BMDParticles.DISAPPEARING_SWIRL.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> new SimpleParticle(particleContext, RandomUtils.range(15,20), VanillaCopies::buildBillBoardGeometry, true, true)));

        event.registerSpriteSet(BMDParticles.SOUL_FLAME.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> {
                            SimpleParticle particle = new SimpleParticle(particleContext, RandomUtils.range(15, 20), VanillaCopies::buildBillBoardGeometry, true, true);
                            particle.setColorOverride(f -> BMDColors.COMET_BLUE);
                            particle.setBrightnessOverride(f -> FULL_BRIGHT);
                            return particle;
                        }));

        event.registerSpriteSet(BMDParticles.MAGIC_CIRCLE.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> {
                            SimpleParticle particle = new SimpleParticle(particleContext, 40, VanillaCopies::buildBillBoardGeometry, true, true);
                            particle.setBrightnessOverride(f -> FULL_BRIGHT);
                            particle.scale(4f);
                            return particle;
                        }));

        event.registerSpriteSet(BMDParticles.OBSIDILITH_BURST.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> {
                            SimpleParticle particle = new SimpleParticle(particleContext, RandomUtils.range(7, 15), VanillaCopies::buildBillBoardGeometry, true, true);
                            particle.setBrightnessOverride(f -> FULL_BRIGHT);
                            particle.scale(4f);
                            particle.setColorOverride(age -> MathUtils.lerpVec(age, BMDColors.ORANGE, BMDColors.RUNIC_BROWN));
                            return particle;
                        }));

        event.registerSpriteSet(BMDParticles.ENCHANT.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> {
                            SimpleParticle particle = new SimpleParticle(particleContext, RandomUtils.range(30, 50), VanillaCopies::buildBillBoardGeometry, false, true);
                            particle.setBrightnessOverride(f -> FULL_BRIGHT);
                            particle.setScaleOverride(f -> (float) ((Math.sin((double) f * Math.PI) + 1f) * 0.1f));
                            return particle;
                        }));

        event.registerSpriteSet(BMDParticles.OBSIDILITH_BURST_INDICATOR.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> {
                            //TODO: Change particle age to burstDelay after creating the BurstAction class
                            SimpleParticle particle = new SimpleParticle(particleContext, 30 + RandomUtils.range(-1, 2), VanillaCopies::buildFlatGeometry, true, true);
                            particle.setColorOverride(f -> BMDColors.ORANGE);
                            particle.setColorVariation(0.3);
                            particle.setBrightnessOverride(f -> FULL_BRIGHT);
                            particle.setScaleOverride(f -> (1 + f) * 0.25f);
                            return particle;
                        }));

        event.registerSpriteSet(BMDParticles.OBSIDILITH_WAVE.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> {
                            SimpleParticle particle = new SimpleParticle(particleContext, RandomUtils.range(7, 15), VanillaCopies::buildBillBoardGeometry, true, true);
                            particle.setBrightnessOverride(f -> FULL_BRIGHT);
                            particle.scale(4f);
                            particle.setColorVariation(0.25);
                            particle.setColorOverride(age -> MathUtils.lerpVec(age, BMDColors.RED, BMDColors.DARK_RED));
                            return particle;
                        }));

        event.registerSpriteSet(BMDParticles.OBSIDILITH_WAVE_INDICATOR.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> {
                            //TODO: Change particle age to waveDelay after creating the WaveAction class
                            SimpleParticle particle = new SimpleParticle(particleContext, 20 + RandomUtils.range(-1, 2), VanillaCopies::buildFlatGeometry, true, true);
                            particle.setColorOverride(f -> BMDColors.RED);
                            particle.setColorVariation(0.3);
                            particle.setBrightnessOverride(f -> FULL_BRIGHT);
                            particle.setScaleOverride(f -> (1 + f) * 0.25f);
                            return particle;
                        }));

        event.registerSpriteSet(BMDParticles.DOWNSPARKLE.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> new SimpleParticle(particleContext, RandomUtils.range(15, 20), VanillaCopies::buildBillBoardGeometry, true, true)));

        event.registerSpriteSet(BMDParticles.OBSIDILITH_SPIKE_INDICATOR.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> {
                            //TODO: Change particle age to waveDelay after creating the WaveAction class
                            SimpleParticle particle = new SimpleParticle(particleContext, 20 +  RandomUtils.range(-1, 2), VanillaCopies::buildFlatGeometry, true, true);
                            particle.setColorOverride(f -> BMDColors.COMET_BLUE);
                            particle.setColorVariation(0.3);
                            particle.setBrightnessOverride(f -> FULL_BRIGHT);
                            particle.setScaleOverride(f -> (1 + f) * 0.25f);
                            return particle;
                        }));

        event.registerSpriteSet(BMDParticles.OBSIDILITH_SPIKE.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> {
                            SimpleParticle particle = new SimpleParticle(particleContext, RandomUtils.range(15, 20), VanillaCopies::buildBillBoardGeometry, true, true);
                            particle.setColorOverride(age -> MathUtils.lerpVec(age, BMDColors.WHITE, BMDColors.COMET_BLUE));
                            particle.setBrightnessOverride(f -> FULL_BRIGHT);
                            particle.setColorVariation(0.25);
                            return particle;
                        }));

        event.registerSpriteSet(BMDParticles.PILLAR_RUNE.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> {
                            SimpleParticle particle = new SimpleParticle(particleContext, 10, VanillaCopies::buildBillBoardGeometry, false, true);
                            particle.setBrightnessOverride(f -> FULL_BRIGHT);
                            particle.setScaleOverride(f -> (float) ((Math.sin((double) f * Math.PI) + 1f) * 0.1f));
                            particle.setColorOverride(age -> MathUtils.lerpVec(age, BMDColors.WHITE, BMDColors.ENDER_PURPLE));
                            particle.setColorVariation(0.2);
                            return particle;
                        }));

        event.registerSpriteSet(BMDParticles.PILLAR_SPAWN_INDICATOR.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> {
                            //TODO: Change particle age to pillarDelay after creating the PillarAction class
                            SimpleParticle particle = new SimpleParticle(particleContext, 40, VanillaCopies::buildBillBoardGeometry, true, true);
                            particle.setColorOverride(f -> BMDColors.ENDER_PURPLE);
                            particle.scale(2.0f);
                            particle.setColorVariation(0.25);
                            particle.setBrightnessOverride(f -> FULL_BRIGHT);
                            return particle;
                        }));

        event.registerSpriteSet(BMDParticles.PILLAR_SPAWN_INDICATOR_2.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> {
                            //TODO: Change particle age to pillarDelay after creating the PillarAction class
                            SimpleParticle particle = new SimpleParticle(particleContext, 40, VanillaCopies::buildBillBoardGeometry, false, true);
                            particle.setBrightnessOverride(f -> FULL_BRIGHT);
                            particle.setScaleOverride(f -> (float) ((Math.sin((double) f * Math.PI) + 1f) * 0.1f));
                            particle.setColorOverride(age -> MathUtils.lerpVec(age, BMDColors.WHITE, BMDColors.ENDER_PURPLE));
                            particle.setColorVariation(0.2);
                            return particle;
                        }));

        event.registerSpriteSet(BMDParticles.OBSIDILITH_ANVIL_INDICATOR.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> {
                            SimpleParticle particle = new SimpleParticle(particleContext, RandomUtils.range(25, 27), VanillaCopies::buildFlatGeometry, true, true);
                            particle.setColorOverride(f -> BMDColors.ENDER_PURPLE);
                            particle.setColorVariation(0.3);
                            particle.setBrightnessOverride(f -> FULL_BRIGHT);
                            particle.setScaleOverride(f -> (1 + f) * 0.25f);
                            return particle;
                        }));

        event.registerSpriteSet(BMDParticles.SPARKLES.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> new SimpleParticle(particleContext, RandomUtils.range(15, 20), VanillaCopies::buildBillBoardGeometry, true, true)));

        event.registerSpriteSet(BMDParticles.GAUNTLET_REVIVE_SPARKLES.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> {
                            SimpleParticle particle = new SimpleParticle(particleContext, RandomUtils.range(15, 20), VanillaCopies::buildBillBoardGeometry, true, true);
                            particle.setColorOverride(f -> BMDColors.LASER_RED);
                            particle.setColorVariation(0.25);
                            particle.setBrightnessOverride(f -> FULL_BRIGHT);
                            return particle;
                        }));

        event.registerSpriteSet(BMDParticles.EYE_OPEN.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> new SimpleParticle(particleContext, RandomUtils.range(60, 70), VanillaCopies::buildBillBoardGeometry, true, true)));

        event.registerSpriteSet(BMDParticles.LINE.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> new SimpleParticle(particleContext, RandomUtils.range(20, 30), VanillaCopies::buildBillBoardGeometry, true, true)));

        event.registerSpriteSet(BMDParticles.ROD.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> new SimpleParticle(particleContext, RandomUtils.range(8, 10), VanillaCopies::buildBillBoardGeometry, true, true)));

        event.registerSpriteSet(BMDParticles.GROUND_ROD.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> new SimpleParticle(particleContext, RandomUtils.range(8, 10), VanillaCopies::buildFlatGeometry, true, true)));

        event.registerSpriteSet(BMDParticles.VOID_BLOSSOM_SPIKE_INDICATOR.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> {
                            //TODO: Change particle age to indicatorDelay after creating the SpikeAction class
                            SimpleParticle particle = new SimpleParticle(particleContext, 20 + RandomUtils.range(-1, 2), VanillaCopies::buildFlatGeometry, true, true);
                            particle.setColorOverride(f -> BMDColors.ENDER_PURPLE);
                            particle.setColorVariation(0.2);
                            particle.setBrightnessOverride(f -> FULL_BRIGHT);
                            particle.setScaleOverride(f -> (1 + f) * 0.25f);
                            return particle;
                        }));

        event.registerSpriteSet(BMDParticles.VOID_BLOSSOM_SPIKE_WAVE_INDICATOR.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> {
                            //TODO: Change particle age to indicatorDelay after creating the SpikeWaveAction class
                            SimpleParticle particle = new SimpleParticle(particleContext, 30 + RandomUtils.range(-1, 2), VanillaCopies::buildFlatGeometry, true, true);
                            particle.setColorOverride(f -> BMDColors.ENDER_PURPLE);
                            particle.setColorVariation(0.2);
                            particle.setBrightnessOverride(f -> FULL_BRIGHT);
                            particle.setScaleOverride(f -> (1 + f) * 0.25f);
                            return particle;
                        }));

        event.registerSpriteSet(BMDParticles.PETAL.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> new SimpleParticle(particleContext, RandomUtils.range(15, 20), VanillaCopies::buildBillBoardGeometry, false, true)));

        event.registerSpriteSet(BMDParticles.POLLEN.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> {
                            SimpleParticle particle = new SimpleParticle(particleContext, RandomUtils.range(15, 20), VanillaCopies::buildBillBoardGeometry, false, true);
                            particle.setColorOverride(f -> new Vec3(1.0, 0.9, 0.4));
                            particle.setColorVariation(0.15);
                            particle.setBrightnessOverride(f -> FULL_BRIGHT);
                            particle.setScaleOverride(f -> 0.05f * (1 - f * 0.25f));
                            int randomRot =RandomUtils.range(0, 360);
                            float angularMomentum = RandomUtils.randSign() * 4f;
                            particle.setRotationOverride(particle1 -> randomRot + particle1.getAge() * angularMomentum);
                            return particle;
                        }));

        event.registerSpriteSet(BMDParticles.SPORE.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> {
                            SimpleParticle particle = new SimpleParticle(particleContext, RandomUtils.range(7, 15), VanillaCopies::buildBillBoardGeometry, true, true);
                            particle.setBrightnessOverride(f -> FULL_BRIGHT);
                            particle.scale(4f);
                            particle.setColorOverride(age -> MathUtils.lerpVec(age, BMDColors.GREEN, BMDColors.DARK_GREEN));
                            particle.setColorVariation(0.25);
                            return particle;
                        }));

        event.registerSpriteSet(BMDParticles.SPORE_INDICATOR.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> {
                            //TODO: Change particle age to explosionDelay after creating the SporeBallProjectile class
                            SimpleParticle particle = new SimpleParticle(particleContext, 30 + RandomUtils.range(-1, 2), VanillaCopies::buildFlatGeometry, true, true);
                            particle.setColorOverride(f -> BMDColors.GREEN);
                            particle.setColorVariation(0.35);
                            particle.setBrightnessOverride(f -> FULL_BRIGHT);
                            particle.setScaleOverride(f -> (1 + f) * 0.25f);
                            return particle;
                        }));

        event.registerSpriteSet(BMDParticles.FLUFF.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> new SimpleParticle(particleContext, RandomUtils.range(15, 20), VanillaCopies::buildBillBoardGeometry, true, true)));

        event.registerSpriteSet(BMDParticles.EARTHDIVE_INDICATOR.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> {
                            SimpleParticle particle = new SimpleParticle(particleContext, RandomUtils.range(15, 20), VanillaCopies::buildBillBoardGeometry, true, false);
                            particle.setColorOverride(f -> BMDColors.RUNIC_BROWN);
                            particle.setColorVariation(0.25);
                            particle.setBrightnessOverride(f -> FULL_BRIGHT);
                            particle.setScaleOverride(f -> (1 - (f * 0.25f)) * 0.35f);
                            return particle;
                        }));
    }

    public static void register(IEventBus eventBus){
        PARTICLE_TYPES.register(eventBus);
    }
}
