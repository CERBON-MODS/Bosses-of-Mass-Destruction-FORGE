package com.cerbon.bosses_of_mass_destruction.entity.custom.lich;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.VecUtils;
import com.cerbon.bosses_of_mass_destruction.config.mob.LichConfig;
import com.cerbon.bosses_of_mass_destruction.item.BMDItems;
import com.cerbon.bosses_of_mass_destruction.particle.BMDParticles;
import com.cerbon.bosses_of_mass_destruction.util.BMDUtils;
import com.google.common.collect.Lists;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.stats.Stats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.stream.Collectors;

public class LichKillCounter {
    private final LichConfig.SummonMechanic config;
    private final List<EntityType<?>> countedEntities;

    public LichKillCounter(LichConfig.SummonMechanic config) {
        this.config = config;
        this.countedEntities = config.entitiesThatCountToSummonCounter != null
                ? config.entitiesThatCountToSummonCounter.stream()
                .map(string -> ForgeRegistries.ENTITIES.getValue(ResourceLocation.tryParse(string)))
                .collect(Collectors.toList())
                : Lists.newArrayList();
    }

    public void afterKilledOtherEntity(Entity entity, LivingEntity killedEntity) {
        if (entity instanceof ServerPlayerEntity && countedEntities.contains(killedEntity.getType())) {
            int entitiesKilled = getUndeadKilled((ServerPlayerEntity) entity);

            if (entitiesKilled > 0 && entitiesKilled % config.numEntitiesKilledToDropSoulStar == 0) {
                BMDUtils.spawnParticle(((ServerPlayerEntity) entity).getLevel(), BMDParticles.SOUL_FLAME.get(), killedEntity.position().add(VecUtils.yAxis), VecUtils.unit, 15, 0.0);
                killedEntity.spawnAtLocation(BMDItems.SOUL_STAR.get());
            }
        }
    }

    private int getUndeadKilled(ServerPlayerEntity entity) {
        return countedEntities.stream()
                .mapToInt(entityType -> entity.getStats().getValue(Stats.ENTITY_KILLED.get(entityType)))
                .sum();
    }
}
