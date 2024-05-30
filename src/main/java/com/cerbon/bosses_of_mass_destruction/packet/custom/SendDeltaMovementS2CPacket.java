package com.cerbon.bosses_of_mass_destruction.packet.custom;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.PacketUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SendDeltaMovementS2CPacket {
    private final Vector3d deltaMovement;

    public SendDeltaMovementS2CPacket(Vector3d deltaMovement){
        this.deltaMovement = deltaMovement;
    }

    public SendDeltaMovementS2CPacket(PacketBuffer buf){
        this.deltaMovement = PacketUtils.readVec3(buf);
    }

    public void write(PacketBuffer buf){
        PacketUtils.writeVec3(buf, this.deltaMovement);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier){
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            Minecraft client = Minecraft.getInstance();
            ClientPlayerEntity localPlayer = client.player;
            if (localPlayer == null) return;

            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> client.execute(() -> localPlayer.setDeltaMovement(this.deltaMovement)));
        });
        ctx.setPacketHandled(true);
    }
}
