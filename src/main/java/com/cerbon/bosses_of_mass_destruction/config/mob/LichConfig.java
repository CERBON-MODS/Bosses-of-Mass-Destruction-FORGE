package com.cerbon.bosses_of_mass_destruction.config.mob;

import me.shedaniel.autoconfig.annotation.ConfigEntry;

import java.util.List;

public class LichConfig {
    public final boolean eternalNighttime = true;

    @ConfigEntry.BoundedDiscrete(min = 0, max = 10000)
    public final int experienceDrop = 1500;
    public final float idleHealingPerTick = 0.2f;
    public final double health = 300.0;

    @ConfigEntry.Gui.CollapsibleObject
    public final Missile missile = new Missile();

    @ConfigEntry.Gui.CollapsibleObject
    public final Comet comet = new Comet();

    @ConfigEntry.Gui.CollapsibleObject
    public final SummonMechanic summonMechanic = new SummonMechanic();

    public static class Missile {
        public String mobEffectId = "minecraft:slowness";

        @ConfigEntry.BoundedDiscrete(min = 0, max = 1000)
        public int mobEffectDuration = 100;

        @ConfigEntry.BoundedDiscrete(min = 0, max = 4)
        public int mobEffectAmplifier = 2;
        public double damage = 9.0;
    }

    public static class Comet {
        public float explosionStrength = 4.0f;
    }

    public static class SummonMechanic {
        public boolean isEnabled = true;

        public List<String> entitiesThatCountToSummonCounter;

        @ConfigEntry.BoundedDiscrete(min = 1, max = 1000)
        public int numEntitiesKilledToDropSoulStar = 50;
    }
}
