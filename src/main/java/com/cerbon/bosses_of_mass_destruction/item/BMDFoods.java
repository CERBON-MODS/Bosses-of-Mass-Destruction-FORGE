package com.cerbon.bosses_of_mass_destruction.item;

import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.item.Food;

public class BMDFoods {
    public static final Food CRYSTAL_FRUIT = new Food.Builder()
            .nutrition(4)
            .saturationMod(1.2F)
            .effect(() -> new EffectInstance(Effects.REGENERATION, 300, 1), 1.0F)
            .effect(() -> new EffectInstance(Effects.HEAL, 1), 1.0F)
            .effect(() -> new EffectInstance(Effects.DAMAGE_RESISTANCE, 600, 0), 1.0F)
            .alwaysEat()
            .build();
}
