package com.cerbon.bosses_of_mass_destruction.packet.custom;

import com.cerbon.bosses_of_mass_destruction.entity.custom.gauntlet.GauntletEntity;
import com.cerbon.bosses_of_mass_destruction.util.BMDConstants;
import com.cerbon.cerbons_api.api.network.Network;
import com.cerbon.cerbons_api.api.network.data.PacketContext;
import com.cerbon.cerbons_api.api.network.data.Side;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class BlindnessS2CPacket implements CustomPacketPayload {
    public static final ResourceLocation IDENTIFIER = ResourceLocation.fromNamespaceAndPath(BMDConstants.MOD_ID, "blindness_s2c_packet");
    public static final StreamCodec<RegistryFriendlyByteBuf, BlindnessS2CPacket> CODEC = new StreamCodec<>() {

        @Override
        public void encode(RegistryFriendlyByteBuf buf, BlindnessS2CPacket packet) {
            buf.writeInt(packet.entityId);
            buf.writeVarIntArray(packet.playerIds);
        }

        @Override
        public @NotNull BlindnessS2CPacket decode(RegistryFriendlyByteBuf buf) {
            return new BlindnessS2CPacket(buf.readInt(), buf.readVarIntArray());
        }
    };

    private final int entityId;
    private final int[] playerIds;

    public BlindnessS2CPacket(int entityId, int[] playerIds) {
        this.entityId = entityId;
        this.playerIds = playerIds;
    }

    public static void handle(PacketContext<BlindnessS2CPacket> ctx) {
        if (ctx.side().equals(Side.SERVER)) return;

        BlindnessS2CPacket packet = ctx.message();

        Minecraft client = Minecraft.getInstance();
        ClientLevel level = client.level;
        if (level == null) return;

        client.execute(() -> {
            Entity entity = level.getEntity(packet.entityId);
            List<Player> players = Arrays.stream(packet.playerIds)
                                         .mapToObj(level::getEntity)
                                         .filter(id -> id instanceof Player)
                                         .map(id -> (Player) id)
                                         .toList();

            if (entity instanceof GauntletEntity gauntletEntity)
                gauntletEntity.clientBlindnessHandler.handlePlayerEffects(players);
        });
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return Network.getType(IDENTIFIER);
    }
}