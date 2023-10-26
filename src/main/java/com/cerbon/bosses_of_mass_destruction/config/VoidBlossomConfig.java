package com.cerbon.bosses_of_mass_destruction.config;

import me.shedaniel.autoconfig.annotation.ConfigEntry;

public class VoidBlossomConfig {
    public final double health = 350.0;
    public final double armor = 4.0;
    public final double attack = 12.0;
    public final float idleHealingPerTick = 0.5f;

    @ConfigEntry.BoundedDiscrete(min = 0, max = 10000)
    public final int experienceDrop = 1000;
}
