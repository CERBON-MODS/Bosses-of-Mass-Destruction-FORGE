package com.cerbon.bosses_of_mass_destruction.entity.custom.gauntlet;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.EventScheduler;
import com.cerbon.bosses_of_mass_destruction.config.mob.GauntletConfig;
import com.cerbon.bosses_of_mass_destruction.entity.ai.goals.CompositeGoal;
import com.cerbon.bosses_of_mass_destruction.entity.ai.goals.FindTargetGoal;
import com.cerbon.bosses_of_mass_destruction.entity.damage.IDamageHandler;
import com.cerbon.bosses_of_mass_destruction.entity.util.IEntityStats;
import com.cerbon.bosses_of_mass_destruction.entity.util.IMoveHandler;
import com.cerbon.bosses_of_mass_destruction.entity.util.INbtHandler;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.util.DamageSource;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.util.math.vector.Vector3d;

public class GauntletGoalHandler implements INbtHandler, IMoveHandler, IDamageHandler {
    private final GauntletEntity entity;
    private final GoalSelector goalSelector;
    private final GoalSelector targetSelector;
    private final EventScheduler eventScheduler;
    private final GauntletConfig mobConfig;
    private final GauntletMovement movementHelper;
    private boolean isAggroed = false;

    public GauntletGoalHandler(GauntletEntity entity, GoalSelector goalSelector, GoalSelector targetSelector, EventScheduler eventScheduler, GauntletConfig mobConfig) {
        this.entity = entity;
        this.goalSelector = goalSelector;
        this.targetSelector = targetSelector;
        this.eventScheduler = eventScheduler;
        this.mobConfig = mobConfig;
        this.movementHelper = new GauntletMovement(entity);
    }

    @Override
    public void beforeDamage(IEntityStats stats, DamageSource damageSource, float amount) {}

    @Override
    public void afterDamage(IEntityStats stats, DamageSource damageSource, float amount, boolean result) {
        if (result && !isAggroed){
            isAggroed = true;
            addGoals();
        }
    }

    @Override
    public boolean shouldDamage(LivingEntity actor, DamageSource damageSource, float amount) {
        return true;
    }

    @Override
    public boolean canMove(MoverType type, Vector3d movement) {
        return isAggroed;
    }

    @Override
    public CompoundNBT toTag(CompoundNBT tag) {
        tag.putBoolean("isAggroed", isAggroed);
        return tag;
    }

    @Override
    public void fromTag(CompoundNBT tag) {
        if (tag.contains("isAggroed")){
            isAggroed = tag.getBoolean("isAggroed");
            if (isAggroed) addGoals();
        }
    }

    private void addGoals() {
        World level = entity.level;
        if (level instanceof ServerWorld){
            GauntletAttacks attackHelper = new GauntletAttacks(entity, eventScheduler, mobConfig, (ServerWorld) level);
            CompositeGoal attackGoal = new CompositeGoal(movementHelper.buildAttackMovement(), attackHelper.buildAttackGoal());

            goalSelector.addGoal(2, new CompositeGoal());
            goalSelector.addGoal(3, attackGoal);

            targetSelector.addGoal(2, new FindTargetGoal<>(entity, PlayerEntity.class, d -> entity.getBoundingBox().inflate(d), 10, true, false, null));
        }
    }
}
