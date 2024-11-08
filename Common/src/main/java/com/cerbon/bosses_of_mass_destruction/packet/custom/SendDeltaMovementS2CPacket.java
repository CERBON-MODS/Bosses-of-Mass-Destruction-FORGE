package com.cerbon.bosses_of_mass_destruction.packet.custom;

import com.cerbon.cerbons_api.api.network.data.PacketContext;
import com.cerbon.cerbons_api.api.network.data.Side;
import com.cerbon.cerbons_api.api.static_utilities.PacketUtils;
import com.cerbon.cerbons_api.util.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public class SendDeltaMovementS2CPacket {
    public static final ResourceLocation CHANNEL = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "send_delta_movement_s2c_packet");
    public static final StreamCodec<FriendlyByteBuf, SendDeltaMovementS2CPacket> STREAM_CODEC = StreamCodec.ofMember(SendDeltaMovementS2CPacket::write, SendDeltaMovementS2CPacket::new);

    private final Vec3 deltaMovement;

    public SendDeltaMovementS2CPacket(Vec3 deltaMovement) {
        this.deltaMovement = deltaMovement;
    }

    public SendDeltaMovementS2CPacket(FriendlyByteBuf buf) {
        this.deltaMovement = PacketUtils.readVec3(buf);
    }

    public void write(FriendlyByteBuf buf) {
        PacketUtils.writeVec3(buf, this.deltaMovement);
    }

    public static void handle(PacketContext<SendDeltaMovementS2CPacket> ctx) {
        if (ctx.side().equals(Side.SERVER)) return;

        SendDeltaMovementS2CPacket packet = ctx.message();

        Minecraft client = Minecraft.getInstance();
        LocalPlayer localPlayer = client.player;
        if (localPlayer == null) return;

        client.execute(() -> localPlayer.setDeltaMovement(packet.deltaMovement));
    }

    public static CustomPacketPayload.Type<CustomPacketPayload> type() {
        return new CustomPacketPayload.Type<>(CHANNEL);
    }
}