package com.cerbon.bosses_of_mass_destruction.event;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.EventScheduler;
import com.cerbon.bosses_of_mass_destruction.block.custom.LevitationBlockEntity;
import com.cerbon.bosses_of_mass_destruction.capability.ChunkBlockCacheProvider;
import com.cerbon.bosses_of_mass_destruction.capability.LevelEventSchedulerProvider;
import com.cerbon.bosses_of_mass_destruction.capability.PlayerMoveHistoryProvider;
import com.cerbon.bosses_of_mass_destruction.entity.BMDEntities;
import com.cerbon.bosses_of_mass_destruction.util.BMDConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

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
}
