package com.cerbon.bosses_of_mass_destruction.packet.custom;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.PacketUtils;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.VecUtils;
import com.cerbon.bosses_of_mass_destruction.block.custom.VoidLilyBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SendParticleS2CPacket {
    private final BlockPos pos;
    private final Vector3d dir;

    public SendParticleS2CPacket(BlockPos pos, Vector3d dir) {
        this.pos = pos;
        this.dir = dir;
    }

    public SendParticleS2CPacket(PacketBuffer buf) {
        this.pos = buf.readBlockPos();
        this.dir = PacketUtils.readVec3(buf);
    }

    public void write(PacketBuffer buf){
        buf.writeBlockPos(this.pos);
        PacketUtils.writeVec3(buf, dir);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier){
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            Minecraft client = Minecraft.getInstance();
            ClientWorld level = client.level;
            if (level == null) return;

            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> client.execute(() -> VoidLilyBlockEntity.spawnVoidLilyParticles(level, VecUtils.asVec3(this.pos), dir)));
        });
        ctx.setPacketHandled(true);
    }
}
