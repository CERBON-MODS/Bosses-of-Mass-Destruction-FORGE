package com.cerbon.bosses_of_mass_destruction.packet.custom;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.PacketUtils;
import com.cerbon.bosses_of_mass_destruction.structure.structure_repair.ObsidilithStructureRepair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.NetworkEvent;

public class ObsidilithReviveS2CPacket {
    private final Vec3 pos;

    public ObsidilithReviveS2CPacket(Vec3 pos) {
        this.pos = pos;
    }

    public ObsidilithReviveS2CPacket(FriendlyByteBuf buf) {
        this.pos = PacketUtils.readVec3(buf);
    }

    public void write(FriendlyByteBuf buf){
        PacketUtils.writeVec3(buf, this.pos);
    }

    public void handle(NetworkEvent.Context ctx) {
        ctx.enqueueWork(() -> {
            Minecraft client = Minecraft.getInstance();
            ClientLevel level = client.level;
            if (level == null) return;
            if (!FMLEnvironment.dist.isClient()) return;

            client.execute(() -> ObsidilithStructureRepair.handleObsidilithRevivePacket(pos, level));
        });
        ctx.setPacketHandled(true);
    }
}
