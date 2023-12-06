package com.cerbon.bosses_of_mass_destruction.config.mob;

import me.shedaniel.autoconfig.annotation.ConfigEntry;

public class ObsidilithConfig {
    public double health = 300.0;
    public double armor = 14.0;
    public double attack = 16.0;
    public float idleHealingPerTick = 0.5f;

    @ConfigEntry.BoundedDiscrete(min = 0, max = 10000)
    public int experienceDrop = 1000;
    public boolean spawnPillarOnDeath = true;
    public float anvilAttackExplosionStrength = 4.0f;

    @ConfigEntry.Gui.CollapsibleObject
    public ArenaGeneration arenaGeneration = new ArenaGeneration();

    public static class ArenaGeneration {
        @ConfigEntry.BoundedDiscrete(min = 1, max = 150)
        public int generationHeight = 90;
    }
}
