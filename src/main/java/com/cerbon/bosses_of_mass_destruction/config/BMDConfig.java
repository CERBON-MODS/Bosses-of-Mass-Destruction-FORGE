package com.cerbon.bosses_of_mass_destruction.config;

import com.cerbon.bosses_of_mass_destruction.config.mob.GauntletConfig;
import com.cerbon.bosses_of_mass_destruction.config.mob.LichConfig;
import com.cerbon.bosses_of_mass_destruction.config.mob.ObsidilithConfig;
import com.cerbon.bosses_of_mass_destruction.config.mob.VoidBlossomConfig;
import com.cerbon.bosses_of_mass_destruction.util.BMDConstants;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@Config(name = BMDConstants.MOD_ID)
public class BMDConfig implements ConfigData {
    @ConfigEntry.Category("Lich")
    @ConfigEntry.Gui.TransitiveObject
    public LichConfig lichConfig = new LichConfig();

    @ConfigEntry.Category("Obsidilith")
    @ConfigEntry.Gui.TransitiveObject
    public ObsidilithConfig obsidilithConfig = new ObsidilithConfig();

    @ConfigEntry.Category("Gauntlet")
    @ConfigEntry.Gui.TransitiveObject
    public GauntletConfig gauntletConfig = new GauntletConfig();

    @ConfigEntry.Category("VoidBlossom")
    @ConfigEntry.Gui.TransitiveObject
    public VoidBlossomConfig voidBlossomConfig = new VoidBlossomConfig();

    public void postInit() {
        List<String> entitiesThatCountToSummonCounter = lichConfig.summonMechanic.entitiesThatCountToSummonCounter;
        if (entitiesThatCountToSummonCounter == null) {
            List<String> defaultEntities = Arrays.asList(
                    "minecraft:zombie",
                    "minecraft:skeleton",
                    "minecraft:drowned",
                    "minecraft:giant",
                    "minecraft:husk",
                    "minecraft:phantom",
                    "minecraft:skeleton_horse",
                    "minecraft:stray",
                    "minecraft:wither",
                    "minecraft:wither_skeleton",
                    "minecraft:zoglin",
                    "minecraft:zombie_horse",
                    "minecraft:zombie_villager",
                    "minecraft:zombified_piglin"
            );
            lichConfig.summonMechanic.entitiesThatCountToSummonCounter = new ArrayList<>(defaultEntities);
        } else {
            lichConfig.summonMechanic.entitiesThatCountToSummonCounter =
                    new ArrayList<>(new HashSet<>(entitiesThatCountToSummonCounter));
        }
    }
}
