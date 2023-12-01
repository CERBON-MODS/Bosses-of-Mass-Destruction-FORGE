package com.cerbon.bosses_of_mass_destruction.config;

import me.shedaniel.autoconfig.annotation.ConfigEntry;

public class GeneralConfig {
    @ConfigEntry.BoundedDiscrete(min = 1, max = 32)
    public int tableOfElevationRadius = 3;
}
