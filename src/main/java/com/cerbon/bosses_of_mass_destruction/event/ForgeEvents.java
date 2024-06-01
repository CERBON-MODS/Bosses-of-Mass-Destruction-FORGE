package com.cerbon.bosses_of_mass_destruction.event;

import com.cerbon.bosses_of_mass_destruction.BossesOfMassDestruction;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.EventScheduler;
import com.cerbon.bosses_of_mass_destruction.block.custom.LevitationBlockEntity;
import com.cerbon.bosses_of_mass_destruction.capability.ChunkBlockCacheProvider;
import com.cerbon.bosses_of_mass_destruction.capability.LevelEventSchedulerProvider;
import com.cerbon.bosses_of_mass_destruction.capability.PlayerMoveHistoryProvider;
import com.cerbon.bosses_of_mass_destruction.entity.BMDEntities;
import com.cerbon.bosses_of_mass_destruction.structure.BMDStructures;
import com.cerbon.bosses_of_mass_destruction.structure.BMDStructuresFeature;
import com.cerbon.bosses_of_mass_destruction.util.BMDConstants;
import com.mojang.serialization.Codec;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.FlatChunkGenerator;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;
import net.minecraft.world.gen.settings.StructureSeparationSettings;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = BMDConstants.MOD_ID)
public class ForgeEvents {

    @SubscribeEvent
    public static void onAttachCapabilitiesLevel(AttachCapabilitiesEvent<World> event){
        if (event.getObject() != null) {
            if (!event.getObject().getCapability(LevelEventSchedulerProvider.EVENT_SCHEDULER).isPresent())
                event.addCapability(new ResourceLocation(BMDConstants.MOD_ID, "event_scheduler"), new LevelEventSchedulerProvider());

            if (!event.getObject().getCapability(ChunkBlockCacheProvider.CHUNK_BLOCK_CACHE).isPresent())
                event.addCapability(new ResourceLocation(BMDConstants.MOD_ID, "chunk_block_cache_capability"), new ChunkBlockCacheProvider());
        }

    }

    @SubscribeEvent
    public static void onAttachCapabilitiesPlayer(AttachCapabilitiesEvent<Entity> event) {
        if(event.getObject() instanceof PlayerEntity)
            if(!event.getObject().getCapability(PlayerMoveHistoryProvider.HISTORICAL_DATA).isPresent())
                event.addCapability(new ResourceLocation(BMDConstants.MOD_ID, "player_move_history"), new PlayerMoveHistoryProvider());
    }

    @SubscribeEvent
    protected static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if(event.side == LogicalSide.SERVER) {
            event.player.getCapability(PlayerMoveHistoryProvider.HISTORICAL_DATA).ifPresent(data -> {
                Vector3d previousPosition = data.get(0);
                Vector3d newPosition = event.player.position();

                // Extremely fast movement in one tick is a sign of teleportation or dimension hopping, and thus we should clear history to avoid undefined behavior
                if (previousPosition.distanceToSqr(newPosition) > 5)
                    data.clear();

                data.set(newPosition);
            });

            LevitationBlockEntity.tickFlight((ServerPlayerEntity) event.player);
        }
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event){
        ClientWorld level = Minecraft.getInstance().level;
        if (level == null) return;

        if (level.getGameTime() % 2 == 0)
            level.getCapability(LevelEventSchedulerProvider.EVENT_SCHEDULER).ifPresent(EventScheduler::updateEvents);
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.WorldTickEvent event){
        if ((event.side == LogicalSide.SERVER) && event.world.getGameTime() % 2 == 0)
            event.world.getCapability(LevelEventSchedulerProvider.EVENT_SCHEDULER).ifPresent(EventScheduler::updateEvents);
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onLivingDeath(LivingDeathEvent event){
        if (BMDEntities.mobConfig.lichConfig.summonMechanic.isEnabled){
            Entity attacker = event.getSource().getEntity();

            if (attacker != null)
                BMDEntities.killCounter.afterKilledOtherEntity(attacker, event.getEntityLiving());
        }
    }

    @SubscribeEvent
    public static void biomeModification(final BiomeLoadingEvent event) {
        Biome.Category biomeCategory = event.getCategory();

        if (biomeCategory == Biome.Category.NETHER)
            event.getGeneration().getStructures().add(() -> BMDStructuresFeature.GAUNTLET_FEATURE);

        else if (biomeCategory == Biome.Category.THEEND)
            event.getGeneration().getStructures().add(() -> BMDStructuresFeature.OBSIDILITH_ARENA_FEATURE);

        else if (event.getClimate().temperature <= 0.05 && biomeCategory != Biome.Category.NONE)
            event.getGeneration().getStructures().add(() -> BMDStructuresFeature.LICH_TOWER_FEATURE);
    }

    private static Method GETCODEC_METHOD;
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void addDimensionalSpacing(final WorldEvent.Load event) {
        if (event.getWorld() instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld) event.getWorld();

            try {
                if(GETCODEC_METHOD == null) GETCODEC_METHOD = ObfuscationReflectionHelper.findMethod(ChunkGenerator.class, "func_230347_a_");
                ResourceLocation cgRL = Registry.CHUNK_GENERATOR.getKey((Codec<? extends ChunkGenerator>) GETCODEC_METHOD.invoke(serverWorld.getChunkSource().generator));
                if(cgRL != null && cgRL.getNamespace().equals("terraforged")) return;
            }
            catch(Exception e){
                BossesOfMassDestruction.LOGGER.error("Was unable to check if " + serverWorld.dimension().location() + " is using Terraforged's ChunkGenerator.");
            }

            if(serverWorld.getChunkSource().getGenerator() instanceof FlatChunkGenerator && serverWorld.dimension().equals(World.OVERWORLD)) return;

            Map<Structure<?>, StructureSeparationSettings> tempMap = new HashMap<>(serverWorld.getChunkSource().generator.getSettings().structureConfig());

            if (serverWorld.dimension().equals(World.OVERWORLD))
                tempMap.putIfAbsent(BMDStructures.LICH_TOWER_STRUCTURE.get(), DimensionStructuresSettings.DEFAULTS.get(BMDStructures.LICH_TOWER_STRUCTURE.get()));

            else if (serverWorld.dimension().equals(World.NETHER))
                tempMap.putIfAbsent(BMDStructures.GAUNTLET_STRUCTURE.get(), DimensionStructuresSettings.DEFAULTS.get(BMDStructures.GAUNTLET_STRUCTURE.get()));

            else if (serverWorld.dimension().equals(World.END))
                tempMap.putIfAbsent(BMDStructures.OBSIDILITH_ARENA_STRUCTURE.get(), DimensionStructuresSettings.DEFAULTS.get(BMDStructures.OBSIDILITH_ARENA_STRUCTURE.get()));

            serverWorld.getChunkSource().generator.getSettings().structureConfig = tempMap;
        }
    }
}
