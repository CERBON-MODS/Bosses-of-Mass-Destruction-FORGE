package com.cerbon.bosses_of_mass_destruction.config.mob;

import me.shedaniel.autoconfig.annotation.ConfigEntry;

public class ObsidilithConfig {
    public final double health = 300.0;
    public final double armor = 14.0;
    public final double attack = 16.0;
    public final float idleHealingPerTick = 0.5f;

    @ConfigEntry.BoundedDiscrete(min = 0, max = 10000)
    public final int experienceDrop = 1000;
    public final boolean spawnPillarOnDeath = true;
    public final float anvilAttackExplosionStrength = 4.0f;
}
