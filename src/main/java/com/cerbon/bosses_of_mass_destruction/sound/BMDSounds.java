package com.cerbon.bosses_of_mass_destruction.sound;

import com.cerbon.bosses_of_mass_destruction.util.BMDConstants;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class BMDSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, BMDConstants.MOD_ID);

    public static final Supplier<SoundEvent> GAUNTLET_HURT = registerSoundEvent("gauntlet_hurt");
    public static final Supplier<SoundEvent> GAUNTLET_DEATH = registerSoundEvent("gauntlet_death");
    public static final Supplier<SoundEvent> GAUNTLET_IDLE = registerSoundEvent("gauntlet_idle");
    public static final Supplier<SoundEvent> GAUNTLET_CAST = registerSoundEvent("gauntlet_cast");
    public static final Supplier<SoundEvent> GAUNTLET_CLINK = registerSoundEvent("gauntlet_clink");
    public static final Supplier<SoundEvent> GAUNTLET_SPIN_PUNCH = registerSoundEvent("gauntlet_spin_punch");
    public static final Supplier<SoundEvent> GAUNTLET_LASER_CHARGE = registerSoundEvent("gauntlet_laser_charge");

    public static final Supplier<SoundEvent> LICH_HURT = registerSoundEvent("lich_hurt");
    public static final Supplier<SoundEvent> LICH_DEATH = registerSoundEvent("lich_death");
    public static final Supplier<SoundEvent> LICH_TELEPORT = registerSoundEvent("lich_teleport");
    public static final Supplier<SoundEvent> BLUE_FIREBALL_LAND = registerSoundEvent("blue_fireball_land");
    public static final Supplier<SoundEvent> COMET_SHOOT = registerSoundEvent("comet_shoot");
    public static final Supplier<SoundEvent> COMET_PREPARE = registerSoundEvent("comet_prepare");
    public static final Supplier<SoundEvent> MISSILE_SHOOT = registerSoundEvent("missile_shoot");
    public static final Supplier<SoundEvent> MISSILE_PREPARE = registerSoundEvent("missile_prepare");
    public static final Supplier<SoundEvent> MINION_RUNE = registerSoundEvent("minion_rune");
    public static final Supplier<SoundEvent> MINION_SUMMON = registerSoundEvent("minion_summon");
    public static final Supplier<SoundEvent> RAGE_PREPARE = registerSoundEvent("rage_prepare");

    public static final Supplier<SoundEvent> OBSIDILITH_HURT = registerSoundEvent("obsidilith_hurt");
    public static final Supplier<SoundEvent> OBSIDILITH_DEATH = registerSoundEvent("obsidilith_death");
    public static final Supplier<SoundEvent> OBSIDILITH_PREPARE_ATTACK = registerSoundEvent("obsidilith_prepare_attack");
    public static final Supplier<SoundEvent> OBSIDILITH_BURST = registerSoundEvent("obsidilith_burst");
    public static final Supplier<SoundEvent> OBSIDILITH_WAVE = registerSoundEvent("obsidilith_wave");
    public static final Supplier<SoundEvent> OBSIDILITH_TELEPORT = registerSoundEvent("obsidilith_teleport");
    public static final Supplier<SoundEvent> TELEPORT_PREPARE = registerSoundEvent("teleport_prepare");
    public static final Supplier<SoundEvent> SPIKE = registerSoundEvent("spike");
    public static final Supplier<SoundEvent> SPIKE_INDICATOR = registerSoundEvent("spike_indicator");
    public static final Supplier<SoundEvent> ENERGY_SHIELD = registerSoundEvent("energy_shield");

    public static final Supplier<SoundEvent> VOID_BLOSSOM_HURT = registerSoundEvent("void_blossom_hurt");
    public static final Supplier<SoundEvent> VOID_BLOSSOM_FALL = registerSoundEvent("void_blossom_fall");
    public static final Supplier<SoundEvent> VOID_BLOSSOM_SPIKE = registerSoundEvent("void_blossom_spike");
    public static final Supplier<SoundEvent> VOID_BLOSSOM_BURROW = registerSoundEvent("void_blossom_burrow");
    public static final Supplier<SoundEvent> SPORE_PREPARE = registerSoundEvent("spore_prepare");
    public static final Supplier<SoundEvent> SPORE_IMPACT = registerSoundEvent("spore_impact");
    public static final Supplier<SoundEvent> SPORE_BALL_LAND = registerSoundEvent("spore_ball_land");
    public static final Supplier<SoundEvent> PETAL_BLADE = registerSoundEvent("petal_blade");
    public static final Supplier<SoundEvent> VOID_SPIKE_INDICATOR = registerSoundEvent("void_spike_indicator");

    public static final Supplier<SoundEvent> SPIKE_WAVE_INDICATOR = registerSoundEvent("spike_wave_indicator");
    public static final Supplier<SoundEvent> WAVE_INDICATOR = registerSoundEvent("wave_indicator");

    public static final Supplier<SoundEvent> SOUL_STAR = registerSoundEvent("soul_star");
    public static final Supplier<SoundEvent> EARTHDIVE_SPEAR_THROW = registerSoundEvent("earthdive_spear_throw");
    public static final Supplier<SoundEvent> CHARGED_ENDER_PEARL = registerSoundEvent("charged_ender_pearl");
    public static final Supplier<SoundEvent> BRIMSTONE = registerSoundEvent("brimstone");

    private static Supplier<SoundEvent> registerSoundEvent(String name){
        ResourceLocation id = new ResourceLocation(BMDConstants.MOD_ID, name);
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(id));
    }

    public static void register(IEventBus eventBus){
        SOUND_EVENTS.register(eventBus);
    }
}
