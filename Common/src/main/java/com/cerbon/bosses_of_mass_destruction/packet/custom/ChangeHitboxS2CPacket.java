package com.cerbon.bosses_of_mass_destruction.packet.custom;

import com.cerbon.bosses_of_mass_destruction.entity.custom.gauntlet.GauntletEntity;
import com.cerbon.bosses_of_mass_destruction.util.BMDConstants;
import com.cerbon.cerbons_api.api.network.Network;
import com.cerbon.cerbons_api.api.network.data.PacketContext;
import com.cerbon.cerbons_api.api.network.data.Side;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class ChangeHitboxS2CPacket implements CustomPacketPayload {
    public static final ResourceLocation IDENTIFIER = ResourceLocation.fromNamespaceAndPath(BMDConstants.MOD_ID, "change_hitbox_s2c_packet");
    public static final StreamCodec<RegistryFriendlyByteBuf, ChangeHitboxS2CPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, packet -> packet.entityId,
            ByteBufCodecs.BOOL, packet -> packet.open,
            ChangeHitboxS2CPacket::new
    );

    private final int entityId;
    private final boolean open;

    public ChangeHitboxS2CPacket(int entityId, boolean open) {
        this.entityId = entityId;
        this.open = open;
    }

    public static void handle(PacketContext<ChangeHitboxS2CPacket> ctx) {
        if (ctx.side().equals(Side.SERVER)) return;

        ChangeHitboxS2CPacket packet = ctx.message();

        Minecraft client = Minecraft.getInstance();
        ClientLevel level = client.level;
        if (level == null) return;

        client.execute(() -> {
            Entity entity = level.getEntity(packet.entityId);

            if (entity instanceof GauntletEntity gauntletEntity) {
                if (packet.open) gauntletEntity.hitboxHelper.setOpenHandHitbox();
                else gauntletEntity.hitboxHelper.setClosedFistHitbox();
            }
        });
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return Network.getType(IDENTIFIER);
    }
}