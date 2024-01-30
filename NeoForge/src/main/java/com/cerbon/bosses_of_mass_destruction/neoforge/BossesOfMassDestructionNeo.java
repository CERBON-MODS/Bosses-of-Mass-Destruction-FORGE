package com.cerbon.bosses_of_mass_destruction.neoforge;

import com.cerbon.bosses_of_mass_destruction.BossesOfMassDestruction;
import com.cerbon.bosses_of_mass_destruction.neoforge.attachment.BMDAttachments;
import com.cerbon.bosses_of_mass_destruction.util.BMDConstants;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(BMDConstants.MOD_ID)
public class BossesOfMassDestructionNeo {

    public BossesOfMassDestructionNeo(IEventBus modEventBus) {
        BossesOfMassDestruction.init();

        BMDAttachments.register(modEventBus);
    }
}
