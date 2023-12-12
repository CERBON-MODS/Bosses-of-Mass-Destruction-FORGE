package com.cerbon.bosses_of_mass_destruction.packet.custom;

import com.cerbon.bosses_of_mass_destruction.entity.custom.void_blossom.VoidBlossomEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.fml.DistExecutor;

import java.util.ArrayList;
import java.util.List;

public class SpikeS2CPacket {
    private final int id;
    private final List<BlockPos> spikePositions;

    public SpikeS2CPacket(int id, List<BlockPos> spikePositions) {
        this.id = id;
        this.spikePositions = spikePositions;
    }

    public SpikeS2CPacket(FriendlyByteBuf buf) {
        this.id = buf.readInt();
        int size = buf.readInt();
        this.spikePositions = new ArrayList<>();
        for (int i = 0; i < size; i++)
            this.spikePositions.add(buf.readBlockPos());
    }

    public void write(FriendlyByteBuf buf){
        buf.writeInt(id);
        buf.writeInt(spikePositions.size());
        for (BlockPos spikePos : spikePositions){
            buf.writeBlockPos(spikePos);
        }
    }

    public void handle(CustomPayloadEvent.Context ctx){
        ctx.enqueueWork(() -> {
            Minecraft client = Minecraft.getInstance();

            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> client.execute(() -> {
                ClientLevel clientLevel = client.level;
                if (clientLevel == null) return;
                Entity entity = clientLevel.getEntity(id);

                if (entity instanceof VoidBlossomEntity){
                    spikePositions.forEach(spikePos -> ((VoidBlossomEntity) entity).clientSpikeHandler.addSpike(spikePos));
                }
            }));
        });
        ctx.setPacketHandled(true);
    }
}
