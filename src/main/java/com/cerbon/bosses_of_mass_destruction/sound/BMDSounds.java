package com.cerbon.bosses_of_mass_destruction.sound;

import com.cerbon.bosses_of_mass_destruction.util.BMDConstants;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BMDSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, BMDConstants.MOD_ID);

    public static final RegistryObject<SoundEvent> GAUNTLET_HURT = registerSoundEvent("gauntlet_hurt");
    public static final RegistryObject<SoundEvent> GAUNTLET_DEATH = registerSoundEvent("gauntlet_death");
    public static final RegistryObject<SoundEvent> GAUNTLET_IDLE = registerSoundEvent("gauntlet_idle");
    public static final RegistryObject<SoundEvent> GAUNTLET_CAST = registerSoundEvent("gauntlet_cast");
    public static final RegistryObject<SoundEvent> GAUNTLET_CLINK = registerSoundEvent("gauntlet_clink");
    public static final RegistryObject<SoundEvent> GAUNTLET_SPIN_PUNCH = registerSoundEvent("gauntlet_spin_punch");
    public static final RegistryObject<SoundEvent> GAUNTLET_LASER_CHARGE = registerSoundEvent("gauntlet_laser_charge");

    public static final RegistryObject<SoundEvent> LICH_HURT = registerSoundEvent("lich_hurt");
    public static final RegistryObject<SoundEvent> LICH_DEATH = registerSoundEvent("lich_death");
    public static final RegistryObject<SoundEvent> LICH_TELEPORT = registerSoundEvent("lich_teleport");
    public static final RegistryObject<SoundEvent> BLUE_FIREBALL_LAND = registerSoundEvent("blue_fireball_land");
    public static final RegistryObject<SoundEvent> COMET_SHOOT = registerSoundEvent("comet_shoot");
    public static final RegistryObject<SoundEvent> COMET_PREPARE = registerSoundEvent("comet_prepare");
    public static final RegistryObject<SoundEvent> MISSILE_SHOOT = registerSoundEvent("missile_shoot");
    public static final RegistryObject<SoundEvent> MISSILE_PREPARE = registerSoundEvent("missile_prepare");
    public static final RegistryObject<SoundEvent> MINION_RUNE = registerSoundEvent("minion_rune");
    public static final RegistryObject<SoundEvent> MINION_SUMMON = registerSoundEvent("minion_summon");
    public static final RegistryObject<SoundEvent> RAGE_PREPARE = registerSoundEvent("rage_prepare");

    public static final RegistryObject<SoundEvent> OBSIDILITH_HURT = registerSoundEvent("obsidilith_hurt");
    public static final RegistryObject<SoundEvent> OBSIDILITH_DEATH = registerSoundEvent("obsidilith_death");
    public static final RegistryObject<SoundEvent> OBSIDILITH_PREPARE_ATTACK = registerSoundEvent("obsidilith_prepare_attack");
    public static final RegistryObject<SoundEvent> OBSIDILITH_BURST = registerSoundEvent("obsidilith_burst");
    public static final RegistryObject<SoundEvent> OBSIDILITH_WAVE = registerSoundEvent("obsidilith_wave");
    public static final RegistryObject<SoundEvent> OBSIDILITH_TELEPORT = registerSoundEvent("obsidilith_teleport");
    public static final RegistryObject<SoundEvent> TELEPORT_PREPARE = registerSoundEvent("teleport_prepare");
    public static final RegistryObject<SoundEvent> SPIKE = registerSoundEvent("spike");
    public static final RegistryObject<SoundEvent> SPIKE_INDICATOR = registerSoundEvent("spike_indicator");
    public static final RegistryObject<SoundEvent> ENERGY_SHIELD = registerSoundEvent("energy_shield");

    public static final RegistryObject<SoundEvent> VOID_BLOSSOM_HURT = registerSoundEvent("void_blossom_hurt");
    public static final RegistryObject<SoundEvent> VOID_BLOSSOM_FALL = registerSoundEvent("void_blossom_fall");
    public static final RegistryObject<SoundEvent> VOID_BLOSSOM_SPIKE = registerSoundEvent("void_blossom_spike");
    public static final RegistryObject<SoundEvent> VOID_BLOSSOM_BURROW = registerSoundEvent("void_blossom_burrow");
    public static final RegistryObject<SoundEvent> SPORE_PREPARE = registerSoundEvent("spore_prepare");
    public static final RegistryObject<SoundEvent> SPORE_IMPACT = registerSoundEvent("spore_impact");
    public static final RegistryObject<SoundEvent> SPORE_BALL_LAND = registerSoundEvent("spore_ball_land");
    public static final RegistryObject<SoundEvent> PETAL_BLADE = registerSoundEvent("petal_blade");
    public static final RegistryObject<SoundEvent> VOID_SPIKE_INDICATOR = registerSoundEvent("void_spike_indicator");

    public static final RegistryObject<SoundEvent> SPIKE_WAVE_INDICATOR = registerSoundEvent("spike_wave_indicator");
    public static final RegistryObject<SoundEvent> WAVE_INDICATOR = registerSoundEvent("wave_indicator");

    public static final RegistryObject<SoundEvent> SOUL_STAR = registerSoundEvent("soul_star");
    public static final RegistryObject<SoundEvent> EARTHDIVE_SPEAR_THROW = registerSoundEvent("earthdive_spear_throw");
    public static final RegistryObject<SoundEvent> CHARGED_ENDER_PEARL = registerSoundEvent("charged_ender_pearl");
    public static final RegistryObject<SoundEvent> BRIMSTONE = registerSoundEvent("brimstone");

    private static RegistryObject<SoundEvent> registerSoundEvent(String name){
        ResourceLocation id = new ResourceLocation(BMDConstants.MOD_ID, name);
        return SOUND_EVENTS.register(name, () -> new SoundEvent(id));
    }

    public static void register(IEventBus eventBus){
        SOUND_EVENTS.register(eventBus);
    }
}
