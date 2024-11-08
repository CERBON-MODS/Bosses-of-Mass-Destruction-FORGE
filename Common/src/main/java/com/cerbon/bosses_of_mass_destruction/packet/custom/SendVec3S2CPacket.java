package com.cerbon.bosses_of_mass_destruction.packet.custom;

import com.cerbon.bosses_of_mass_destruction.util.VecId;
import com.cerbon.cerbons_api.api.network.data.PacketContext;
import com.cerbon.cerbons_api.api.network.data.Side;
import com.cerbon.cerbons_api.api.static_utilities.PacketUtils;
import com.cerbon.cerbons_api.util.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public class SendVec3S2CPacket {
    public static final ResourceLocation CHANNEL = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "send_vec3_s2c_packet");
    public static final StreamCodec<FriendlyByteBuf, SendVec3S2CPacket> STREAM_CODEC = StreamCodec.ofMember(SendVec3S2CPacket::write, SendVec3S2CPacket::new);

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

    public void write(FriendlyByteBuf buf) {
        PacketUtils.writeVec3(buf, this.pos);
        buf.writeInt(id);
    }

    public static void handle(PacketContext<SendVec3S2CPacket> ctx) {
        if (ctx.side().equals(Side.SERVER)) return;

        SendVec3S2CPacket packet = ctx.message();

        Minecraft client = Minecraft.getInstance();
        ClientLevel level = client.level;
        VecId vecId = VecId.fromInt(packet.id);

        if (level == null) return;
        if (vecId == null) return;

        client.execute(() -> vecId.effectHandler.get().clientHandler(level, packet.pos));
    }

    public static CustomPacketPayload.Type<CustomPacketPayload> type() {
        return new CustomPacketPayload.Type<>(CHANNEL);
    }
}