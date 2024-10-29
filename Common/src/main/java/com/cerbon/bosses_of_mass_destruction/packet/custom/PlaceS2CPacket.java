package com.cerbon.bosses_of_mass_destruction.packet.custom;

import com.cerbon.bosses_of_mass_destruction.block.custom.VoidBlossomBlock;
import com.cerbon.bosses_of_mass_destruction.util.BMDConstants;
import com.cerbon.cerbons_api.api.network.Network;
import com.cerbon.cerbons_api.api.network.data.PacketContext;
import com.cerbon.cerbons_api.api.network.data.Side;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public class PlaceS2CPacket implements CustomPacketPayload {
    public static final ResourceLocation IDENTIFIER = ResourceLocation.fromNamespaceAndPath(BMDConstants.MOD_ID, "place_s2c_packet");
    public static final StreamCodec<RegistryFriendlyByteBuf, PlaceS2CPacket> CODEC = new StreamCodec<>() {

        @Override
        public void encode(RegistryFriendlyByteBuf buf, PlaceS2CPacket packet) {
            buf.writeVec3(packet.pos);
        }

        @Override
        public PlaceS2CPacket decode(RegistryFriendlyByteBuf buf) {
            return new PlaceS2CPacket(buf.readVec3());
        }
    };

    private final Vec3 pos;

    public PlaceS2CPacket(Vec3 pos) {
        this.pos = pos;
    }

    public static void handle(PacketContext<PlaceS2CPacket> ctx) {
        if (ctx.side().equals(Side.SERVER)) return;

        PlaceS2CPacket packet = ctx.message();
        Minecraft client = Minecraft.getInstance();

        client.execute(() -> VoidBlossomBlock.handleVoidBlossomPlace(packet.pos));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return Network.getType(IDENTIFIER);
    }
}