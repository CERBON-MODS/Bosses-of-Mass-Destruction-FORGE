package com.cerbon.bosses_of_mass_destruction.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;

public class BMDFoods {
    public static final FoodProperties CRYSTAL_FRUIT = new FoodProperties.Builder()
            .nutrition(4)
            .saturationModifier(1.2F)
            .effect(new MobEffectInstance(MobEffects.REGENERATION, 300, 1), 1.0F)
            .effect(new MobEffectInstance(MobEffects.HEAL, 1), 1.0F)
            .effect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 600, 0), 1.0F)
            .alwaysEdible()
            .build();
}
