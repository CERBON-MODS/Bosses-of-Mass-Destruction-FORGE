package com.cerbon.bosses_of_mass_destruction.packet.custom;

import com.cerbon.bosses_of_mass_destruction.block.custom.VoidLilyBlockEntity;
import com.cerbon.cerbons_api.api.network.data.PacketContext;
import com.cerbon.cerbons_api.api.network.data.Side;
import com.cerbon.cerbons_api.api.static_utilities.PacketUtils;
import com.cerbon.cerbons_api.api.static_utilities.VecUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;

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

    public void write(FriendlyByteBuf buf) {
        buf.writeBlockPos(this.pos);
        PacketUtils.writeVec3(buf, dir);
    }

    public static void handle(PacketContext<SendParticleS2CPacket> ctx) {
        if (ctx.side().equals(Side.SERVER)) return;

        SendParticleS2CPacket packet = ctx.message();

        Minecraft client = Minecraft.getInstance();
        ClientLevel level = client.level;
        if (level == null) return;

        client.execute(() -> VoidLilyBlockEntity.spawnVoidLilyParticles(level, VecUtils.asVec3(packet.pos), packet.dir));
    }
}