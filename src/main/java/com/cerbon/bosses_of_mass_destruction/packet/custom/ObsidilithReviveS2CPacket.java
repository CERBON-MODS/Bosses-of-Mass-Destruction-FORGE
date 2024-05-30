package com.cerbon.bosses_of_mass_destruction.packet.custom;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.PacketUtils;
import com.cerbon.bosses_of_mass_destruction.structure.structure_repair.ObsidilithStructureRepair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class ObsidilithReviveS2CPacket {
    private final Vector3d pos;

    public ObsidilithReviveS2CPacket(Vector3d pos) {
        this.pos = pos;
    }

    public ObsidilithReviveS2CPacket(PacketBuffer buf) {
        this.pos = PacketUtils.readVec3(buf);
    }

    public void write(PacketBuffer buf){
        PacketUtils.writeVec3(buf, this.pos);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier){
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            Minecraft client = Minecraft.getInstance();
            ClientWorld level = client.level;
            if (level == null) return;

            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> client.execute(() -> ObsidilithStructureRepair.handleObsidilithRevivePacket(pos, level)));
        });
        ctx.setPacketHandled(true);
    }
}
