package com.cerbon.bosses_of_mass_destruction.packet.custom;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.PacketUtils;
import com.cerbon.bosses_of_mass_destruction.util.VecId;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.fml.DistExecutor;

import java.util.function.Supplier;

public class SendVec3S2CPacket {
    private final Vec3 pos;
    private final int id;

    public SendVec3S2CPacket(Vec3 pos, int id) {
        this.pos = pos;
        this.id = id;
    }

    public SendVec3S2CPacket(FriendlyByteBuf buf) {
        this.pos = PacketUtils.readVec3(buf);
        this.id = buf.readInt();
    }

    public void write(FriendlyByteBuf buf){
        PacketUtils.writeVec3(buf, this.pos);
        buf.writeInt(id);
    }

    public void handle(Supplier<CustomPayloadEvent.Context> supplier){
        CustomPayloadEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            Minecraft client = Minecraft.getInstance();
            ClientLevel level = client.level;
            VecId vecId = VecId.fromInt(id);

            if (level == null) return;
            if (vecId == null) return;

            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> client.execute(() -> vecId.effectHandler.get().clientHandler(level, pos)));
        });
        ctx.setPacketHandled(true);
    }
}
