package com.cerbon.bosses_of_mass_destruction.entity.custom.gauntlet;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.data.HistoricalData;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.random.WeightedRandom;
import com.cerbon.bosses_of_mass_destruction.entity.ai.TargetSwitcher;
import com.cerbon.bosses_of_mass_destruction.entity.ai.action.IActionWithCooldown;
import com.cerbon.bosses_of_mass_destruction.entity.damage.DamageMemory;
import net.minecraft.world.entity.LivingEntity;

import java.util.Map;

public class GauntletMoveLogic implements IActionWithCooldown {
    private final Map<Byte, IActionWithCooldown> actions;
    private final GauntletEntity entity;
    private final HistoricalData<Byte> moveHistory = new HistoricalData<>((byte) 0, 4);
    private final TargetSwitcher targetSwitcher;

    public static final double laserPercentage = 0.85;
    public static final double swirlPunchPercentage = 0.7;
    public static final double blindnessPercentage = 0.5;

    public GauntletMoveLogic(Map<Byte, IActionWithCooldown> actions, GauntletEntity entity, DamageMemory damageMemory) {
        this.actions = actions;
        this.entity = entity;
        this.targetSwitcher = new TargetSwitcher(entity, damageMemory);
    }

    @Override
    public int perform() {
        targetSwitcher.trySwitchTarget();
        byte moveByte = chooseMove();
        IActionWithCooldown action = actions.get(moveByte);
        if (action == null) throw new IllegalArgumentException(moveByte + " action not registered as an attack");
        entity.level().broadcastEntityEvent(entity, moveByte);
        return action.perform();
    }

    private byte chooseMove(){
        LivingEntity target = entity.getTarget();
        if (target == null) return GauntletAttacks.punchAttack;
        float healthPercentage = entity.getHealth() / entity.getMaxHealth();

        WeightedRandom<Byte> random = new WeightedRandom<>();

        double punchWeight = 1.0;
        double laserWeight = moveHistory.get(0) == GauntletAttacks.laserAttack || healthPercentage >= laserPercentage ? 0.0 : 0.7;
        double swirlPunchWeight = moveHistory.get(0) == GauntletAttacks.swirlPunchAttack || healthPercentage >= swirlPunchPercentage ? 0.0 : 0.7;
        double blindnessWeight = moveHistory.getAll().contains(GauntletAttacks.blindnessAttack) || healthPercentage >= blindnessPercentage ? 0.0 : 1.0;

        random.add(punchWeight, GauntletAttacks.punchAttack);
        random.add(laserWeight, GauntletAttacks.laserAttack);
        random.add(swirlPunchWeight, GauntletAttacks.swirlPunchAttack);
        random.add(blindnessWeight, GauntletAttacks.blindnessAttack);

        byte nextMove = random.next();
        moveHistory.set(nextMove);

        return nextMove;
    }
}
