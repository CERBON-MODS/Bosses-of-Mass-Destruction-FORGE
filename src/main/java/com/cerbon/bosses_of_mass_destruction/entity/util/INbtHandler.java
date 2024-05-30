package com.cerbon.bosses_of_mass_destruction.entity.util;

import net.minecraft.nbt.CompoundNBT;

public interface INbtHandler {
    CompoundNBT toTag(CompoundNBT tag);
    void fromTag(CompoundNBT tag);
}
