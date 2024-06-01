package com.cerbon.bosses_of_mass_destruction;

import com.cerbon.bosses_of_mass_destruction.block.BMDBlockEntities;
import com.cerbon.bosses_of_mass_destruction.block.BMDBlocks;
import com.cerbon.bosses_of_mass_destruction.config.BMDConfig;
import com.cerbon.bosses_of_mass_destruction.entity.BMDEntities;
import com.cerbon.bosses_of_mass_destruction.item.BMDItems;
import com.cerbon.bosses_of_mass_destruction.particle.BMDParticles;
import com.cerbon.bosses_of_mass_destruction.sound.BMDSounds;
import com.cerbon.bosses_of_mass_destruction.structure.BMDStructures;
import com.cerbon.bosses_of_mass_destruction.util.BMDConstants;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(BMDConstants.MOD_ID)
public class BossesOfMassDestruction {
    public static final Logger LOGGER = LoggerFactory.getLogger(BMDConstants.MOD_ID);

    public BossesOfMassDestruction() {
        AutoConfig.register(BMDConfig.class, JanksonConfigSerializer::new);
        AutoConfig.getConfigHolder(BMDConfig.class).getConfig().postInit();
        AutoConfig.getConfigHolder(BMDConfig.class).save();

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        BMDBlocks.register(modEventBus);
        BMDItems.register(modEventBus);

        BMDEntities.register(modEventBus);
        BMDBlockEntities.register(modEventBus);

        BMDSounds.register(modEventBus);
        BMDParticles.register(modEventBus);
        BMDStructures.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);
    }
}
