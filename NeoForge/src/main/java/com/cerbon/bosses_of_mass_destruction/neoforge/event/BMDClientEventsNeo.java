package com.cerbon.bosses_of_mass_destruction.neoforge.event;

import com.cerbon.bosses_of_mass_destruction.block.BMDBlockEntities;
import com.cerbon.bosses_of_mass_destruction.config.BMDConfig;
import com.cerbon.bosses_of_mass_destruction.entity.BMDEntities;
import com.cerbon.bosses_of_mass_destruction.entity.custom.obsidilith.BurstAction;
import com.cerbon.bosses_of_mass_destruction.entity.custom.obsidilith.PillarAction;
import com.cerbon.bosses_of_mass_destruction.entity.custom.obsidilith.WaveAction;
import com.cerbon.bosses_of_mass_destruction.entity.custom.void_blossom.SpikeAction;
import com.cerbon.bosses_of_mass_destruction.entity.custom.void_blossom.SpikeWaveAction;
import com.cerbon.bosses_of_mass_destruction.item.BMDItems;
import com.cerbon.bosses_of_mass_destruction.particle.BMDParticles;
import com.cerbon.bosses_of_mass_destruction.projectile.SporeBallProjectile;
import com.cerbon.bosses_of_mass_destruction.util.BMDConstants;
import com.cerbon.cerbons_api.api.general.particle.SimpleParticle;
import com.cerbon.cerbons_api.api.general.particle.SimpleParticleProvider;
import com.cerbon.cerbons_api.api.static_utilities.Geometries;
import com.cerbon.cerbons_api.api.static_utilities.MathUtils;
import com.cerbon.cerbons_api.api.static_utilities.RandomUtils;
import com.cerbon.cerbons_api.api.static_utilities.Vec3Colors;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@EventBusSubscriber(modid = BMDConstants.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class BMDClientEventsNeo {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        BMDItems.initClient();
        BMDEntities.initClient();
        BMDBlockEntities.initClient();
        ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class, () -> (modContainer, screen) -> AutoConfig.getConfigScreen(BMDConfig.class, screen).get());
    }

    @SubscribeEvent
    public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(BMDParticles.DISAPPEARING_SWIRL.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> new SimpleParticle(particleContext, RandomUtils.range(15,20), Geometries::buildBillBoardGeometry, true, true)));

        event.registerSpriteSet(BMDParticles.SOUL_FLAME.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> {
                            SimpleParticle particle = new SimpleParticle(particleContext, RandomUtils.range(15, 20), Geometries::buildBillBoardGeometry, true, true);
                            particle.setColorOverride(f -> Vec3Colors.COMET_BLUE);
                            particle.setBrightnessOverride(f -> BMDParticles.FULL_BRIGHT);
                            return particle;
                        }));

        event.registerSpriteSet(BMDParticles.MAGIC_CIRCLE.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> {
                            SimpleParticle particle = new SimpleParticle(particleContext, 40, Geometries::buildBillBoardGeometry, true, true);
                            particle.setBrightnessOverride(f -> BMDParticles.FULL_BRIGHT);
                            particle.scale(4f);
                            return particle;
                        }));

        event.registerSpriteSet(BMDParticles.OBSIDILITH_BURST.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> {
                            SimpleParticle particle = new SimpleParticle(particleContext, RandomUtils.range(7, 15), Geometries::buildBillBoardGeometry, true, true);
                            particle.setBrightnessOverride(f -> BMDParticles.FULL_BRIGHT);
                            particle.scale(4f);
                            particle.setColorOverride(age -> MathUtils.lerpVec(age, Vec3Colors.ORANGE, Vec3Colors.RUNIC_BROWN));
                            return particle;
                        }));

        event.registerSpriteSet(BMDParticles.ENCHANT.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> {
                            SimpleParticle particle = new SimpleParticle(particleContext, RandomUtils.range(30, 50), Geometries::buildBillBoardGeometry, false, true);
                            particle.setBrightnessOverride(f -> BMDParticles.FULL_BRIGHT);
                            particle.setScaleOverride(f -> (float) ((Math.sin((double) f * Math.PI) + 1f) * 0.1f));
                            return particle;
                        }));

        event.registerSpriteSet(BMDParticles.OBSIDILITH_BURST_INDICATOR.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> {
                            SimpleParticle particle = new SimpleParticle(particleContext, BurstAction.burstDelay + RandomUtils.range(-1, 2), Geometries::buildFlatGeometry, true, true);
                            particle.setColorOverride(f -> Vec3Colors.ORANGE);
                            particle.setColorVariation(0.3);
                            particle.setBrightnessOverride(f -> BMDParticles.FULL_BRIGHT);
                            particle.setScaleOverride(f -> (1 + f) * 0.25f);
                            return particle;
                        }));

        event.registerSpriteSet(BMDParticles.OBSIDILITH_WAVE.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> {
                            SimpleParticle particle = new SimpleParticle(particleContext, RandomUtils.range(7, 15), Geometries::buildBillBoardGeometry, true, true);
                            particle.setBrightnessOverride(f -> BMDParticles.FULL_BRIGHT);
                            particle.scale(4f);
                            particle.setColorVariation(0.25);
                            particle.setColorOverride(age -> MathUtils.lerpVec(age, Vec3Colors.RED, Vec3Colors.DARK_RED));
                            return particle;
                        }));

        event.registerSpriteSet(BMDParticles.OBSIDILITH_WAVE_INDICATOR.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> {
                            SimpleParticle particle = new SimpleParticle(particleContext, WaveAction.waveDelay + RandomUtils.range(-1, 2), Geometries::buildFlatGeometry, true, true);
                            particle.setColorOverride(f -> Vec3Colors.RED);
                            particle.setColorVariation(0.3);
                            particle.setBrightnessOverride(f -> BMDParticles.FULL_BRIGHT);
                            particle.setScaleOverride(f -> (1 + f) * 0.25f);
                            return particle;
                        }));

        event.registerSpriteSet(BMDParticles.DOWNSPARKLE.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> new SimpleParticle(particleContext, RandomUtils.range(15, 20), Geometries::buildBillBoardGeometry, true, true)));

        event.registerSpriteSet(BMDParticles.OBSIDILITH_SPIKE_INDICATOR.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> {
                            SimpleParticle particle = new SimpleParticle(particleContext, WaveAction.waveDelay +  RandomUtils.range(-1, 2), Geometries::buildFlatGeometry, true, true);
                            particle.setColorOverride(f -> Vec3Colors.COMET_BLUE);
                            particle.setColorVariation(0.3);
                            particle.setBrightnessOverride(f -> BMDParticles.FULL_BRIGHT);
                            particle.setScaleOverride(f -> (1 + f) * 0.25f);
                            return particle;
                        }));

        event.registerSpriteSet(BMDParticles.OBSIDILITH_SPIKE.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> {
                            SimpleParticle particle = new SimpleParticle(particleContext, RandomUtils.range(15, 20), Geometries::buildBillBoardGeometry, true, true);
                            particle.setColorOverride(age -> MathUtils.lerpVec(age, Vec3Colors.WHITE, Vec3Colors.COMET_BLUE));
                            particle.setBrightnessOverride(f -> BMDParticles.FULL_BRIGHT);
                            particle.setColorVariation(0.25);
                            return particle;
                        }));

        event.registerSpriteSet(BMDParticles.PILLAR_RUNE.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> {
                            SimpleParticle particle = new SimpleParticle(particleContext, 10, Geometries::buildBillBoardGeometry, false, true);
                            particle.setBrightnessOverride(f -> BMDParticles.FULL_BRIGHT);
                            particle.setScaleOverride(f -> (float) ((Math.sin((double) f * Math.PI) + 1f) * 0.1f));
                            particle.setColorOverride(age -> MathUtils.lerpVec(age, Vec3Colors.WHITE, Vec3Colors.ENDER_PURPLE));
                            particle.setColorVariation(0.2);
                            return particle;
                        }));

        event.registerSpriteSet(BMDParticles.PILLAR_SPAWN_INDICATOR.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> {
                            SimpleParticle particle = new SimpleParticle(particleContext, PillarAction.pillarDelay, Geometries::buildBillBoardGeometry, true, true);
                            particle.setColorOverride(f -> Vec3Colors.ENDER_PURPLE);
                            particle.scale(2.0f);
                            particle.setColorVariation(0.25);
                            particle.setBrightnessOverride(f -> BMDParticles.FULL_BRIGHT);
                            return particle;
                        }));

        event.registerSpriteSet(BMDParticles.PILLAR_SPAWN_INDICATOR_2.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> {
                            SimpleParticle particle = new SimpleParticle(particleContext, PillarAction.pillarDelay, Geometries::buildBillBoardGeometry, false, true);
                            particle.setBrightnessOverride(f -> BMDParticles.FULL_BRIGHT);
                            particle.setScaleOverride(f -> (float) ((Math.sin((double) f * Math.PI) + 1f) * 0.1f));
                            particle.setColorOverride(age -> MathUtils.lerpVec(age, Vec3Colors.WHITE, Vec3Colors.ENDER_PURPLE));
                            particle.setColorVariation(0.2);
                            return particle;
                        }));

        event.registerSpriteSet(BMDParticles.OBSIDILITH_ANVIL_INDICATOR.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> {
                            SimpleParticle particle = new SimpleParticle(particleContext, RandomUtils.range(25, 27), Geometries::buildFlatGeometry, true, true);
                            particle.setColorOverride(f -> Vec3Colors.ENDER_PURPLE);
                            particle.setColorVariation(0.3);
                            particle.setBrightnessOverride(f -> BMDParticles.FULL_BRIGHT);
                            particle.setScaleOverride(f -> (1 + f) * 0.25f);
                            return particle;
                        }));

        event.registerSpriteSet(BMDParticles.SPARKLES.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> new SimpleParticle(particleContext, RandomUtils.range(15, 20), Geometries::buildBillBoardGeometry, true, true)));

        event.registerSpriteSet(BMDParticles.GAUNTLET_REVIVE_SPARKLES.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> {
                            SimpleParticle particle = new SimpleParticle(particleContext, RandomUtils.range(15, 20), Geometries::buildBillBoardGeometry, true, true);
                            particle.setColorOverride(f -> Vec3Colors.LASER_RED);
                            particle.setColorVariation(0.25);
                            particle.setBrightnessOverride(f -> BMDParticles.FULL_BRIGHT);
                            return particle;
                        }));

        event.registerSpriteSet(BMDParticles.EYE_OPEN.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> new SimpleParticle(particleContext, RandomUtils.range(60, 70), Geometries::buildBillBoardGeometry, true, true)));

        event.registerSpriteSet(BMDParticles.LINE.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> new SimpleParticle(particleContext, RandomUtils.range(20, 30), Geometries::buildBillBoardGeometry, true, true)));

        event.registerSpriteSet(BMDParticles.ROD.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> new SimpleParticle(particleContext, RandomUtils.range(8, 10), Geometries::buildBillBoardGeometry, true, true)));

        event.registerSpriteSet(BMDParticles.GROUND_ROD.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> new SimpleParticle(particleContext, RandomUtils.range(8, 10), Geometries::buildFlatGeometry, true, true)));

        event.registerSpriteSet(BMDParticles.VOID_BLOSSOM_SPIKE_INDICATOR.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> {
                            SimpleParticle particle = new SimpleParticle(particleContext, SpikeAction.indicatorDelay + RandomUtils.range(-1, 2), Geometries::buildFlatGeometry, true, true);
                            particle.setColorOverride(f -> Vec3Colors.ENDER_PURPLE);
                            particle.setColorVariation(0.2);
                            particle.setBrightnessOverride(f -> BMDParticles.FULL_BRIGHT);
                            particle.setScaleOverride(f -> (1 + f) * 0.25f);
                            return particle;
                        }));

        event.registerSpriteSet(BMDParticles.VOID_BLOSSOM_SPIKE_WAVE_INDICATOR.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> {
                            SimpleParticle particle = new SimpleParticle(particleContext, SpikeWaveAction.indicatorDelay + RandomUtils.range(-1, 2), Geometries::buildFlatGeometry, true, true);
                            particle.setColorOverride(f -> Vec3Colors.ENDER_PURPLE);
                            particle.setColorVariation(0.2);
                            particle.setBrightnessOverride(f -> BMDParticles.FULL_BRIGHT);
                            particle.setScaleOverride(f -> (1 + f) * 0.25f);
                            return particle;
                        }));

        event.registerSpriteSet(BMDParticles.PETAL.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> new SimpleParticle(particleContext, RandomUtils.range(15, 20), Geometries::buildBillBoardGeometry, false, true)));

        event.registerSpriteSet(BMDParticles.POLLEN.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> {
                            SimpleParticle particle = new SimpleParticle(particleContext, RandomUtils.range(15, 20), Geometries::buildBillBoardGeometry, false, true);
                            particle.setColorOverride(f -> new Vec3(1.0, 0.9, 0.4));
                            particle.setColorVariation(0.15);
                            particle.setBrightnessOverride(f -> BMDParticles.FULL_BRIGHT);
                            particle.setScaleOverride(f -> 0.05f * (1 - f * 0.25f));
                            int randomRot =RandomUtils.range(0, 360);
                            float angularMomentum = RandomUtils.randSign() * 4f;
                            particle.setRotationOverride(particle1 -> randomRot + particle1.getAge() * angularMomentum);
                            return particle;
                        }));

        event.registerSpriteSet(BMDParticles.SPORE.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> {
                            SimpleParticle particle = new SimpleParticle(particleContext, RandomUtils.range(7, 15), Geometries::buildBillBoardGeometry, true, true);
                            particle.setBrightnessOverride(f -> BMDParticles.FULL_BRIGHT);
                            particle.scale(4f);
                            particle.setColorOverride(age -> MathUtils.lerpVec(age, Vec3Colors.GREEN, Vec3Colors.DARK_GREEN));
                            particle.setColorVariation(0.25);
                            return particle;
                        }));

        event.registerSpriteSet(BMDParticles.SPORE_INDICATOR.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> {
                            SimpleParticle particle = new SimpleParticle(particleContext, SporeBallProjectile.explosionDelay + RandomUtils.range(-1, 2), Geometries::buildFlatGeometry, true, true);
                            particle.setColorOverride(f -> Vec3Colors.GREEN);
                            particle.setColorVariation(0.35);
                            particle.setBrightnessOverride(f -> BMDParticles.FULL_BRIGHT);
                            particle.setScaleOverride(f -> (1 + f) * 0.25f);
                            return particle;
                        }));

        event.registerSpriteSet(BMDParticles.FLUFF.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> new SimpleParticle(particleContext, RandomUtils.range(15, 20), Geometries::buildBillBoardGeometry, true, true)));

        event.registerSpriteSet(BMDParticles.EARTHDIVE_INDICATOR.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> {
                            SimpleParticle particle = new SimpleParticle(particleContext, RandomUtils.range(15, 20), Geometries::buildBillBoardGeometry, true, false);
                            particle.setColorOverride(f -> Vec3Colors.RUNIC_BROWN);
                            particle.setColorVariation(0.25);
                            particle.setBrightnessOverride(f -> BMDParticles.FULL_BRIGHT);
                            particle.setScaleOverride(f -> (1 - (f * 0.25f)) * 0.35f);
                            return particle;
                        }
                )
        );
    }
}
