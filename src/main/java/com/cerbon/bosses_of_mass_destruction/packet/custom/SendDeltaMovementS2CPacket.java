package com.cerbon.bosses_of_mass_destruction.packet.custom;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.PacketUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.fml.DistExecutor;

public class SendDeltaMovementS2CPacket {
    private final Vec3 deltaMovement;

    public SendDeltaMovementS2CPacket(Vec3 deltaMovement){
        this.deltaMovement = deltaMovement;
    }

    public SendDeltaMovementS2CPacket(FriendlyByteBuf buf){
        this.deltaMovement = PacketUtils.readVec3(buf);
    }

    public void write(FriendlyByteBuf buf){
        PacketUtils.writeVec3(buf, this.deltaMovement);
    }

    public void handle(CustomPayloadEvent.Context ctx){
        ctx.enqueueWork(() -> {
            Minecraft client = Minecraft.getInstance();
            LocalPlayer localPlayer = client.player;
            if (localPlayer == null) return;

            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> client.execute(() -> localPlayer.setDeltaMovement(this.deltaMovement)));
        });
        ctx.setPacketHandled(true);
    }
}
