package com.cerbon.bosses_of_mass_destruction.packet.custom;

import com.cerbon.bosses_of_mass_destruction.util.BMDConstants;
import com.cerbon.bosses_of_mass_destruction.util.VecId;
import com.cerbon.cerbons_api.api.network.Network;
import com.cerbon.cerbons_api.api.network.data.PacketContext;
import com.cerbon.cerbons_api.api.network.data.Side;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class SendVec3S2CPacket implements CustomPacketPayload {
    public static final ResourceLocation IDENTIFIER = ResourceLocation.fromNamespaceAndPath(BMDConstants.MOD_ID, "send_vec3_s2c_packet");
    public static final StreamCodec<RegistryFriendlyByteBuf, SendVec3S2CPacket> CODEC = new StreamCodec<RegistryFriendlyByteBuf, SendVec3S2CPacket>() {

        @Override
        public void encode(RegistryFriendlyByteBuf buf, SendVec3S2CPacket packet) {
            buf.writeVec3(packet.pos);
            buf.writeInt(packet.id);
        }

        @Override
        public @NotNull SendVec3S2CPacket decode(RegistryFriendlyByteBuf buf) {
            return new SendVec3S2CPacket(buf.readVec3(), buf.readInt());
        }
    };

    private final Vec3 pos;
    private final int id;

    public SendVec3S2CPacket(Vec3 pos, int id) {
        this.pos = pos;
        this.id = id;
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

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return Network.getType(IDENTIFIER);
    }
}