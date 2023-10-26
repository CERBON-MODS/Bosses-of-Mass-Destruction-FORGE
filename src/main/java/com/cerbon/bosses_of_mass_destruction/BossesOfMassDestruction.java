package com.cerbon.bosses_of_mass_destruction;

import com.cerbon.bosses_of_mass_destruction.config.BMDConfig;
import com.cerbon.bosses_of_mass_destruction.item.BMDCreativeModeTabs;
import com.cerbon.bosses_of_mass_destruction.item.BMDItems;
import com.cerbon.bosses_of_mass_destruction.particle.BMDParticles;
import com.cerbon.bosses_of_mass_destruction.sound.BMDSounds;
import com.cerbon.bosses_of_mass_destruction.util.BMDConstants;
import com.mojang.logging.LogUtils;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
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

        AutoConfig.register(BMDConfig.class, JanksonConfigSerializer::new);
        AutoConfig.getConfigHolder(BMDConfig.class).getConfig().postInit();
        AutoConfig.getConfigHolder(BMDConfig.class).save();
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
            BMDParticles.initClient(event);
        }
    }
}
