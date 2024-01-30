package com.cerbon.bosses_of_mass_destruction.sound;

import com.cerbon.bosses_of_mass_destruction.util.BMDConstants;
import com.cerbon.cerbons_api.api.registry.RegistryEntry;
import com.cerbon.cerbons_api.api.registry.ResourcefulRegistries;
import com.cerbon.cerbons_api.api.registry.ResourcefulRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public class BMDSounds {
    public static final ResourcefulRegistry<SoundEvent> SOUND_EVENTS = ResourcefulRegistries.create(BuiltInRegistries.SOUND_EVENT, BMDConstants.MOD_ID);

    public static final RegistryEntry<SoundEvent> GAUNTLET_HURT = registerSoundEvent("gauntlet_hurt");
    public static final RegistryEntry<SoundEvent> GAUNTLET_DEATH = registerSoundEvent("gauntlet_death");
    public static final RegistryEntry<SoundEvent> GAUNTLET_IDLE = registerSoundEvent("gauntlet_idle");
    public static final RegistryEntry<SoundEvent> GAUNTLET_CAST = registerSoundEvent("gauntlet_cast");
    public static final RegistryEntry<SoundEvent> GAUNTLET_CLINK = registerSoundEvent("gauntlet_clink");
    public static final RegistryEntry<SoundEvent> GAUNTLET_SPIN_PUNCH = registerSoundEvent("gauntlet_spin_punch");
    public static final RegistryEntry<SoundEvent> GAUNTLET_LASER_CHARGE = registerSoundEvent("gauntlet_laser_charge");

    public static final RegistryEntry<SoundEvent> LICH_HURT = registerSoundEvent("lich_hurt");
    public static final RegistryEntry<SoundEvent> LICH_DEATH = registerSoundEvent("lich_death");
    public static final RegistryEntry<SoundEvent> LICH_TELEPORT = registerSoundEvent("lich_teleport");
    public static final RegistryEntry<SoundEvent> BLUE_FIREBALL_LAND = registerSoundEvent("blue_fireball_land");
    public static final RegistryEntry<SoundEvent> COMET_SHOOT = registerSoundEvent("comet_shoot");
    public static final RegistryEntry<SoundEvent> COMET_PREPARE = registerSoundEvent("comet_prepare");
    public static final RegistryEntry<SoundEvent> MISSILE_SHOOT = registerSoundEvent("missile_shoot");
    public static final RegistryEntry<SoundEvent> MISSILE_PREPARE = registerSoundEvent("missile_prepare");
    public static final RegistryEntry<SoundEvent> MINION_RUNE = registerSoundEvent("minion_rune");
    public static final RegistryEntry<SoundEvent> MINION_SUMMON = registerSoundEvent("minion_summon");
    public static final RegistryEntry<SoundEvent> RAGE_PREPARE = registerSoundEvent("rage_prepare");

    public static final RegistryEntry<SoundEvent> OBSIDILITH_HURT = registerSoundEvent("obsidilith_hurt");
    public static final RegistryEntry<SoundEvent> OBSIDILITH_DEATH = registerSoundEvent("obsidilith_death");
    public static final RegistryEntry<SoundEvent> OBSIDILITH_PREPARE_ATTACK = registerSoundEvent("obsidilith_prepare_attack");
    public static final RegistryEntry<SoundEvent> OBSIDILITH_BURST = registerSoundEvent("obsidilith_burst");
    public static final RegistryEntry<SoundEvent> OBSIDILITH_WAVE = registerSoundEvent("obsidilith_wave");
    public static final RegistryEntry<SoundEvent> OBSIDILITH_TELEPORT = registerSoundEvent("obsidilith_teleport");
    public static final RegistryEntry<SoundEvent> TELEPORT_PREPARE = registerSoundEvent("teleport_prepare");
    public static final RegistryEntry<SoundEvent> SPIKE = registerSoundEvent("spike");
    public static final RegistryEntry<SoundEvent> SPIKE_INDICATOR = registerSoundEvent("spike_indicator");
    public static final RegistryEntry<SoundEvent> ENERGY_SHIELD = registerSoundEvent("energy_shield");

    public static final RegistryEntry<SoundEvent> VOID_BLOSSOM_HURT = registerSoundEvent("void_blossom_hurt");
    public static final RegistryEntry<SoundEvent> VOID_BLOSSOM_FALL = registerSoundEvent("void_blossom_fall");
    public static final RegistryEntry<SoundEvent> VOID_BLOSSOM_SPIKE = registerSoundEvent("void_blossom_spike");
    public static final RegistryEntry<SoundEvent> VOID_BLOSSOM_BURROW = registerSoundEvent("void_blossom_burrow");
    public static final RegistryEntry<SoundEvent> SPORE_PREPARE = registerSoundEvent("spore_prepare");
    public static final RegistryEntry<SoundEvent> SPORE_IMPACT = registerSoundEvent("spore_impact");
    public static final RegistryEntry<SoundEvent> SPORE_BALL_LAND = registerSoundEvent("spore_ball_land");
    public static final RegistryEntry<SoundEvent> PETAL_BLADE = registerSoundEvent("petal_blade");
    public static final RegistryEntry<SoundEvent> VOID_SPIKE_INDICATOR = registerSoundEvent("void_spike_indicator");

    public static final RegistryEntry<SoundEvent> SPIKE_WAVE_INDICATOR = registerSoundEvent("spike_wave_indicator");
    public static final RegistryEntry<SoundEvent> WAVE_INDICATOR = registerSoundEvent("wave_indicator");

    public static final RegistryEntry<SoundEvent> SOUL_STAR = registerSoundEvent("soul_star");
    public static final RegistryEntry<SoundEvent> EARTHDIVE_SPEAR_THROW = registerSoundEvent("earthdive_spear_throw");
    public static final RegistryEntry<SoundEvent> CHARGED_ENDER_PEARL = registerSoundEvent("charged_ender_pearl");
    public static final RegistryEntry<SoundEvent> BRIMSTONE = registerSoundEvent("brimstone");

    private static RegistryEntry<SoundEvent> registerSoundEvent(String name) {
        ResourceLocation id = new ResourceLocation(BMDConstants.MOD_ID, name);
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(id));
    }

    public static void register() {
        SOUND_EVENTS.register();
    }
}
