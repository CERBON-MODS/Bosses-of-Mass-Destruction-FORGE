package com.cerbon.bosses_of_mass_destruction.entity.ai.goals;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.util.math.AxisAlignedBB;

import javax.annotation.Nonnull;
import java.util.function.Function;
import java.util.function.Predicate;

public class FindTargetGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {
    private final Function<Double, AxisAlignedBB> searchBoxProvider;

    public FindTargetGoal(MobEntity mob, Class<T> targetClass, Function<Double, AxisAlignedBB> searchBoxProvider, int reciprocalChance, boolean checkVisibility, boolean checkCanNavigate, Predicate<LivingEntity> targetPredicate) {
        super(mob, targetClass, reciprocalChance, checkVisibility, checkCanNavigate, targetPredicate);
        this.searchBoxProvider = searchBoxProvider;
        unseenMemoryTicks = 200;
    }

    @Override
    protected @Nonnull AxisAlignedBB getTargetSearchArea(double distance) {
        return searchBoxProvider.apply(distance);
    }
}

