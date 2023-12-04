package com.cerbon.bosses_of_mass_destruction.packet.custom;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.PacketUtils;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.VecUtils;
import com.cerbon.bosses_of_mass_destruction.block.custom.VoidLilyBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.fml.DistExecutor;

import java.util.function.Supplier;

public class SendParticleS2CPacket {
    private final BlockPos pos;
    private final Vec3 dir;

    public SendParticleS2CPacket(BlockPos pos, Vec3 dir) {
        this.pos = pos;
        this.dir = dir;
    }

    public SendParticleS2CPacket(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.dir = PacketUtils.readVec3(buf);
    }

    public void write(FriendlyByteBuf buf){
        buf.writeBlockPos(this.pos);
        PacketUtils.writeVec3(buf, dir);
    }

    public void handle(Supplier<CustomPayloadEvent.Context> supplier){
        CustomPayloadEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            Minecraft client = Minecraft.getInstance();
            ClientLevel level = client.level;
            if (level == null) return;

            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> client.execute(() -> VoidLilyBlockEntity.spawnVoidLilyParticles(level, VecUtils.asVec3(this.pos), dir)));
        });
        ctx.setPacketHandled(true);
    }
}
