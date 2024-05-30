package com.cerbon.bosses_of_mass_destruction.packet.custom;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.PacketUtils;
import com.cerbon.bosses_of_mass_destruction.util.VecId;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SendVec3S2CPacket {
    private final Vector3d pos;
    private final int id;

    public SendVec3S2CPacket(Vector3d pos, int id) {
        this.pos = pos;
        this.id = id;
    }

    public SendVec3S2CPacket(PacketBuffer buf) {
        this.pos = PacketUtils.readVec3(buf);
        this.id = buf.readInt();
    }

    public void write(PacketBuffer buf){
        PacketUtils.writeVec3(buf, this.pos);
        buf.writeInt(id);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier){
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            Minecraft client = Minecraft.getInstance();
            ClientWorld level = client.level;
            VecId vecId = VecId.fromInt(id);

            if (level == null) return;
            if (vecId == null) return;

            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> client.execute(() -> vecId.effectHandler.get().clientHandler(level, pos)));
        });
        ctx.setPacketHandled(true);
    }
}
