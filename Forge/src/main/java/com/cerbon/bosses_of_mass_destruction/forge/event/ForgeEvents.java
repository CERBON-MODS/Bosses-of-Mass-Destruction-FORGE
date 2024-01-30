package com.cerbon.bosses_of_mass_destruction.forge.event;

import com.cerbon.bosses_of_mass_destruction.block.custom.LevitationBlockEntity;
import com.cerbon.bosses_of_mass_destruction.entity.BMDEntities;
import com.cerbon.bosses_of_mass_destruction.forge.capability.ChunkBlockCacheProvider;
import com.cerbon.bosses_of_mass_destruction.forge.capability.PlayerMoveHistoryProvider;
import com.cerbon.bosses_of_mass_destruction.util.BMDConstants;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
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
    public static void onAttachCapabilitiesLevel(AttachCapabilitiesEvent<Level> event) {
        if (event.getObject() == null || event.getObject().getCapability(ChunkBlockCacheProvider.CHUNK_BLOCK_CACHE).isPresent()) return;
        event.addCapability(new ResourceLocation(BMDConstants.MOD_ID, "chunk_block_cache_capability"), new ChunkBlockCacheProvider());
    }

    @SubscribeEvent
    public static void onAttachCapabilitiesPlayer(AttachCapabilitiesEvent<Entity> event) {
        if(!(event.getObject() instanceof Player) || event.getObject().getCapability(PlayerMoveHistoryProvider.HISTORICAL_DATA).isPresent()) return;
        event.addCapability(new ResourceLocation(BMDConstants.MOD_ID, "player_move_history"), new PlayerMoveHistoryProvider());
    }

    @SubscribeEvent
    protected static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if(event.side == LogicalSide.CLIENT) return;

        event.player.getCapability(PlayerMoveHistoryProvider.HISTORICAL_DATA).ifPresent(data -> {
            Vec3 previousPosition = data.get(0);
            Vec3 newPosition = event.player.position();

            // Extremely fast movement in one tick is a sign of teleportation or dimension hopping, and thus we should clear history to avoid undefined behavior
            if (previousPosition.distanceToSqr(newPosition) > 5)
                data.clear();

            data.add(newPosition);
        });

        LevitationBlockEntity.tickFlight((ServerPlayer) event.player);
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onLivingDeath(LivingDeathEvent event){
        if (BMDEntities.mobConfig.lichConfig.summonMechanic.isEnabled) {
            Entity attacker = event.getSource().getEntity();

            if (attacker != null)
                BMDEntities.killCounter.afterKilledOtherEntity(attacker, event.getEntity());
        }
    }
}
