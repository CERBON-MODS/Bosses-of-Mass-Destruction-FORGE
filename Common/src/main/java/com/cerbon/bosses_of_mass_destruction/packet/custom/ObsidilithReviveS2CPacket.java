package com.cerbon.bosses_of_mass_destruction.packet.custom;

import com.cerbon.bosses_of_mass_destruction.structure.structure_repair.ObsidilithStructureRepair;
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

public class ObsidilithReviveS2CPacket implements CustomPacketPayload {
    public static final ResourceLocation IDENTIFIER = ResourceLocation.fromNamespaceAndPath(BMDConstants.MOD_ID, "obsidilith_revive_s2c_packet");
    public static final StreamCodec<RegistryFriendlyByteBuf, ObsidilithReviveS2CPacket> CODEC = new StreamCodec<>() {

        @Override
        public void encode(RegistryFriendlyByteBuf buf, ObsidilithReviveS2CPacket packet) {
            buf.writeVec3(packet.pos);
        }

        @Override
        public @NotNull ObsidilithReviveS2CPacket decode(RegistryFriendlyByteBuf buf) {
            return new ObsidilithReviveS2CPacket(buf.readVec3());
        }
    };

    private final Vec3 pos;

    public ObsidilithReviveS2CPacket(Vec3 pos) {
        this.pos = pos;
    }

    public static void handle(PacketContext<ObsidilithReviveS2CPacket> ctx) {
        if (ctx.side().equals(Side.SERVER)) return;

        ObsidilithReviveS2CPacket packet = ctx.message();

        Minecraft client = Minecraft.getInstance();
        ClientLevel level = client.level;
        if (level == null) return;

        client.execute(() -> ObsidilithStructureRepair.handleObsidilithRevivePacket(packet.pos, level));
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return Network.getType(IDENTIFIER);
    }
}
