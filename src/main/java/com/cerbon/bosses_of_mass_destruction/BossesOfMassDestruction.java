package com.cerbon.bosses_of_mass_destruction;

import com.cerbon.bosses_of_mass_destruction.capability.PlayerMoveHistoryProvider;
import com.cerbon.bosses_of_mass_destruction.config.BMDConfig;
import com.cerbon.bosses_of_mass_destruction.entity.BMDEntities;
import com.cerbon.bosses_of_mass_destruction.item.BMDCreativeModeTabs;
import com.cerbon.bosses_of_mass_destruction.item.BMDItems;
import com.cerbon.bosses_of_mass_destruction.packet.BMDPacketHandler;
import com.cerbon.bosses_of_mass_destruction.particle.BMDParticles;
import com.cerbon.bosses_of_mass_destruction.sound.BMDSounds;
import com.cerbon.bosses_of_mass_destruction.util.BMDConstants;
import com.mojang.logging.LogUtils;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.List;

@Mod(BMDConstants.MOD_ID)
public class BossesOfMassDestruction {
    private static final Logger LOGGER = LogUtils.getLogger();

    public BossesOfMassDestruction() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        BMDCreativeModeTabs.register(modEventBus);
        BMDItems.register(modEventBus);
        BMDEntities.register(modEventBus);

        BMDSounds.register(modEventBus);
        BMDParticles.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);

        AutoConfig.register(BMDConfig.class, JanksonConfigSerializer::new);
        AutoConfig.getConfigHolder(BMDConfig.class).getConfig().postInit();
        AutoConfig.getConfigHolder(BMDConfig.class).save();
    }


    @Mod.EventBusSubscriber(modid = BMDConstants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class CommonEvents {

        @SubscribeEvent
        protected static void onCommonSetup(FMLCommonSetupEvent event){
            event.enqueueWork(BMDPacketHandler::register);
        }

        @SubscribeEvent
        protected static void addCreativeTab(@NotNull BuildCreativeModeTabContentsEvent event) {
            if (event.getTab() == BMDCreativeModeTabs.BOSSES_OF_MASS_DESTRUCTION.get()) {
                event.accept(BMDItems.ANCIENT_ANIMA);
                event.accept(BMDItems.BLAZING_EYE);
                event.accept(BMDItems.OBSIDIAN_HEART);
                event.accept(BMDItems.EARTHDIVE_SPEAR.get());
                event.accept(BMDItems.VOID_THORN);
                event.accept(BMDItems.CRYSTAL_FRUIT);
                event.accept(BMDItems.CHARGED_ENDER_PEARL);
            }
        }


    }

    @Mod.EventBusSubscriber(modid = BMDConstants.MOD_ID)
    public static class ForgeEvents {

        @SubscribeEvent
        public static void onAttachCapabilitiesPlayer(AttachCapabilitiesEvent<Entity> event) {
            if(event.getObject() instanceof Player player)
                if(!player.getCapability(PlayerMoveHistoryProvider.HISTORICAL_DATA).isPresent())
                    event.addCapability(new ResourceLocation(BMDConstants.MOD_ID, "player_move_history"), new PlayerMoveHistoryProvider());
        }

        @SubscribeEvent
        protected static void onPlayerTick(TickEvent.PlayerTickEvent event) {
            if(event.side == LogicalSide.SERVER) {
                event.player.getCapability(PlayerMoveHistoryProvider.HISTORICAL_DATA).ifPresent(data -> {
                    Vec3 previousPosition = data.get(0);
                    Vec3 newPosition = event.player.position();

                    // Extremely fast movement in one tick is a sign of teleportation or dimension hopping, and thus we should clear history to avoid undefined behavior
                    if (previousPosition.distanceToSqr(newPosition) > 5)
                        data.clear();

                    data.set(newPosition);
                });
            }
        }
    }

    @Mod.EventBusSubscriber(modid = BMDConstants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientEvents {

        @SubscribeEvent
        protected static void onClientSetup(FMLClientSetupEvent event){
            BMDEntities.initClient();
            BMDItems.initClient();
        }

        @SubscribeEvent
        protected static void registerParticleProviders(final @NotNull RegisterParticleProvidersEvent event) {
            BMDParticles.initClient(event);
        }
    }
}
