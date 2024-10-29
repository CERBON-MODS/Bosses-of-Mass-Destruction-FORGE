package com.cerbon.bosses_of_mass_destruction.packet.custom;

import com.cerbon.bosses_of_mass_destruction.util.BMDConstants;
import com.cerbon.cerbons_api.api.network.Network;
import com.cerbon.cerbons_api.api.network.data.PacketContext;
import com.cerbon.cerbons_api.api.network.data.Side;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class SendDeltaMovementS2CPacket implements CustomPacketPayload {
    public static final ResourceLocation IDENTIFIER = ResourceLocation.fromNamespaceAndPath(BMDConstants.MOD_ID, "send_delta_movement_s2c_packet");
    public static final StreamCodec<RegistryFriendlyByteBuf, SendDeltaMovementS2CPacket> CODEC = new StreamCodec<RegistryFriendlyByteBuf, SendDeltaMovementS2CPacket>() {

        @Override
        public void encode(RegistryFriendlyByteBuf buf, SendDeltaMovementS2CPacket packet) {
            buf.writeVec3(packet.deltaMovement);
        }

        @Override
        public @NotNull SendDeltaMovementS2CPacket decode(RegistryFriendlyByteBuf buf) {
            return new SendDeltaMovementS2CPacket(buf.readVec3());
        }
    };

    private final Vec3 deltaMovement;

    public SendDeltaMovementS2CPacket(Vec3 deltaMovement) {
        this.deltaMovement = deltaMovement;
    }

    public static void handle(PacketContext<SendDeltaMovementS2CPacket> ctx) {
        if (ctx.side().equals(Side.SERVER)) return;

        SendDeltaMovementS2CPacket packet = ctx.message();

        Minecraft client = Minecraft.getInstance();
        LocalPlayer localPlayer = client.player;
        if (localPlayer == null) return;

        client.execute(() -> localPlayer.setDeltaMovement(packet.deltaMovement));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return Network.getType(IDENTIFIER);
    }
}