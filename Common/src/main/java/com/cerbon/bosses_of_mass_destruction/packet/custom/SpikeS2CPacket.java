package com.cerbon.bosses_of_mass_destruction.packet.custom;

import com.cerbon.bosses_of_mass_destruction.entity.custom.void_blossom.VoidBlossomEntity;
import com.cerbon.bosses_of_mass_destruction.util.BMDConstants;
import com.cerbon.cerbons_api.api.network.Network;
import com.cerbon.cerbons_api.api.network.data.PacketContext;
import com.cerbon.cerbons_api.api.network.data.Side;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SpikeS2CPacket implements CustomPacketPayload {
    public static final ResourceLocation IDENTIFIER = ResourceLocation.fromNamespaceAndPath(BMDConstants.MOD_ID, "spike_s2c_packet");
    public static final StreamCodec<RegistryFriendlyByteBuf, SpikeS2CPacket> CODEC = new StreamCodec<>() {

        @Override
        public void encode(RegistryFriendlyByteBuf buf, SpikeS2CPacket packet) {
            buf.writeInt(packet.id);
            buf.writeInt(packet.spikePositions.size());
            for (BlockPos spikePos : packet.spikePositions)
                buf.writeBlockPos(spikePos);
        }

        @Override
        public @NotNull SpikeS2CPacket decode(RegistryFriendlyByteBuf buf) {
            int id = buf.readInt();
            int size = buf.readInt();
            List<BlockPos> spikePositions = new ArrayList<>();
            for (int i = 0; i < size; i++)
                spikePositions.add(buf.readBlockPos());

            return new SpikeS2CPacket(id, spikePositions);
        }
    };

    private final int id;
    private final List<BlockPos> spikePositions;

    public SpikeS2CPacket(int id, List<BlockPos> spikePositions) {
        this.id = id;
        this.spikePositions = spikePositions;
    }

    public static void handle(PacketContext<SpikeS2CPacket> ctx) {
        if (ctx.side().equals(Side.SERVER)) return;

        SpikeS2CPacket packet = ctx.message();
        Minecraft client = Minecraft.getInstance();

        client.execute(() -> {
            ClientLevel clientLevel = client.level;
            if (clientLevel == null) return;
            Entity entity = clientLevel.getEntity(packet.id);

            if (entity instanceof VoidBlossomEntity voidBlossomEntity)
                packet.spikePositions.forEach(voidBlossomEntity.clientSpikeHandler::addSpike);
        });
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return Network.getType(IDENTIFIER);
    }
}