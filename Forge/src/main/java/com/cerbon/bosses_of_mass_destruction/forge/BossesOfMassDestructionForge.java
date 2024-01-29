package com.cerbon.bosses_of_mass_destruction.forge;

import com.cerbon.bosses_of_mass_destruction.BossesOfMassDestruction;
import com.cerbon.bosses_of_mass_destruction.util.BMDConstants;
import net.minecraftforge.fml.common.Mod;

@Mod(BMDConstants.MOD_ID)
public class BossesOfMassDestructionForge {

    public BossesOfMassDestructionForge() {
        BossesOfMassDestruction.init();
    }
}