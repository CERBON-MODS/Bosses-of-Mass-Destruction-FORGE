package com.cerbon.bosses_of_mass_destruction.particle;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.MathUtils;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.RandomUtils;
import com.cerbon.bosses_of_mass_destruction.entity.custom.obsidilith.BurstAction;
import com.cerbon.bosses_of_mass_destruction.entity.custom.obsidilith.PillarAction;
import com.cerbon.bosses_of_mass_destruction.entity.custom.obsidilith.WaveAction;
import com.cerbon.bosses_of_mass_destruction.entity.custom.void_blossom.SpikeAction;
import com.cerbon.bosses_of_mass_destruction.entity.custom.void_blossom.SpikeWaveAction;
import com.cerbon.bosses_of_mass_destruction.projectile.SporeBallProjectile;
import com.cerbon.bosses_of_mass_destruction.util.BMDColors;
import com.cerbon.bosses_of_mass_destruction.util.BMDConstants;
import com.cerbon.bosses_of_mass_destruction.util.VanillaCopies;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleType;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class BMDParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, BMDConstants.MOD_ID);

    public static final RegistryObject<BasicParticleType> DISAPPEARING_SWIRL = PARTICLE_TYPES.register("disappearing_swirl",
            () -> new BasicParticleType(true));

    public static final RegistryObject<BasicParticleType> SOUL_FLAME = PARTICLE_TYPES.register("soul_flame",
            () -> new BasicParticleType(true));

    public static final RegistryObject<BasicParticleType> MAGIC_CIRCLE = PARTICLE_TYPES.register("magic_circle",
            () -> new BasicParticleType(true));

    public static final RegistryObject<BasicParticleType> OBSIDILITH_BURST = PARTICLE_TYPES.register("obsidilith_burst",
            () -> new BasicParticleType(true));

    public static final RegistryObject<BasicParticleType> ENCHANT = PARTICLE_TYPES.register("enchant",
            () -> new BasicParticleType(true));

    public static final RegistryObject<BasicParticleType> OBSIDILITH_BURST_INDICATOR = PARTICLE_TYPES.register("obsidilith_burst_indicator",
            () -> new BasicParticleType(true));

    public static final RegistryObject<BasicParticleType> OBSIDILITH_WAVE = PARTICLE_TYPES.register("obsidilith_wave",
            () -> new BasicParticleType(true));

    public static final RegistryObject<BasicParticleType> OBSIDILITH_WAVE_INDICATOR = PARTICLE_TYPES.register("obsidilith_wave_indicator",
            () -> new BasicParticleType(true));

    public static final RegistryObject<BasicParticleType> DOWNSPARKLE = PARTICLE_TYPES.register("downsparkle",
            () -> new BasicParticleType(true));

    public static final RegistryObject<BasicParticleType> OBSIDILITH_SPIKE_INDICATOR = PARTICLE_TYPES.register("obsidilith_spike_indicator",
            () -> new BasicParticleType(true));

    public static final RegistryObject<BasicParticleType> OBSIDILITH_SPIKE = PARTICLE_TYPES.register("obsidilith_spike",
            () -> new BasicParticleType(true));

    public static final RegistryObject<BasicParticleType> PILLAR_RUNE = PARTICLE_TYPES.register("pillar_rune",
            () -> new BasicParticleType(true));

    public static final RegistryObject<BasicParticleType> PILLAR_SPAWN_INDICATOR = PARTICLE_TYPES.register("pillar_spawn_indicator",
            () -> new BasicParticleType(true));

    public static final RegistryObject<BasicParticleType> PILLAR_SPAWN_INDICATOR_2 = PARTICLE_TYPES.register("pillar_spawn_indicator_2",
            () -> new BasicParticleType(true));

    public static final RegistryObject<BasicParticleType> OBSIDILITH_ANVIL_INDICATOR = PARTICLE_TYPES.register("obsidilith_anvil_indicator",
            () -> new BasicParticleType(true));

    public static final RegistryObject<BasicParticleType> SPARKLES = PARTICLE_TYPES.register("sparkles",
            () -> new BasicParticleType(true));

    public static final RegistryObject<BasicParticleType> GAUNTLET_REVIVE_SPARKLES = PARTICLE_TYPES.register("gauntlet_revive_sparkles",
            () -> new BasicParticleType(true));

    public static final RegistryObject<BasicParticleType> EYE_OPEN = PARTICLE_TYPES.register("eye_open",
            () -> new BasicParticleType(true));

    public static final RegistryObject<BasicParticleType> LINE = PARTICLE_TYPES.register("line",
            () -> new BasicParticleType(true));

    public static final RegistryObject<BasicParticleType> VOID_BLOSSOM_SPIKE_INDICATOR = PARTICLE_TYPES.register("void_blossom_spike_indicator",
            () -> new BasicParticleType(true));

    public static final RegistryObject<BasicParticleType> VOID_BLOSSOM_SPIKE_WAVE_INDICATOR = PARTICLE_TYPES.register("void_blossom_spike_wave_indicator",
            () -> new BasicParticleType(true));

    public static final RegistryObject<BasicParticleType> PETAL = PARTICLE_TYPES.register("petal",
            () -> new BasicParticleType(true));

    public static final RegistryObject<BasicParticleType> SPORE = PARTICLE_TYPES.register("spore",
            () -> new BasicParticleType(true));

    public static final RegistryObject<BasicParticleType> SPORE_INDICATOR = PARTICLE_TYPES.register("spore_indicator",
            () -> new BasicParticleType(true));

    public static final RegistryObject<BasicParticleType> FLUFF = PARTICLE_TYPES.register("fluff",
            () -> new BasicParticleType(true));

    public static final RegistryObject<BasicParticleType> POLLEN = PARTICLE_TYPES.register("pollen",
            () -> new BasicParticleType(true));

    public static final RegistryObject<BasicParticleType> EARTHDIVE_INDICATOR = PARTICLE_TYPES.register("earthdive_indicator",
            () -> new BasicParticleType(true));

    public static final RegistryObject<BasicParticleType> ROD = PARTICLE_TYPES.register("rod",
            () -> new BasicParticleType(true));

    public static final RegistryObject<BasicParticleType> GROUND_ROD = PARTICLE_TYPES.register("ground_rod",
            () -> new BasicParticleType(true));

    public static final int FULL_BRIGHT = 15728880;

    @OnlyIn(Dist.CLIENT)
    public static void initClient(){
        ParticleManager particleEngine = Minecraft.getInstance().particleEngine;

        particleEngine.register(BMDParticles.DISAPPEARING_SWIRL.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> new SimpleParticle(particleContext, RandomUtils.range(15,20), VanillaCopies::buildBillBoardGeometry, true, true)));

        particleEngine.register(BMDParticles.SOUL_FLAME.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> {
                            SimpleParticle particle = new SimpleParticle(particleContext, RandomUtils.range(15, 20), VanillaCopies::buildBillBoardGeometry, true, true);
                            particle.setColorOverride(f -> BMDColors.COMET_BLUE);
                            particle.setBrightnessOverride(f -> FULL_BRIGHT);
                            return particle;
                        }));

        particleEngine.register(BMDParticles.MAGIC_CIRCLE.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> {
                            SimpleParticle particle = new SimpleParticle(particleContext, 40, VanillaCopies::buildBillBoardGeometry, true, true);
                            particle.setBrightnessOverride(f -> FULL_BRIGHT);
                            particle.scale(4f);
                            return particle;
                        }));

        particleEngine.register(BMDParticles.OBSIDILITH_BURST.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> {
                            SimpleParticle particle = new SimpleParticle(particleContext, RandomUtils.range(7, 15), VanillaCopies::buildBillBoardGeometry, true, true);
                            particle.setBrightnessOverride(f -> FULL_BRIGHT);
                            particle.scale(4f);
                            particle.setColorOverride(age -> MathUtils.lerpVec(age, BMDColors.ORANGE, BMDColors.RUNIC_BROWN));
                            return particle;
                        }));

        particleEngine.register(BMDParticles.ENCHANT.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> {
                            SimpleParticle particle = new SimpleParticle(particleContext, RandomUtils.range(30, 50), VanillaCopies::buildBillBoardGeometry, false, true);
                            particle.setBrightnessOverride(f -> FULL_BRIGHT);
                            particle.setScaleOverride(f -> (float) ((Math.sin((double) f * Math.PI) + 1f) * 0.1f));
                            return particle;
                        }));

        particleEngine.register(BMDParticles.OBSIDILITH_BURST_INDICATOR.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> {
                            SimpleParticle particle = new SimpleParticle(particleContext, BurstAction.burstDelay + RandomUtils.range(-1, 2), VanillaCopies::buildFlatGeometry, true, true);
                            particle.setColorOverride(f -> BMDColors.ORANGE);
                            particle.setColorVariation(0.3);
                            particle.setBrightnessOverride(f -> FULL_BRIGHT);
                            particle.setScaleOverride(f -> (1 + f) * 0.25f);
                            return particle;
                        }));

        particleEngine.register(BMDParticles.OBSIDILITH_WAVE.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> {
                            SimpleParticle particle = new SimpleParticle(particleContext, RandomUtils.range(7, 15), VanillaCopies::buildBillBoardGeometry, true, true);
                            particle.setBrightnessOverride(f -> FULL_BRIGHT);
                            particle.scale(4f);
                            particle.setColorVariation(0.25);
                            particle.setColorOverride(age -> MathUtils.lerpVec(age, BMDColors.RED, BMDColors.DARK_RED));
                            return particle;
                        }));

        particleEngine.register(BMDParticles.OBSIDILITH_WAVE_INDICATOR.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> {
                            SimpleParticle particle = new SimpleParticle(particleContext, WaveAction.waveDelay + RandomUtils.range(-1, 2), VanillaCopies::buildFlatGeometry, true, true);
                            particle.setColorOverride(f -> BMDColors.RED);
                            particle.setColorVariation(0.3);
                            particle.setBrightnessOverride(f -> FULL_BRIGHT);
                            particle.setScaleOverride(f -> (1 + f) * 0.25f);
                            return particle;
                        }));

        particleEngine.register(BMDParticles.DOWNSPARKLE.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> new SimpleParticle(particleContext, RandomUtils.range(15, 20), VanillaCopies::buildBillBoardGeometry, true, true)));

        particleEngine.register(BMDParticles.OBSIDILITH_SPIKE_INDICATOR.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> {
                            SimpleParticle particle = new SimpleParticle(particleContext, WaveAction.waveDelay +  RandomUtils.range(-1, 2), VanillaCopies::buildFlatGeometry, true, true);
                            particle.setColorOverride(f -> BMDColors.COMET_BLUE);
                            particle.setColorVariation(0.3);
                            particle.setBrightnessOverride(f -> FULL_BRIGHT);
                            particle.setScaleOverride(f -> (1 + f) * 0.25f);
                            return particle;
                        }));

        particleEngine.register(BMDParticles.OBSIDILITH_SPIKE.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> {
                            SimpleParticle particle = new SimpleParticle(particleContext, RandomUtils.range(15, 20), VanillaCopies::buildBillBoardGeometry, true, true);
                            particle.setColorOverride(age -> MathUtils.lerpVec(age, BMDColors.WHITE, BMDColors.COMET_BLUE));
                            particle.setBrightnessOverride(f -> FULL_BRIGHT);
                            particle.setColorVariation(0.25);
                            return particle;
                        }));

        particleEngine.register(BMDParticles.PILLAR_RUNE.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> {
                            SimpleParticle particle = new SimpleParticle(particleContext, 10, VanillaCopies::buildBillBoardGeometry, false, true);
                            particle.setBrightnessOverride(f -> FULL_BRIGHT);
                            particle.setScaleOverride(f -> (float) ((Math.sin((double) f * Math.PI) + 1f) * 0.1f));
                            particle.setColorOverride(age -> MathUtils.lerpVec(age, BMDColors.WHITE, BMDColors.ENDER_PURPLE));
                            particle.setColorVariation(0.2);
                            return particle;
                        }));

        particleEngine.register(BMDParticles.PILLAR_SPAWN_INDICATOR.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> {
                            SimpleParticle particle = new SimpleParticle(particleContext, PillarAction.pillarDelay, VanillaCopies::buildBillBoardGeometry, true, true);
                            particle.setColorOverride(f -> BMDColors.ENDER_PURPLE);
                            particle.scale(2.0f);
                            particle.setColorVariation(0.25);
                            particle.setBrightnessOverride(f -> FULL_BRIGHT);
                            return particle;
                        }));

        particleEngine.register(BMDParticles.PILLAR_SPAWN_INDICATOR_2.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> {
                            SimpleParticle particle = new SimpleParticle(particleContext, PillarAction.pillarDelay, VanillaCopies::buildBillBoardGeometry, false, true);
                            particle.setBrightnessOverride(f -> FULL_BRIGHT);
                            particle.setScaleOverride(f -> (float) ((Math.sin((double) f * Math.PI) + 1f) * 0.1f));
                            particle.setColorOverride(age -> MathUtils.lerpVec(age, BMDColors.WHITE, BMDColors.ENDER_PURPLE));
                            particle.setColorVariation(0.2);
                            return particle;
                        }));

        particleEngine.register(BMDParticles.OBSIDILITH_ANVIL_INDICATOR.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> {
                            SimpleParticle particle = new SimpleParticle(particleContext, RandomUtils.range(25, 27), VanillaCopies::buildFlatGeometry, true, true);
                            particle.setColorOverride(f -> BMDColors.ENDER_PURPLE);
                            particle.setColorVariation(0.3);
                            particle.setBrightnessOverride(f -> FULL_BRIGHT);
                            particle.setScaleOverride(f -> (1 + f) * 0.25f);
                            return particle;
                        }));

        particleEngine.register(BMDParticles.SPARKLES.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> new SimpleParticle(particleContext, RandomUtils.range(15, 20), VanillaCopies::buildBillBoardGeometry, true, true)));

        particleEngine.register(BMDParticles.GAUNTLET_REVIVE_SPARKLES.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> {
                            SimpleParticle particle = new SimpleParticle(particleContext, RandomUtils.range(15, 20), VanillaCopies::buildBillBoardGeometry, true, true);
                            particle.setColorOverride(f -> BMDColors.LASER_RED);
                            particle.setColorVariation(0.25);
                            particle.setBrightnessOverride(f -> FULL_BRIGHT);
                            return particle;
                        }));

        particleEngine.register(BMDParticles.EYE_OPEN.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> new SimpleParticle(particleContext, RandomUtils.range(60, 70), VanillaCopies::buildBillBoardGeometry, true, true)));

        particleEngine.register(BMDParticles.LINE.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> new SimpleParticle(particleContext, RandomUtils.range(20, 30), VanillaCopies::buildBillBoardGeometry, true, true)));

        particleEngine.register(BMDParticles.ROD.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> new SimpleParticle(particleContext, RandomUtils.range(8, 10), VanillaCopies::buildBillBoardGeometry, true, true)));

        particleEngine.register(BMDParticles.GROUND_ROD.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> new SimpleParticle(particleContext, RandomUtils.range(8, 10), VanillaCopies::buildFlatGeometry, true, true)));

        particleEngine.register(BMDParticles.VOID_BLOSSOM_SPIKE_INDICATOR.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> {
                            SimpleParticle particle = new SimpleParticle(particleContext, SpikeAction.indicatorDelay + RandomUtils.range(-1, 2), VanillaCopies::buildFlatGeometry, true, true);
                            particle.setColorOverride(f -> BMDColors.ENDER_PURPLE);
                            particle.setColorVariation(0.2);
                            particle.setBrightnessOverride(f -> FULL_BRIGHT);
                            particle.setScaleOverride(f -> (1 + f) * 0.25f);
                            return particle;
                        }));

        particleEngine.register(BMDParticles.VOID_BLOSSOM_SPIKE_WAVE_INDICATOR.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> {
                            SimpleParticle particle = new SimpleParticle(particleContext, SpikeWaveAction.indicatorDelay + RandomUtils.range(-1, 2), VanillaCopies::buildFlatGeometry, true, true);
                            particle.setColorOverride(f -> BMDColors.ENDER_PURPLE);
                            particle.setColorVariation(0.2);
                            particle.setBrightnessOverride(f -> FULL_BRIGHT);
                            particle.setScaleOverride(f -> (1 + f) * 0.25f);
                            return particle;
                        }));

        particleEngine.register(BMDParticles.PETAL.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> new SimpleParticle(particleContext, RandomUtils.range(15, 20), VanillaCopies::buildBillBoardGeometry, false, true)));

        particleEngine.register(BMDParticles.POLLEN.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> {
                            SimpleParticle particle = new SimpleParticle(particleContext, RandomUtils.range(15, 20), VanillaCopies::buildBillBoardGeometry, false, true);
                            particle.setColorOverride(f -> new Vector3d(1.0, 0.9, 0.4));
                            particle.setColorVariation(0.15);
                            particle.setBrightnessOverride(f -> FULL_BRIGHT);
                            particle.setScaleOverride(f -> 0.05f * (1 - f * 0.25f));
                            int randomRot =RandomUtils.range(0, 360);
                            float angularMomentum = RandomUtils.randSign() * 4f;
                            particle.setRotationOverride(particle1 -> randomRot + particle1.getAge() * angularMomentum);
                            return particle;
                        }));

        particleEngine.register(BMDParticles.SPORE.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> {
                            SimpleParticle particle = new SimpleParticle(particleContext, RandomUtils.range(7, 15), VanillaCopies::buildBillBoardGeometry, true, true);
                            particle.setBrightnessOverride(f -> FULL_BRIGHT);
                            particle.scale(4f);
                            particle.setColorOverride(age -> MathUtils.lerpVec(age, BMDColors.GREEN, BMDColors.DARK_GREEN));
                            particle.setColorVariation(0.25);
                            return particle;
                        }));

        particleEngine.register(BMDParticles.SPORE_INDICATOR.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> {
                            SimpleParticle particle = new SimpleParticle(particleContext, SporeBallProjectile.explosionDelay + RandomUtils.range(-1, 2), VanillaCopies::buildFlatGeometry, true, true);
                            particle.setColorOverride(f -> BMDColors.GREEN);
                            particle.setColorVariation(0.35);
                            particle.setBrightnessOverride(f -> FULL_BRIGHT);
                            particle.setScaleOverride(f -> (1 + f) * 0.25f);
                            return particle;
                        }));

        particleEngine.register(BMDParticles.FLUFF.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> new SimpleParticle(particleContext, RandomUtils.range(15, 20), VanillaCopies::buildBillBoardGeometry, true, true)));

        particleEngine.register(BMDParticles.EARTHDIVE_INDICATOR.get(),
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
