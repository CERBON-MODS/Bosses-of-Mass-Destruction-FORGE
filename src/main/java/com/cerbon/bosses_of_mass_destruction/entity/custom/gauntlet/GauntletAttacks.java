package com.cerbon.bosses_of_mass_destruction.entity.custom.gauntlet;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.EventScheduler;
import com.cerbon.bosses_of_mass_destruction.config.mob.GauntletConfig;
import com.cerbon.bosses_of_mass_destruction.entity.ai.action.CooldownAction;
import com.cerbon.bosses_of_mass_destruction.entity.ai.action.IActionStop;
import com.cerbon.bosses_of_mass_destruction.entity.ai.action.IActionWithCooldown;
import com.cerbon.bosses_of_mass_destruction.entity.ai.goals.ActionGoal;
import net.minecraft.world.server.ServerWorld;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class GauntletAttacks {
    private final GauntletEntity entity;
    private final GauntletMoveLogic moveLogic;

    public static final byte punchAttack = 4;
    public static final byte stopPunchAnimation = 5;
    public static final byte stopAttackAnimation = 6;
    public static final byte stopPoundAnimation = 7;
    public static final byte laserAttack = 8;
    public static final byte laserAttackStop = 9;
    public static final byte swirlPunchAttack = 10;
    public static final byte blindnessAttack = 11;

    public GauntletAttacks(GauntletEntity entity, EventScheduler eventScheduler, GauntletConfig mobConfig, ServerWorld serverLevel) {
        this.entity = entity;

        Supplier<Boolean> cancelAttackAction = () -> entity.isDeadOrDying() || entity.getTarget() == null;
        Map<Byte, IActionWithCooldown> statusRegistry = new HashMap<>();
        statusRegistry.put(punchAttack, new PunchAction(entity, eventScheduler, mobConfig, cancelAttackAction, serverLevel));
        statusRegistry.put(laserAttack, new LaserAction(entity, eventScheduler, cancelAttackAction, serverLevel));
        statusRegistry.put(swirlPunchAttack, new SwirlPunchAction(entity, eventScheduler, mobConfig, cancelAttackAction, serverLevel));
        statusRegistry.put(blindnessAttack, new BlindnessAction(entity, eventScheduler, cancelAttackAction, serverLevel));
        this.moveLogic = new GauntletMoveLogic(statusRegistry, entity, entity.damageMemory);
    }

    public ActionGoal buildAttackGoal(){
        CooldownAction attackAction = new CooldownAction(moveLogic, 80);
        IActionStop onCancel = () -> {
            entity.level.broadcastEntityEvent(entity, stopAttackAnimation);
            attackAction.stop();
        };
        return new ActionGoal(
                this::canContinueAttack,
                null,
                attackAction,
                null,
                onCancel
        );
    }

    private boolean canContinueAttack(){
        return entity.isAlive() && entity.getTarget() != null;
    }
}
