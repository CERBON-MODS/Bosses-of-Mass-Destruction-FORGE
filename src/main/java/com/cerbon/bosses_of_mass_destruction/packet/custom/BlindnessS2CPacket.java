package com.cerbon.bosses_of_mass_destruction.packet.custom;

import com.cerbon.bosses_of_mass_destruction.entity.custom.gauntlet.GauntletEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class BlindnessS2CPacket {
    private final int entityId;
    private final int[] playerIds;

    public BlindnessS2CPacket(int entityId, int[] playerIds) {
        this.entityId = entityId;
        this.playerIds = playerIds;
    }

    public BlindnessS2CPacket(PacketBuffer buf) {
        this.entityId = buf.readInt();
        this.playerIds = buf.readVarIntArray();
    }

    public void write(PacketBuffer buf){
        buf.writeInt(entityId);
        buf.writeVarIntArray(playerIds);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier){
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            Minecraft client = Minecraft.getInstance();
            ClientWorld level = client.level;
            if (level == null) return;

            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> client.execute(() -> {
                Entity entity = level.getEntity(entityId);
                List<PlayerEntity> players = Arrays.stream(playerIds)
                        .mapToObj(level::getEntity)
                        .filter(id -> id instanceof PlayerEntity)
                        .map(id -> (PlayerEntity) id)
                        .collect(Collectors.toList());

                if (entity instanceof GauntletEntity)
                    ((GauntletEntity) entity).clientBlindnessHandler.handlePlayerEffects(players);
            }));
        });
        ctx.setPacketHandled(true);
    }
}
