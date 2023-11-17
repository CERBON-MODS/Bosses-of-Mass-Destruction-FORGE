package com.cerbon.bosses_of_mass_destruction.event;

import com.cerbon.bosses_of_mass_destruction.block.BMDBlockEntities;
import com.cerbon.bosses_of_mass_destruction.block.BMDBlocks;
import com.cerbon.bosses_of_mass_destruction.entity.BMDEntities;
import com.cerbon.bosses_of_mass_destruction.item.BMDCreativeModeTabs;
import com.cerbon.bosses_of_mass_destruction.item.BMDItems;
import com.cerbon.bosses_of_mass_destruction.packet.BMDPacketHandler;
import com.cerbon.bosses_of_mass_destruction.particle.BMDParticles;
import com.cerbon.bosses_of_mass_destruction.util.BMDConstants;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.jetbrains.annotations.NotNull;

public class BMDEvents {

    @Mod.EventBusSubscriber(modid = BMDConstants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientEvents {

        @SubscribeEvent
        protected static void onClientSetup(FMLClientSetupEvent event){
            BMDEntities.initClient();
            BMDItems.initClient();
            BMDBlockEntities.initClient();
        }

        @SubscribeEvent
        protected static void registerParticleProviders(final @NotNull RegisterParticleProvidersEvent event) {
            BMDParticles.initClient(event);
        }
    }

    @Mod.EventBusSubscriber(modid = BMDConstants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class CommonEvents {

        @SubscribeEvent
        protected static void onCommonSetup(FMLCommonSetupEvent event){
            event.enqueueWork(BMDPacketHandler::register);
        }

        @SubscribeEvent
        public static void entityAttributeEvent(EntityAttributeCreationEvent event){
            BMDEntities.createAttributes(event);
        }

        @SubscribeEvent
        protected static void addCreativeTab(@NotNull BuildCreativeModeTabContentsEvent event) {
            if (event.getTab() == BMDCreativeModeTabs.BOSSES_OF_MASS_DESTRUCTION.get()) {
                event.accept(BMDItems.MOB_WARD);
                event.accept(BMDBlocks.MONOLITH_BLOCK);
                event.accept(BMDBlocks.LEVITATION_BLOCK);
                event.accept(BMDBlocks.VOID_LILY_BLOCK);
                event.accept(BMDItems.SOUL_STAR);
                event.accept(BMDItems.ANCIENT_ANIMA);
                event.accept(BMDItems.BLAZING_EYE);
                event.accept(BMDItems.OBSIDIAN_HEART);
                event.accept(BMDItems.EARTHDIVE_SPEAR);
                event.accept(BMDItems.VOID_THORN);
                event.accept(BMDItems.CRYSTAL_FRUIT);
                event.accept(BMDItems.CHARGED_ENDER_PEARL);
            }
        }
    }
}
