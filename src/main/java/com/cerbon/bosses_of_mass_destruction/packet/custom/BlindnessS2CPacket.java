package com.cerbon.bosses_of_mass_destruction.packet.custom;

import com.cerbon.bosses_of_mass_destruction.entity.custom.gauntlet.GauntletEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.fml.DistExecutor;

import java.util.Arrays;
import java.util.List;

public class BlindnessS2CPacket {
    private final int entityId;
    private final int[] playerIds;

    public BlindnessS2CPacket(int entityId, int[] playerIds) {
        this.entityId = entityId;
        this.playerIds = playerIds;
    }

    public BlindnessS2CPacket(FriendlyByteBuf buf) {
        this.entityId = buf.readInt();
        this.playerIds = buf.readVarIntArray();
    }

    public void write(FriendlyByteBuf buf){
        buf.writeInt(entityId);
        buf.writeVarIntArray(playerIds);
    }

    public void handle(CustomPayloadEvent.Context ctx){
        ctx.enqueueWork(() -> {
            Minecraft client = Minecraft.getInstance();
            ClientLevel level = client.level;
            if (level == null) return;

            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> client.execute(() -> {
                Entity entity = level.getEntity(entityId);
                List<Player> players = Arrays.stream(playerIds)
                        .mapToObj(level::getEntity)
                        .filter(id -> id instanceof Player)
                        .map(id -> (Player) id)
                        .toList();

                if (entity instanceof GauntletEntity)
                    ((GauntletEntity) entity).clientBlindnessHandler.handlePlayerEffects(players);
            }));
        });
        ctx.setPacketHandled(true);
    }
}