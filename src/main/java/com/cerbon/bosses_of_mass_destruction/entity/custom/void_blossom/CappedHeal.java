package com.cerbon.bosses_of_mass_destruction.entity.custom.void_blossom;

import com.cerbon.bosses_of_mass_destruction.entity.custom.lich.LichUtils;
import com.cerbon.bosses_of_mass_destruction.entity.util.EntityAdapter;
import com.cerbon.bosses_of_mass_destruction.entity.util.EntityStats;
import com.cerbon.bosses_of_mass_destruction.entity.util.IEntityTick;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.entity.MobEntity;

import java.util.List;

public class CappedHeal implements IEntityTick<ServerWorld> {
    private final MobEntity entity;
    private final List<Float> hpMilestones;
    private final float healingPerTick;
    private final EntityAdapter adapter;
    private final EntityStats stats;

    public CappedHeal(MobEntity entity, List<Float> hpMilestones, float healingPerTick) {
        this.entity = entity;
        this.hpMilestones = hpMilestones;
        this.healingPerTick = healingPerTick;
        this.adapter = new EntityAdapter(entity);
        this.stats = new EntityStats(entity);
    }

    @Override
    public void tick(ServerWorld level) {
        if (entity.getTarget() == null)
            LichUtils.cappedHeal(adapter, stats, hpMilestones, healingPerTick, entity::heal);
    }
}

