package com.cerbon.bosses_of_mass_destruction.entity.custom.lich;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.VecUtils;
import com.cerbon.bosses_of_mass_destruction.config.mob.LichConfig;
import com.cerbon.bosses_of_mass_destruction.item.BMDItems;
import com.cerbon.bosses_of_mass_destruction.particle.BMDParticles;
import com.cerbon.bosses_of_mass_destruction.util.BMDUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
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
                .map(string -> ForgeRegistries.ENTITY_TYPES.getValue(ResourceLocation.tryParse(string)))
                .collect(Collectors.toList())
                : List.of();
    }

    public void afterKilledOtherEntity(Entity entity, LivingEntity killedEntity) {
        if (entity instanceof ServerPlayer && countedEntities.contains(killedEntity.getType())) {
            int entitiesKilled = getUndeadKilled((ServerPlayer) entity);
            if (entitiesKilled > 0 && entitiesKilled % config.numEntitiesKilledToDropSoulStar == 0) {
                BMDUtils.spawnParticle(((ServerPlayer) entity).serverLevel(), BMDParticles.SOUL_FLAME.get(), killedEntity.position().add(VecUtils.yAxis), VecUtils.unit, 15, 0.0);
                killedEntity.spawnAtLocation(BMDItems.BLAZING_EYE.get()); // TODO: Change item to SoulStar
            }
        }
    }

    private int getUndeadKilled(ServerPlayer entity) {
        return countedEntities.stream()
                .mapToInt(entityType -> entity.getStats().getValue(Stats.ENTITY_KILLED.get(entityType)))
                .sum();
    }
}
