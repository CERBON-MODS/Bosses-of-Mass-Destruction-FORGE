package com.cerbon.bosses_of_mass_destruction.packet.custom;

import com.cerbon.bosses_of_mass_destruction.structure.structure_repair.VoidBlossomStructureRepair;
import com.cerbon.cerbons_api.api.network.data.PacketContext;
import com.cerbon.cerbons_api.api.network.data.Side;
import com.cerbon.cerbons_api.api.static_utilities.PacketUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;

public class VoidBlossomReviveS2CPacket {
    private final Vec3 pos;

    public VoidBlossomReviveS2CPacket(Vec3 pos) {
        this.pos = pos;
    }

    public VoidBlossomReviveS2CPacket(FriendlyByteBuf buf) {
        this.pos = PacketUtils.readVec3(buf);
    }

    public void write(FriendlyByteBuf buf) {
        PacketUtils.writeVec3(buf, this.pos);
    }

    public static void handle(PacketContext<VoidBlossomReviveS2CPacket> ctx) {
        if (ctx.side().equals(Side.SERVER)) return;

        VoidBlossomReviveS2CPacket packet = ctx.message();

        Minecraft client = Minecraft.getInstance();
        ClientLevel level = client.level;
        if (level == null) return;

        client.execute(() -> VoidBlossomStructureRepair.handleVoidBlossomRevivePacket(packet.pos, level));
    }
}