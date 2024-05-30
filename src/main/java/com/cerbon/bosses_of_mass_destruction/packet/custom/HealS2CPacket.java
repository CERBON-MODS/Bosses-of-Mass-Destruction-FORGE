package com.cerbon.bosses_of_mass_destruction.packet.custom;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.PacketUtils;
import com.cerbon.bosses_of_mass_destruction.block.custom.VoidBlossomBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class HealS2CPacket {
    private final Vector3d source;
    private final Vector3d dest;

    public HealS2CPacket(Vector3d source, Vector3d dest) {
        this.source = source;
        this.dest = dest;
    }

    public HealS2CPacket(PacketBuffer buf) {
        this.source = PacketUtils.readVec3(buf);
        this.dest = PacketUtils.readVec3(buf);
    }

    public void write(PacketBuffer buf){
        PacketUtils.writeVec3(buf, this.source);
        PacketUtils.writeVec3(buf, this.dest);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier){
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            Minecraft client = Minecraft.getInstance();
            ClientWorld level = client.level;
            if (level == null) return;

            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> client.execute(() -> VoidBlossomBlock.handleVoidBlossomHeal(level, source, dest)));
        });
        ctx.setPacketHandled(true);
    }
}
