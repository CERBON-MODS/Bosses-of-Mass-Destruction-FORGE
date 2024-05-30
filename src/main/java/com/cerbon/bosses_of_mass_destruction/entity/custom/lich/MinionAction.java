package com.cerbon.bosses_of_mass_destruction.entity.custom.lich;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.EventScheduler;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.TimedEvent;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.random.ModRandom;
import com.cerbon.bosses_of_mass_destruction.entity.ai.action.IActionWithCooldown;
import com.cerbon.bosses_of_mass_destruction.entity.spawn.*;
import com.cerbon.bosses_of_mass_destruction.particle.BMDParticles;
import com.cerbon.bosses_of_mass_destruction.sound.BMDSounds;
import com.cerbon.bosses_of_mass_destruction.util.BMDUtils;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class MinionAction implements IActionWithCooldown {
    private final LichEntity entity;
    private final EventScheduler eventScheduler;
    private final Supplier<Boolean> shouldCancel;

    public static final int minionSummonCooldown = 80;
    public static final int minionSummonParticleDelay = 10;
    public static final int minionSummonDelay = 40;
    public static final int minionRuneToMinionSpawnDelay = 40;
    public static final String summonId = "minecraft:phantom";
    public static final CompoundNBT summonNbt;

    static {
        try {
            summonNbt = JsonToNBT.parseTag("{Health:14,Size:2,Attributes:[{Name:\"generic.max_health\",Base:14f}]}");
        } catch (CommandSyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static final EntityType<?> summonEntityType = ForgeRegistries.ENTITIES.getValue(ResourceLocation.tryParse(summonId));

    public MinionAction(LichEntity entity, EventScheduler eventScheduler, Supplier<Boolean> shouldCancel) {
        this.entity = entity;
        this.eventScheduler = eventScheduler;
        this.shouldCancel = shouldCancel;
    }

    @Override
    public int perform() {
        Entity target = entity.getTarget();
        if (!(target instanceof ServerPlayerEntity)) return minionSummonCooldown;
        performMinionSummon((ServerPlayerEntity) target);
        return minionSummonCooldown;
    }

    private void performMinionSummon(ServerPlayerEntity target) {
        eventScheduler.addEvent(
                new TimedEvent(
                        () -> beginSummonSingleMob(target),
                        minionSummonDelay,
                        1,
                        shouldCancel
                )
        );
    }

    void beginSummonSingleMob(ServerPlayerEntity target){
        CompoundNBT compoundTag = summonNbt.copy();
        compoundTag.putString("id", summonId);
        SimpleMobSpawner mobSpawner = new SimpleMobSpawner(target.getLevel());
        CompoundTagEntityProvider entityProvider = new CompoundTagEntityProvider(compoundTag, target.getLevel());
        MobEntitySpawnPredicate spawnPredicate = new MobEntitySpawnPredicate(target.level);
        IMobSpawner summonCircleBeforeSpawn = (pos, summon) -> {
            BMDUtils.spawnParticle(target.getLevel(), BMDParticles.MAGIC_CIRCLE.get(), pos, Vector3d.ZERO, 0, 0.0);
            BMDUtils.playSound(target.getLevel(), pos, BMDSounds.MINION_RUNE.get(), SoundCategory.HOSTILE, 1.0f, 64, null);
            eventScheduler.addEvent(
                    new TimedEvent(
                            () -> {
                                mobSpawner.spawn(pos, summon);
                                entity.playSound(BMDSounds.MINION_SUMMON.get(), 0.7f, 1.0f);
                            },
                            minionRuneToMinionSpawnDelay,
                            1,
                            shouldCancel
                    )
            );
        };

        new MobPlacementLogic(
                new RangedSpawnPosition(target.position(), 4.0, 8.0, new ModRandom()),
                entityProvider,
                spawnPredicate,
                summonCircleBeforeSpawn
        ).tryPlacement(30);
    }
}
