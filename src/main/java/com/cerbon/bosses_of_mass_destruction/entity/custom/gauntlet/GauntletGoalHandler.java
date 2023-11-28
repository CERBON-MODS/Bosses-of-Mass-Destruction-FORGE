package com.cerbon.bosses_of_mass_destruction.entity.custom.gauntlet;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.EventScheduler;
import com.cerbon.bosses_of_mass_destruction.config.mob.GauntletConfig;
import com.cerbon.bosses_of_mass_destruction.entity.ai.goals.CompositeGoal;
import com.cerbon.bosses_of_mass_destruction.entity.ai.goals.FindTargetGoal;
import com.cerbon.bosses_of_mass_destruction.entity.damage.IDamageHandler;
import com.cerbon.bosses_of_mass_destruction.entity.util.IEntityStats;
import com.cerbon.bosses_of_mass_destruction.entity.util.IMoveHandler;
import com.cerbon.bosses_of_mass_destruction.entity.util.INbtHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

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
    public boolean canMove(MoverType type, Vec3 movement) {
        return isAggroed;
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        tag.putBoolean("isAggroed", isAggroed);
        return tag;
    }

    @Override
    public void fromTag(CompoundTag tag) {
        if (tag.contains("isAggroed")){
            isAggroed = tag.getBoolean("isAggroed");
            if (isAggroed) addGoals();
        }
    }

    private void addGoals() {
        Level level = entity.level;
        if (level instanceof ServerLevel serverLevel){
            GauntletAttacks attackHelper = new GauntletAttacks(entity, eventScheduler, mobConfig, serverLevel);
            CompositeGoal attackGoal = new CompositeGoal(movementHelper.buildAttackMovement(), attackHelper.buildAttackGoal());

            goalSelector.addGoal(2, new CompositeGoal());
            goalSelector.addGoal(3, attackGoal);

            targetSelector.addGoal(2, new FindTargetGoal<>(entity, Player.class, d -> entity.getBoundingBox().inflate(d), 10, true, false, null));
        }
    }
}
