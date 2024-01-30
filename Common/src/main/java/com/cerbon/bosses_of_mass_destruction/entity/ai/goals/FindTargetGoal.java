package com.cerbon.bosses_of_mass_destruction.entity.ai.goals;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;
import java.util.function.Predicate;

public class FindTargetGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {
    private final Function<Double, AABB> searchBoxProvider;

    public FindTargetGoal(Mob mob, Class<T> targetClass, Function<Double, AABB> searchBoxProvider, int reciprocalChance, boolean checkVisibility, boolean checkCanNavigate, Predicate<LivingEntity> targetPredicate) {
        super(mob, targetClass, reciprocalChance, checkVisibility, checkCanNavigate, targetPredicate);
        this.searchBoxProvider = searchBoxProvider;
        unseenMemoryTicks = 200;
    }

    @Override
    protected @NotNull AABB getTargetSearchArea(double distance) {
        return searchBoxProvider.apply(distance);
    }
}

