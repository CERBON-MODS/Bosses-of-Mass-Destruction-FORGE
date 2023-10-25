package com.cerbon.bosses_of_mass_destruction;

import com.cerbon.bosses_of_mass_destruction.item.BMDItems;
import com.cerbon.bosses_of_mass_destruction.util.BMDConstants;
import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(BMDConstants.MOD_ID)
public class BossesOfMassDestruction {
    private static final Logger LOGGER = LogUtils.getLogger();

    public BossesOfMassDestruction() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        BMDItems.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);
    }
}
