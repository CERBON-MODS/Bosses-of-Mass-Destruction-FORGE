package com.cerbon.bosses_of_mass_destruction.entity.damage;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.data.HistoricalData;
import com.cerbon.bosses_of_mass_destruction.entity.util.IEntityStats;
import net.minecraft.util.DamageSource;
import net.minecraft.entity.LivingEntity;

import java.util.List;

public class DamageMemory implements IDamageHandler {
    private final HistoricalData<DamageHistory> historicalData;
    private final LivingEntity entity;

    public DamageMemory(int hitsToRemember, LivingEntity entity) {
        this.entity = entity;
        DamageHistory defaultDamageHistory = new DamageHistory(0f, DamageSource.OUT_OF_WORLD, 0);
        this.historicalData = new HistoricalData<>(defaultDamageHistory, hitsToRemember);
    }

    @Override
    public void beforeDamage(IEntityStats stats, DamageSource damageSource, float amount) {}

    @Override
    public void afterDamage(IEntityStats stats, DamageSource damageSource, float amount, boolean result) {
        int minimumDamageToNotice = 4;
        if(result && damageSource.getEntity() != null && amount > minimumDamageToNotice)
            historicalData.set(new DamageHistory(amount, damageSource, entity.tickCount));
    }

    @Override
    public boolean shouldDamage(LivingEntity actor, DamageSource damageSource, float amount) {return true;}

    public List<DamageHistory> getDamageHistory() {
        return historicalData.getAll();
    }

    public static class DamageHistory {

        public final float amount;
        public final DamageSource source;
        public final int ageWhenDamaged;

        public DamageHistory(float amount, DamageSource source, int ageWhenDamaged) {
            this.amount = amount;
            this.source = source;
            this.ageWhenDamaged = ageWhenDamaged;
        }

        public float amount() {
            return amount;
        }
    }
}

