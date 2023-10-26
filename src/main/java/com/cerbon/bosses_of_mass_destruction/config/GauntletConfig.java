package com.cerbon.bosses_of_mass_destruction.config;

import me.shedaniel.autoconfig.annotation.ConfigEntry;

public class GauntletConfig {
    public final double health = 250.0;
    public final double armor = 8.0;
    public final double attack = 16.0;
    public final double idleHealingPerTick = 0.5f;

    @ConfigEntry.BoundedDiscrete(min = 0, max = 10000)
    public final int experienceDrop = 1000;
    public final boolean spawnAncientDebrisOnDeath = true;
    public final double energizedPunchExplosionSize = 4.5;
    public final double normalPunchExplosionMultiplier = 1.5;
}
