package com.cerbon.bosses_of_mass_destruction.packet.custom;

import com.cerbon.bosses_of_mass_destruction.item.custom.ChargedEnderPearlEntity;
import com.cerbon.cerbons_api.api.network.data.PacketContext;
import com.cerbon.cerbons_api.api.network.data.Side;
import com.cerbon.cerbons_api.api.static_utilities.PacketUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;

public class ChargedEnderPearlS2CPacket {
    private final Vec3 pos;

    public ChargedEnderPearlS2CPacket(Vec3 pos) {
        this.pos = pos;
    }

    public ChargedEnderPearlS2CPacket(FriendlyByteBuf buf) {
        this.pos = PacketUtils.readVec3(buf);
    }

    public void write(FriendlyByteBuf buf) {
        PacketUtils.writeVec3(buf, this.pos);
    }

    public static void handle(PacketContext<ChargedEnderPearlS2CPacket> ctx) {
        if (ctx.side().equals(Side.SERVER)) return;

        ChargedEnderPearlS2CPacket packet = ctx.message();
        Minecraft client = Minecraft.getInstance();

        client.execute(() -> ChargedEnderPearlEntity.handlePearlImpact(packet.pos));
    }
}
