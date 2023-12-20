package com.cerbon.bosses_of_mass_destruction.packet.custom;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.PacketUtils;
import com.cerbon.bosses_of_mass_destruction.block.custom.VoidBlossomBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.NetworkEvent;

public class HealS2CPacket {
    private final Vec3 source;
    private final Vec3 dest;

    public HealS2CPacket(Vec3 source, Vec3 dest) {
        this.source = source;
        this.dest = dest;
    }

    public HealS2CPacket(FriendlyByteBuf buf) {
        this.source = PacketUtils.readVec3(buf);
        this.dest = PacketUtils.readVec3(buf);
    }

    public void write(FriendlyByteBuf buf){
        PacketUtils.writeVec3(buf, this.source);
        PacketUtils.writeVec3(buf, this.dest);
    }

    public void handle(NetworkEvent.Context ctx) {
        ctx.enqueueWork(() -> {
            Minecraft client = Minecraft.getInstance();
            ClientLevel level = client.level;
            if (level == null) return;
            if (!FMLEnvironment.dist.isClient()) return;

            client.execute(() -> VoidBlossomBlock.handleVoidBlossomHeal(level, source, dest));
        });
        ctx.setPacketHandled(true);
    }
}
