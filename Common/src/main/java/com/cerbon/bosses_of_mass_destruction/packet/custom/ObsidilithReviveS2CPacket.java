package com.cerbon.bosses_of_mass_destruction.packet.custom;

import com.cerbon.bosses_of_mass_destruction.structure.structure_repair.ObsidilithStructureRepair;
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

public class ObsidilithReviveS2CPacket {
    public static final ResourceLocation CHANNEL = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "obsidilith_revive_s2c_packet");
    public static final StreamCodec<FriendlyByteBuf, ObsidilithReviveS2CPacket> STREAM_CODEC = StreamCodec.ofMember(ObsidilithReviveS2CPacket::write, ObsidilithReviveS2CPacket::new);

    private final Vec3 pos;

    public ObsidilithReviveS2CPacket(Vec3 pos) {
        this.pos = pos;
    }

    public ObsidilithReviveS2CPacket(FriendlyByteBuf buf) {
        this.pos = PacketUtils.readVec3(buf);
    }

    public void write(FriendlyByteBuf buf) {
        PacketUtils.writeVec3(buf, this.pos);
    }

    public static void handle(PacketContext<ObsidilithReviveS2CPacket> ctx) {
        if (ctx.side().equals(Side.SERVER)) return;

        ObsidilithReviveS2CPacket packet = ctx.message();

        Minecraft client = Minecraft.getInstance();
        ClientLevel level = client.level;
        if (level == null) return;

        client.execute(() -> ObsidilithStructureRepair.handleObsidilithRevivePacket(packet.pos, level));
    }

    public static CustomPacketPayload.Type<CustomPacketPayload> type() {
        return new CustomPacketPayload.Type<>(CHANNEL);
    }
}