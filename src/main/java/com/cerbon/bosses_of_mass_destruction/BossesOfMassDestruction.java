package com.cerbon.bosses_of_mass_destruction;

import com.cerbon.bosses_of_mass_destruction.attachment.BMDAttachments;
import com.cerbon.bosses_of_mass_destruction.block.BMDBlockEntities;
import com.cerbon.bosses_of_mass_destruction.block.BMDBlocks;
import com.cerbon.bosses_of_mass_destruction.config.BMDConfig;
import com.cerbon.bosses_of_mass_destruction.entity.BMDEntities;
import com.cerbon.bosses_of_mass_destruction.item.BMDCreativeModeTabs;
import com.cerbon.bosses_of_mass_destruction.item.BMDItems;
import com.cerbon.bosses_of_mass_destruction.particle.BMDParticles;
import com.cerbon.bosses_of_mass_destruction.sound.BMDSounds;
import com.cerbon.bosses_of_mass_destruction.structure.BMDStructures;
import com.cerbon.bosses_of_mass_destruction.util.BMDConstants;
import com.mojang.logging.LogUtils;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

@Mod(BMDConstants.MOD_ID)
public class BossesOfMassDestruction {
    public static final Logger LOGGER = LogUtils.getLogger();

    public BossesOfMassDestruction(IEventBus modEventBus) {
        AutoConfig.register(BMDConfig.class, JanksonConfigSerializer::new);
        AutoConfig.getConfigHolder(BMDConfig.class).getConfig().postInit();
        AutoConfig.getConfigHolder(BMDConfig.class).save();

        BMDCreativeModeTabs.register(modEventBus);
        BMDBlocks.register(modEventBus);
        BMDItems.register(modEventBus);

        BMDEntities.register(modEventBus);
        BMDBlockEntities.register(modEventBus);
        BMDAttachments.register(modEventBus);

        BMDSounds.register(modEventBus);
        BMDParticles.register(modEventBus);
        BMDStructures.register(modEventBus);

        //NeoForge.EVENT_BUS.register(this);
    }
}
