package com.cerbon.bosses_of_mass_destruction;

import com.cerbon.bosses_of_mass_destruction.item.BMDCreativeModeTabs;
import com.cerbon.bosses_of_mass_destruction.item.BMDItems;
import com.cerbon.bosses_of_mass_destruction.particle.BMDParticles;
import com.cerbon.bosses_of_mass_destruction.particle.SimpleParticle;
import com.cerbon.bosses_of_mass_destruction.particle.SimpleParticleProvider;
import com.cerbon.bosses_of_mass_destruction.sound.BMDSounds;
import com.cerbon.bosses_of_mass_destruction.util.BMDColors;
import com.cerbon.bosses_of_mass_destruction.util.BMDConstants;
import com.cerbon.bosses_of_mass_destruction.util.VanillaCopies;
import com.cerbon.bosses_of_mass_destruction.util.maelstrom.static_utilities.MathUtils;
import com.cerbon.bosses_of_mass_destruction.util.maelstrom.static_utilities.RandomUtils;
import com.mojang.logging.LogUtils;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

@Mod(BMDConstants.MOD_ID)
public class BossesOfMassDestruction {
    private static final Logger LOGGER = LogUtils.getLogger();

    public BossesOfMassDestruction() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::addCreativeTab);

        BMDCreativeModeTabs.register(modEventBus);
        BMDItems.register(modEventBus);

        BMDSounds.register(modEventBus);
        BMDParticles.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void addCreativeTab(@NotNull BuildCreativeModeTabContentsEvent event) {
        if (event.getTab() == BMDCreativeModeTabs.BOSSES_OF_MASS_DESTRUCTION.get()) {
            event.accept(BMDItems.ANCIENT_ANIMA);
            event.accept(BMDItems.BLAZING_EYE);
            event.accept(BMDItems.OBSIDIAN_HEART);
            event.accept(BMDItems.VOID_THORN);
            event.accept(BMDItems.CRYSTAL_FRUIT);
        }
    }

    @Mod.EventBusSubscriber(modid = BMDConstants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientEvents {

        @SubscribeEvent
        protected static void registerParticleProviders(final @NotNull RegisterParticleProvidersEvent event) {
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
    }
}
