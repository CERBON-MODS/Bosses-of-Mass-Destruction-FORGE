package com.cerbon.bosses_of_mass_destruction.packet.custom;

import com.cerbon.bosses_of_mass_destruction.block.custom.VoidBlossomBlock;
import com.cerbon.bosses_of_mass_destruction.util.BMDConstants;
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

public class HealS2CPacket implements CustomPacketPayload {
    public static final ResourceLocation IDENTIFIER = ResourceLocation.fromNamespaceAndPath(BMDConstants.MOD_ID, "heal_s2c_packet");
    public static final StreamCodec<RegistryFriendlyByteBuf, HealS2CPacket> CODEC = new StreamCodec<>() {

        @Override
        public void encode(RegistryFriendlyByteBuf buf, HealS2CPacket packet) {
            buf.writeVec3(packet.source);
            buf.writeVec3(packet.dest);
        }

        @Override
        public @NotNull HealS2CPacket decode(RegistryFriendlyByteBuf buf) {
            return new HealS2CPacket(buf.readVec3(), buf.readVec3());
        }
    };

    private final Vec3 source;
    private final Vec3 dest;

    public HealS2CPacket(Vec3 source, Vec3 dest) {
        this.source = source;
        this.dest = dest;
    }

    public static void handle(PacketContext<HealS2CPacket> ctx) {
        if (ctx.side().equals(Side.SERVER)) return;

        HealS2CPacket packet = ctx.message();

        Minecraft client = Minecraft.getInstance();
        ClientLevel level = client.level;
        if (level == null) return;

        client.execute(() -> VoidBlossomBlock.handleVoidBlossomHeal(level, packet.source, packet.dest));
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return Network.getType(IDENTIFIER);
    }
}