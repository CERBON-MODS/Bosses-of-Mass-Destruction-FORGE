package com.cerbon.bosses_of_mass_destruction.config.mob;

import me.shedaniel.autoconfig.annotation.ConfigEntry;

public class VoidBlossomConfig {
    public double health = 350.0;
    public double armor = 4.0;
    public double attack = 12.0;
    public float idleHealingPerTick = 0.5f;

    @ConfigEntry.BoundedDiscrete(min = 0, max = 10000)
    public int experienceDrop = 1000;
}
