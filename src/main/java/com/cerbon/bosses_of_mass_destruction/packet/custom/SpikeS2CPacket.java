package com.cerbon.bosses_of_mass_destruction.packet.custom;

import com.cerbon.bosses_of_mass_destruction.entity.custom.void_blossom.VoidBlossomEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;

public class SpikeS2CPacket {
    private final int id;
    private List<BlockPos> spikesPositions;
    private BlockPos spikePos;

    public SpikeS2CPacket(int id, List<BlockPos> spikesPositions) {
        this.id = id;
        this.spikesPositions = spikesPositions;
    }

    public SpikeS2CPacket(FriendlyByteBuf buf) {
        this.id = buf.readInt();
        this.spikePos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
    }

    public void write(FriendlyByteBuf buf){
        for (BlockPos spikePos : spikesPositions){
            buf.writeInt(id);
            buf.writeInt(spikePos.getX());
            buf.writeInt(spikePos.getY());
            buf.writeInt(spikePos.getZ());
        }
    }

    public void handle(Supplier<NetworkEvent.Context> supplier){
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            Minecraft client = Minecraft.getInstance();

            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> client.execute(() -> {
                ClientLevel clientLevel = client.level;
                if (clientLevel == null) return;
                Entity entity = clientLevel.getEntity(id);

                if (entity instanceof VoidBlossomEntity){
                    ((VoidBlossomEntity) entity).clientSpikeHandler.addSpike(spikePos);
                }
            }));
        });
        ctx.setPacketHandled(true);
    }
}
