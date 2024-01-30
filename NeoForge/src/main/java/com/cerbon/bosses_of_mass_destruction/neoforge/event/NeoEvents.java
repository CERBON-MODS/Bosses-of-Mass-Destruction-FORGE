package com.cerbon.bosses_of_mass_destruction.neoforge.event;

import com.cerbon.bosses_of_mass_destruction.block.custom.LevitationBlockEntity;
import com.cerbon.bosses_of_mass_destruction.entity.BMDEntities;
import com.cerbon.bosses_of_mass_destruction.neoforge.attachment.BMDAttachments;
import com.cerbon.bosses_of_mass_destruction.neoforge.attachment.saved_data.LevelChunkBlockCache;
import com.cerbon.bosses_of_mass_destruction.util.BMDConstants;
import com.cerbon.cerbons_api.api.general.data.HistoricalData;
import com.cerbon.cerbons_api.neoforge.attachment.saved_data.LevelEventScheduler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.LogicalSide;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.event.TickEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

@Mod.EventBusSubscriber(modid = BMDConstants.MOD_ID)
public class NeoEvents {

    @SubscribeEvent
    public static void onPlayerLogging(ClientPlayerNetworkEvent.LoggingIn event) {
        LevelChunkBlockCache.INSTANCE = new LevelChunkBlockCache();
    }

    @SubscribeEvent
    protected static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if(event.side == LogicalSide.CLIENT) return;

        HistoricalData<Vec3> data = event.player.getData(BMDAttachments.HISTORICAL_DATA);

        Vec3 previousPosition = data.get(0);
        Vec3 newPosition = event.player.position();

        // Extremely fast movement in one tick is a sign of teleportation or dimension hopping, and thus we should clear history to avoid undefined behavior
        if (previousPosition.distanceToSqr(newPosition) > 5)
            data.clear();

        data.add(newPosition);

        LevitationBlockEntity.tickFlight((ServerPlayer) event.player);
    }

    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event){
        if ((event.side == LogicalSide.SERVER || event.side == LogicalSide.CLIENT) && event.level.getGameTime() % 2 == 0)
            LevelEventScheduler.get(event.level).updateEvents();
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onLivingDeath(LivingDeathEvent event){
        if (BMDEntities.mobConfig.lichConfig.summonMechanic.isEnabled){
            Entity attacker = event.getSource().getEntity();

            if (attacker != null)
                BMDEntities.killCounter.afterKilledOtherEntity(attacker, event.getEntity());
        }
    }
}
