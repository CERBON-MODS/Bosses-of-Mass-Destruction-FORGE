package com.cerbon.bosses_of_mass_destruction.event;

import com.cerbon.bosses_of_mass_destruction.entity.BMDEntities;
import com.cerbon.bosses_of_mass_destruction.item.BMDCreativeModeTabs;
import com.cerbon.bosses_of_mass_destruction.item.BMDItems;
import com.cerbon.bosses_of_mass_destruction.packet.BMDPacketHandler;
import com.cerbon.bosses_of_mass_destruction.particle.BMDParticles;
import com.cerbon.bosses_of_mass_destruction.util.BMDConstants;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
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

        @SubscribeEvent
        public static void entityAttributeEvent(EntityAttributeCreationEvent event){
            event.put(BMDEntities.LICH.get(), Mob.createMobAttributes()
                    .add(Attributes.FLYING_SPEED, 5.0)
                    .add(Attributes.MAX_HEALTH, BMDEntities.mobConfig.lichConfig.health)
                    .add(Attributes.FOLLOW_RANGE, 64)
                    .add(Attributes.ATTACK_DAMAGE, BMDEntities.mobConfig.lichConfig.missile.damage)
                    .build());
        }
    }
}
